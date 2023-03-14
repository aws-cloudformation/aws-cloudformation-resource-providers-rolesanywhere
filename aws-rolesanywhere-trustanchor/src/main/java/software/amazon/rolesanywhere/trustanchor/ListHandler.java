package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.services.rolesanywhere.model.ListTrustAnchorsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.sourceToModelSource;
import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.tagListToModelTagList;

public class ListHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        ListTrustAnchorsResponse response = client.listTrustAnchor(request);

        final List<ResourceModel> models = response.trustAnchors().stream()
                .map(trustAnchor -> ResourceModel.builder()
                        .trustAnchorId(trustAnchor.trustAnchorId())
                        .trustAnchorArn(trustAnchor.trustAnchorArn())
                        .name(trustAnchor.name())
                        .enabled(trustAnchor.enabled())
                        .source(sourceToModelSource(trustAnchor.source()))
                        .tags(tagListToModelTagList(client.listTagsForResource(trustAnchor.trustAnchorArn()).tags()))
                        .build())
                .collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .nextToken(response.nextToken())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
