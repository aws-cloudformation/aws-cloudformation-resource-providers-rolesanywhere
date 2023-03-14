package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.services.rolesanywhere.model.DeleteTrustAnchorResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ResourceModel model = request.getDesiredResourceState();

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        client.deleteTrustAnchor(model);

        return ProgressEvent.defaultSuccessHandler(null);
    }
}
