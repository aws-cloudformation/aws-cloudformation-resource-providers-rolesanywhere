package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.services.rolesanywhere.model.CreateTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.sourceToModelSource;
import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.tagListToModelTagList;


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

        CreateTrustAnchorResponse response = client.createTrustAnchor(request);
        ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.trustAnchor().trustAnchorArn());

        final ResourceModel responseResourceModel = ResourceModel.builder()
                .trustAnchorId(response.trustAnchor().trustAnchorId())
                .trustAnchorArn(response.trustAnchor().trustAnchorArn())
                .name(response.trustAnchor().name())
                .enabled(response.trustAnchor().enabled())
                .source(sourceToModelSource(response.trustAnchor().source()))
                .tags(tagListToModelTagList(tagResponse.tags()))
                .build();

        return ProgressEvent.defaultSuccessHandler(responseResourceModel);
    }

}
