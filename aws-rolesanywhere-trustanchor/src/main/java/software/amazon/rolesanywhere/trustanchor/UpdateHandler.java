package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.services.rolesanywhere.model.DisableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateTrustAnchorResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.tagListToModelTagList;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
                .resourceModel(doUpdateTrustAnchor(proxy, request, this.client, logger))
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doUpdateTrustAnchor(AmazonWebServicesClientProxy proxy,
                                      ResourceHandlerRequest<ResourceModel> request,
                                      BaseRolesAnywhereClient client,
                                      Logger logger) {

        ResourceModel model = request.getDesiredResourceState();
        ResourceModel responseResourceModel = ResourceModel.builder().build();

        boolean requestedEnableOp = model.getEnabled() != null;
        boolean requestedUpdateOp = model.getName() != null
                || model.getSource() != null || model.getTags() != null;

        if (requestedEnableOp) {
            if (model.getEnabled()) {
                EnableTrustAnchorResponse enableTrustAnchorResponse = client.enableTrustAnchor(model);
                responseResourceModel = ResourceModel.builder()
                        .trustAnchorId(enableTrustAnchorResponse.trustAnchor().trustAnchorId())
                        .trustAnchorArn(enableTrustAnchorResponse.trustAnchor().trustAnchorArn())
                        .enabled(enableTrustAnchorResponse.trustAnchor().enabled())
                        .name(enableTrustAnchorResponse.trustAnchor().name())
                        .source(BaseHandlerUtils.sourceToModelSource(enableTrustAnchorResponse.trustAnchor().source()))
                        .tags(request.getDesiredResourceState().getTags())
                        .build();
            }
            else if (!model.getEnabled()) {
                DisableTrustAnchorResponse disableTrustAnchorResponse = client.disableTrustAnchor(model);
                responseResourceModel = ResourceModel.builder()
                        .trustAnchorId(disableTrustAnchorResponse.trustAnchor().trustAnchorId())
                        .trustAnchorArn(disableTrustAnchorResponse.trustAnchor().trustAnchorArn())
                        .enabled(disableTrustAnchorResponse.trustAnchor().enabled())
                        .name(disableTrustAnchorResponse.trustAnchor().name())
                        .source(BaseHandlerUtils.sourceToModelSource(disableTrustAnchorResponse.trustAnchor().source()))
                        .tags(request.getDesiredResourceState().getTags())
                        .build();
            }
        }
        if (requestedUpdateOp) {
            UpdateTrustAnchorResponse updateTrustAnchorResponse = client.updateTrustAnchor(request);
            ListTagsForResourceResponse tagResponse = client.listTagsForResource(updateTrustAnchorResponse.trustAnchor().trustAnchorArn());
            responseResourceModel = ResourceModel.builder()
                    .trustAnchorId(updateTrustAnchorResponse.trustAnchor().trustAnchorId())
                    .trustAnchorArn(updateTrustAnchorResponse.trustAnchor().trustAnchorArn())
                    .enabled(updateTrustAnchorResponse.trustAnchor().enabled())
                    .name(updateTrustAnchorResponse.trustAnchor().name())
                    .source(BaseHandlerUtils.sourceToModelSource(updateTrustAnchorResponse.trustAnchor().source()))
                    .tags(tagListToModelTagList(tagResponse.tags()))
                    .tags(request.getDesiredResourceState().getTags())
                    .build();
        }
        return responseResourceModel;
    }
}
