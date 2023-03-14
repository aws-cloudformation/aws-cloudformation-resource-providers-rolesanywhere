package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.DisableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateCrlResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.nio.charset.StandardCharsets;

import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.tagListToModelTagList;

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
                .resourceModel(doUpdateCrl(proxy, request, this.client, logger))
                .status(OperationStatus.SUCCESS)
                .build();
    }

    private ResourceModel doUpdateCrl(AmazonWebServicesClientProxy proxy,
                                      ResourceHandlerRequest<ResourceModel> request,
                                      BaseRolesAnywhereClient client,
                                      Logger logger) {

        ResourceModel model = request.getDesiredResourceState();
        ResourceModel responseResourceModel = ResourceModel.builder().build();

        boolean requestedEnableOp = model.getEnabled() != null;
        boolean requestedUpdateOp = model.getName() != null
                || model.getCrlData() != null || model.getTags() != null;

        if (requestedEnableOp) {
            if (model.getEnabled()) {
                EnableCrlResponse enableCrlResponse = client.enableCrl(model);
                ListTagsForResourceResponse tagResponse = client.listTagsForResource(enableCrlResponse.crl().crlArn());
                responseResourceModel = ResourceModel.builder()
                        .crlId(enableCrlResponse.crl().crlId())
                        .enabled(enableCrlResponse.crl().enabled())
                        .name(enableCrlResponse.crl().name())
                        .crlData(enableCrlResponse.crl().crlData().asString(StandardCharsets.UTF_8))
                        .trustAnchorArn(enableCrlResponse.crl().trustAnchorArn())
                        .tags(tagListToModelTagList(tagResponse.tags()))
                        .build();
            }
            else if (!model.getEnabled()) {
                DisableCrlResponse disableCrlResponse = client.disableCrl(model);
                ListTagsForResourceResponse tagResponse = client.listTagsForResource(disableCrlResponse.crl().crlArn());
                responseResourceModel = ResourceModel.builder()
                        .crlId(disableCrlResponse.crl().crlId())
                        .enabled(disableCrlResponse.crl().enabled())
                        .name(disableCrlResponse.crl().name())
                        .crlData(disableCrlResponse.crl().crlData().asString(StandardCharsets.UTF_8))
                        .trustAnchorArn(disableCrlResponse.crl().trustAnchorArn())
                        .tags(tagListToModelTagList(tagResponse.tags()))
                        .build();
            }
        }
        if (requestedUpdateOp) {
            UpdateCrlResponse updateCrlResponse = client.updateCrl(request);
            ListTagsForResourceResponse tagResponse = client.listTagsForResource(updateCrlResponse.crl().crlArn());
            responseResourceModel = ResourceModel.builder()
                    .crlId(updateCrlResponse.crl().crlId())
                    .enabled(updateCrlResponse.crl().enabled())
                    .name(updateCrlResponse.crl().name())
                    .crlData(updateCrlResponse.crl().crlData().asString(StandardCharsets.UTF_8))
                    .trustAnchorArn(updateCrlResponse.crl().trustAnchorArn())
                    .tags(tagListToModelTagList(tagResponse.tags()))
                    .build();
        }

        return responseResourceModel;

    }
}