package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.ListCrlsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.tagListToModelTagList;

public class ListHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        ListCrlsResponse listCrlsResponse = client.listCrl(request);

        final List<ResourceModel> models = listCrlsResponse.crls().stream()
                .map(crl -> ResourceModel.builder()
                        .crlId(crl.crlId())
                        .enabled(crl.enabled())
                        .name(crl.name())
                        .crlData(crl.crlData().asString(StandardCharsets.UTF_8))
                        .trustAnchorArn(crl.trustAnchorArn())
                        .tags(tagListToModelTagList(client.listTagsForResource(crl.crlArn()).tags()))
                        .build())
                .collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .nextToken(listCrlsResponse.nextToken())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
