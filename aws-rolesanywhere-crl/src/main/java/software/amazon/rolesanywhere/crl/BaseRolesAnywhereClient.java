package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.rolesanywhere.RolesAnywhereClient;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereRequest;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereResponse;
import software.amazon.awssdk.services.rolesanywhere.model.Tag;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.DisableCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.GetCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.GetCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ImportCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ImportCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListCrlsRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListCrlsResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ResourceNotFoundException;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.net.URI;
import java.nio.charset.StandardCharsets;
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

        if (null == model.getCrlData()) {
            throw new CfnInvalidRequestException("CrlData must be set.");
        }
    }

    public ImportCrlResponse importCrl(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();
        Set<Tag> tagsToCreate = TagUtils.generateTagsForCreate(model, request);
        ImportCrlRequest importCrlRequest = ImportCrlRequest.builder()
                .name(model.getName())
                .crlData(SdkBytes.fromByteArray(model.getCrlData().getBytes(StandardCharsets.UTF_8)))
                .enabled(model.getEnabled())
                .trustAnchorArn(model.getTrustAnchorArn())
                .tags(tagsToCreate)
                .build();

        logger.log("Attempting crl import");
        return this.invoke(importCrlRequest, rolesanywhereClient::importCrl);
    }

    public GetCrlResponse getCrl(final ResourceModel model) {
        GetCrlRequest request = GetCrlRequest.builder()
                .crlId(model.getCrlId())
                .build();

        logger.log("Attempting get for crl resource " + model.getCrlId());
        return this.invoke(request, rolesanywhereClient::getCrl);
    }

    public DeleteCrlResponse deleteCrl(final ResourceModel model) {
        DeleteCrlRequest request = DeleteCrlRequest.builder()
                .crlId(model.getCrlId())
                .build();

        logger.log("Attempting delete for crl resource " + model.getCrlId());
        return this.invoke(request, rolesanywhereClient::deleteCrl);
    }

    public ListCrlsResponse listCrl(final ResourceHandlerRequest<ResourceModel> request) {
        ListCrlsRequest listRequest = ListCrlsRequest.builder()
                .nextToken(request.getNextToken())
                .pageSize(50)
                .build();

        logger.log("Attempting list for crl resources");
        return this.invoke(listRequest, rolesanywhereClient::listCrls);
    }

    public UpdateCrlResponse updateCrl(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();

        UpdateCrlRequest updateCrlRequest = UpdateCrlRequest.builder()
                .crlId(model.getCrlId())
                .name(model.getName())
                .crlData(SdkBytes.fromByteArray(model.getCrlData().getBytes(StandardCharsets.UTF_8)))
                .build();

        logger.log("Attempting update for crl resource " + model.getCrlId());
        UpdateCrlResponse updateCrlResponse = this.invoke(updateCrlRequest, rolesanywhereClient::updateCrl);


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
                    .resourceArn(updateCrlResponse.crl().crlArn())
                    .tagKeys(tagKeysToRemove)
                    .build();

                this.invoke(untagResourceRequest, rolesanywhereClient::untagResource);
            }

            if (tagsToCreate.size() > 0) {
                TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                        .resourceArn(updateCrlResponse.crl().crlArn())
                        .tags(tagsToCreate)
                        .build();

                this.invoke(tagResourceRequest, rolesanywhereClient::tagResource);
            }

        }
        return updateCrlResponse;
    }

    public EnableCrlResponse enableCrl(final ResourceModel model) {
        EnableCrlRequest request = EnableCrlRequest.builder()
                .crlId(model.getCrlId())
                .build();

        logger.log("Attempting enable for crl resource " + model.getCrlId());
        return this.invoke(request, rolesanywhereClient::enableCrl);
    }

    public DisableCrlResponse disableCrl(final ResourceModel model) {
        DisableCrlRequest request = DisableCrlRequest.builder()
                .crlId(model.getCrlId())
                .build();

        logger.log("Attempting disable for crl resource " + model.getCrlId());
        return this.invoke(request, rolesanywhereClient::disableCrl);
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
