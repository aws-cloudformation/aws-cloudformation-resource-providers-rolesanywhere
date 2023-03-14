package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.DeleteCrlResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

//import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.crlDetailToModelCrl;

public class DeleteHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceHandlerRequest<ResourceModel> request,
            final CallbackContext callbackContext,
            final Logger logger) {

        ResourceModel model = request.getDesiredResourceState();

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        return ProgressEvent.defaultSuccessHandler(doDeleteCrl(proxy, model, client, logger));
    }

    private ResourceModel doDeleteCrl(AmazonWebServicesClientProxy proxy,
                                      ResourceModel model,
                                      BaseRolesAnywhereClient client,
                                      Logger logger) {

        DeleteCrlResponse response = client.deleteCrl(model);

        return null;

    }
}