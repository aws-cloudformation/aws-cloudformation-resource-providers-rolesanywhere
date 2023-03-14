package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rolesanywhere.model.CreateTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.CreateTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DeleteTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.DisableTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.GetTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.GetTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTrustAnchorsRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTrustAnchorsResponse;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereRequest;
import software.amazon.awssdk.services.rolesanywhere.model.RolesAnywhereResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ResourceNotFoundException;
import software.amazon.awssdk.services.rolesanywhere.model.SourceData;
import software.amazon.awssdk.services.rolesanywhere.model.Source;
import software.amazon.awssdk.services.rolesanywhere.RolesAnywhereClient;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ValidationException;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.exceptions.CfnNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import software.amazon.awssdk.services.rolesanywhere.model.Tag;

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

        if (null == model.getSource()) {
            throw new CfnInvalidRequestException("Source must be set.");
        }

        if (null == model.getSource().getSourceData()) {
            throw new CfnInvalidRequestException("SourceData must be set.");
        }

        if (null == model.getSource().getSourceType()) {
            throw new CfnInvalidRequestException("SourceType must be set.");
        }
    }

    public Source translateModelToSource(final ResourceModel model) {
        SourceData sourceData = SourceData.builder()
                .acmPcaArn(model.getSource().getSourceData().getAcmPcaArn())
                .x509CertificateData(model.getSource().getSourceData().getX509CertificateData())
                .build();

        Source source = Source.builder()
                .sourceData(sourceData)
                .sourceType(model.getSource().getSourceType())
                .build();

        return source;
    }

    public CreateTrustAnchorResponse createTrustAnchor(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();
        Set<Tag> tagsToCreate = TagUtils.generateTagsForCreate(model, request);

        Source source = translateModelToSource(model);

        CreateTrustAnchorRequest createTrustAnchorRequest = CreateTrustAnchorRequest.builder()
                .enabled(model.getEnabled())
                .name(model.getName())
                .source(source)
                .tags(tagsToCreate)
                .build();

        logger.log("Attempting create trustanchor resource.");
        return this.invoke(createTrustAnchorRequest, rolesanywhereClient::createTrustAnchor);
    }

    public GetTrustAnchorResponse getTrustAnchor(final ResourceModel model) {
        GetTrustAnchorRequest request = GetTrustAnchorRequest.builder()
                .trustAnchorId(model.getTrustAnchorId())
                .build();

        logger.log("Attempting get for trustanchor resource " + model.getTrustAnchorId());
        return this.invoke(request, rolesanywhereClient::getTrustAnchor);
    }

    public UpdateTrustAnchorResponse updateTrustAnchor(final ResourceHandlerRequest<ResourceModel> request) {
        final ResourceModel model = request.getDesiredResourceState();

        UpdateTrustAnchorRequest updateTrustAnchorRequest = UpdateTrustAnchorRequest.builder()
                .trustAnchorId(model.getTrustAnchorId())
                .name(model.getName())
                .source(translateModelToSource(model))
                .build();

        logger.log("Attempting update for trustAnchor resource " + model.getTrustAnchorId());
        UpdateTrustAnchorResponse updateTrustAnchorResponse = this.invoke(updateTrustAnchorRequest, rolesanywhereClient::updateTrustAnchor);


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
                        .resourceArn(updateTrustAnchorResponse.trustAnchor().trustAnchorArn())
                        .tagKeys(tagKeysToRemove)
                        .build();

                this.invoke(untagResourceRequest, rolesanywhereClient::untagResource);
            }

            if (tagsToCreate.size() > 0) {
                TagResourceRequest tagResourceRequest = TagResourceRequest.builder()
                        .resourceArn(updateTrustAnchorResponse.trustAnchor().trustAnchorArn())
                        .tags(tagsToCreate)
                        .build();

                this.invoke(tagResourceRequest, rolesanywhereClient::tagResource);
            }

        }
        return updateTrustAnchorResponse;
    }

    public DeleteTrustAnchorResponse deleteTrustAnchor(final ResourceModel model) {
        DeleteTrustAnchorRequest request = DeleteTrustAnchorRequest.builder()
                .trustAnchorId(model.getTrustAnchorId())
                .build();

        logger.log("Attempting delete for trustanchor resource " + model.getTrustAnchorId());
        return this.invoke(request, rolesanywhereClient::deleteTrustAnchor);
    }

    public ListTrustAnchorsResponse listTrustAnchor(final ResourceHandlerRequest<ResourceModel> request) {
        ListTrustAnchorsRequest listRequest = ListTrustAnchorsRequest.builder()
                .nextToken(request.getNextToken())
                .pageSize(50)
                .build();

        logger.log("Attempting list for trustanchor resources");
        return this.invoke(listRequest, rolesanywhereClient::listTrustAnchors);
    }

    public EnableTrustAnchorResponse enableTrustAnchor(final ResourceModel model) {
        EnableTrustAnchorRequest request = EnableTrustAnchorRequest.builder()
                .trustAnchorId(model.getTrustAnchorId())
                .build();

        logger.log("Attempting enable for trustanchor resource " + model.getTrustAnchorId());
        return this.invoke(request, rolesanywhereClient::enableTrustAnchor);
    }

    public DisableTrustAnchorResponse disableTrustAnchor(final ResourceModel model) {
        DisableTrustAnchorRequest request = DisableTrustAnchorRequest.builder()
                .trustAnchorId(model.getTrustAnchorId())
                .build();

        logger.log("Attempting disable for trustanchor resource " + model.getTrustAnchorId());
        return this.invoke(request, rolesanywhereClient::disableTrustAnchor);
    }

    public ListTagsForResourceResponse listTagsForResource(final String resourceArn) {
        ListTagsForResourceRequest listTagsRequest = ListTagsForResourceRequest.builder()
                .resourceArn(resourceArn)
                .build();

        logger.log("Attempting list tags for resource " + resourceArn);
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
