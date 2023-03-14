package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.ListProfilesResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.List;
import java.util.stream.Collectors;

import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.tagListToModelTagList;

public class ListHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        ListProfilesResponse listProfilesResponse = client.listProfiles(request);

        final List<ResourceModel> models = listProfilesResponse.profiles().stream()
                .map(profile -> ResourceModel.builder()
                        .profileId(profile.profileId())
                        .profileArn(profile.profileArn())
                        // reserved for future use
                        .requireInstanceProperties(false)
                        .managedPolicyArns(profile.managedPolicyArns())
                        .roleArns(profile.roleArns())
                        .durationSeconds(profile.durationSeconds() != null ? profile.durationSeconds().doubleValue() : 3600)
                        .sessionPolicy(profile.sessionPolicy())
                        .enabled(profile.enabled())
                        .tags(tagListToModelTagList(client.listTagsForResource(profile.profileArn()).tags()))
                        .build())
                .collect(Collectors.toList());

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .resourceModels(models)
            .nextToken(listProfilesResponse.nextToken())
            .status(OperationStatus.SUCCESS)
            .build();
    }
}
