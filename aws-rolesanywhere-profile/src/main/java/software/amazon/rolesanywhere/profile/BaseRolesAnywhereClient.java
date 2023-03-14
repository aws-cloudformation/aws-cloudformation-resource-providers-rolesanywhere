package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;

import software.amazon.awssdk.services.rolesanywhere.RolesAnywhereClient;
import software.amazon.awssdk.services.rolesanywhere.model.CreateProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.CreateProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.DisableProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.GetProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.GetProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListProfilesRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListProfilesResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereRequest;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ResourceNotFoundException;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import software.amazon.awssdk.services.rolesanywhere.model.Tag;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class BaseRolesAnywhereClient {

    final Logger logger;
    static RolesAnywhereClient rolesanywhereClient;
    private AmazonWebServicesClientProxy clientProxy;

    public BaseRolesAnywhereClient(AmazonWebServicesClientProxy clientProxy, Logger logger, ResourceHandlerRequest<ResourceModel> request) {
        this.clientProxy = clientProxy;
        this.logger = logger;

        rolesanywhereClient = RolesAnywhereClient.builder()
                .region(Region.of(request.getRegion()))
                .build();
    }

    /**
     * Validate the desiredResourceState of an incoming request
     * @param model the desiredResourceState ResourceModel
     */
    public void validateModelParameters(final ResourceModel model) {
        if (null == model.getName()) {
            throw new CfnInvalidRequestException("Name must be set.");
        }

        if (null == model.getRoleArns()) {
            throw new CfnInvalidRequestException("RoleArns must be set.");
        }
    }


    public CreateProfileResponse createProfile(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();
        Set<Tag> tagsToCreate = TagUtils.generateTagsForCreate(model, request);

        Double durationSeconds = model.getDurationSeconds();

        CreateProfileRequest createProfileRequest = CreateProfileRequest.builder()
                .name(model.getName())
                .sessionPolicy(model.getSessionPolicy())
                .requireInstanceProperties(model.getRequireInstanceProperties())
                .managedPolicyArns(model.getManagedPolicyArns())
                .roleArns(model.getRoleArns())
                .durationSeconds(null == durationSeconds ? null : durationSeconds.intValue())
                .enabled(model.getEnabled())
                .tags(tagsToCreate)
                .build();

        logger.log("Attempting profile create.");
        return this.invoke(createProfileRequest, rolesanywhereClient::createProfile);
    }

    public GetProfileResponse getProfile(final ResourceModel model) {
        GetProfileRequest request = GetProfileRequest.builder()
                .profileId(model.getProfileId())
                .build();

        logger.log("Attempting get for profile resource: " + model.getProfileArn());
        return this.invoke(request, rolesanywhereClient::getProfile);
    }

    public DeleteProfileResponse deleteProfile(final ResourceModel model) {
        DeleteProfileRequest request = DeleteProfileRequest.builder()
                .profileId(model.getProfileId())
                .build();

        logger.log("Attempting delete for resource " + model.getProfileArn());
        return this.invoke(request, rolesanywhereClient::deleteProfile);
    }

    public ListProfilesResponse listProfiles(final ResourceHandlerRequest<ResourceModel> request) {
        ListProfilesRequest listRequest = ListProfilesRequest.builder()
                .nextToken(request.getNextToken())
                .pageSize(50)
                .build();

        logger.log("Attempting list for profile resources");
        return this.invoke(listRequest, rolesanywhereClient::listProfiles);
    }

    public UpdateProfileResponse updateProfile(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();

        Double durationSeconds = model.getDurationSeconds();

        UpdateProfileRequest updateProfileRequest = UpdateProfileRequest.builder()
                .profileId(model.getProfileId())
                .name(model.getName())
                .managedPolicyArns(model.getManagedPolicyArns())
                .sessionPolicy(model.getSessionPolicy())
                .durationSeconds(null == durationSeconds ? null : durationSeconds.intValue())
                .roleArns(model.getRoleArns())
                .build();

        logger.log("Attempting update for profile resource " + model.getProfileArn());
        UpdateProfileResponse updateProfileResponse = this.invoke(updateProfileRequest, rolesanywhereClient::updateProfile);

        if (TagUtils.shouldUpdateTags(model, request)) {
            Set<Tag> tagsToRemove = TagUtils.getTagsToRemove(request);
            Set<Tag> tagsToCreate = TagUtils.getTagsToAdd(request);

            Set<String> tagKeysToRemove = new HashSet<>();
            tagsToRemove.stream().forEach(
                    tag -> {
                        tagKeysToRemove.add(tag.key());
                    }
            );
            if (tagsToRemove.size() > 0) {
                UntagResourceRequest untagResourceRequest = UntagResourceRequest.builder()
                        .resourceArn(updateProfileResponse.profile().profileArn())
                        .tagKeys(tagKeysToRemove)
                        .build();

                this.invoke(untagResourceRequest, rolesanywhereClient::untagResource);
            }

            if (tagsToCreate.size() > 0) {
                TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                        .resourceArn(updateProfileResponse.profile().profileArn())
                        .tags(tagsToCreate)
                        .build();

                this.invoke(tagResourceRequest, rolesanywhereClient::tagResource);
            }

        }
        return updateProfileResponse;
    }

    public EnableProfileResponse enableProfile(final ResourceModel model) {
        EnableProfileRequest request = EnableProfileRequest.builder()
                .profileId(model.getProfileId())
                .build();

        logger.log("Attempting enable for profile resource: " + model.getProfileArn());
        return this.invoke(request, rolesanywhereClient::enableProfile);
    }

    public DisableProfileResponse disableProfile(final ResourceModel model) {
        DisableProfileRequest request = DisableProfileRequest.builder()
                .profileId(model.getProfileId())
                .build();

        logger.log("Attempting disable for profile resource: " + model.getProfileArn());
        return this.invoke(request, rolesanywhereClient::disableProfile);
    }

    public ListTagsForResourceResponse listTagsForResource(final String resourceArn) {
        ListTagsForResourceRequest listTagsRequest = ListTagsForResourceRequest.builder()
                .resourceArn(resourceArn)
                .build();

        return this.invoke(listTagsRequest, rolesanywhereClient::listTagsForResource);
    }


    public <RequestT extends RolesAnywhereRequest, ResponseT extends RolesAnywhereResponse> ResponseT
    invoke(RequestT request, Function<RequestT, ResponseT> method) {
        try {
            ResponseT response = clientProxy.injectCredentialsAndInvokeV2(request, method);
            return response;
        } catch (ValidationException e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (ResourceNotFoundException e) {
            logger.log(e.getMessage());
            throw new CfnNotFoundException(e);
        } catch (SdkClientException e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(e);
        } catch (Exception e) {
            logger.log(e.getMessage());
            throw new CfnInvalidRequestException(e);
        }
    }
}
