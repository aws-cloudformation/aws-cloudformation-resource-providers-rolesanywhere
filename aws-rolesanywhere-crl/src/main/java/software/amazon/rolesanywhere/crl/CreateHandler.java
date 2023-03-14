package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.ImportCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.nio.charset.StandardCharsets;

//import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.crlDetailToModelCrl;
import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.tagListToModelTagList;

public class CreateHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);
        client.validateModelParameters(request.getDesiredResourceState());

        ImportCrlResponse response = client.importCrl(request);
        ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.crl().crlArn());
        logger.log("tagResponse: " + tagResponse);

        final ResourceModel responseResourceModel = ResourceModel.builder()
                .crlId(response.crl().crlId())
                .enabled(response.crl().enabled())
                .name(response.crl().name())
                .crlData(response.crl().crlData().asString(StandardCharsets.UTF_8))
                .tags(tagListToModelTagList(tagResponse.tags()))
                .trustAnchorArn(response.crl().trustAnchorArn())
                .build();

        return ProgressEvent.defaultSuccessHandler(responseResourceModel);
    }
}
