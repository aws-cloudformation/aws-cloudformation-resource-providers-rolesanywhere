package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.GetCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.nio.charset.StandardCharsets;

import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.tagListToModelTagList;

public class ReadHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModel(doReadCrl(proxy, request, client, logger))
            .status(OperationStatus.SUCCESS)
            .build();
    }

    private ResourceModel doReadCrl(AmazonWebServicesClientProxy proxy,
                                    ResourceHandlerRequest<ResourceModel> request,
                                    BaseRolesAnywhereClient client,
                                    Logger logger) {

        GetCrlResponse response = client.getCrl(request.getDesiredResourceState());

        ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.crl().crlArn());

        final ResourceModel responseResourceModel = ResourceModel.builder()
                .crlId(response.crl().crlId())
                .enabled(response.crl().enabled())
                .name(response.crl().name())
                .crlData(response.crl().crlData().asString(StandardCharsets.UTF_8))
                .trustAnchorArn(response.crl().trustAnchorArn())
                .tags(tagListToModelTagList(tagResponse.tags()))
                .build();

        return responseResourceModel;
    }
}
