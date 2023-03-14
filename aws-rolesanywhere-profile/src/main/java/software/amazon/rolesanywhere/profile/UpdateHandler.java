package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.DisableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateProfileResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.tagListToModelTagList;

public class UpdateHandler extends BaseHandler<CallbackContext> {

    private BaseRolesAnywhereClient client;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.client = new BaseRolesAnywhereClient(proxy, logger, request);

        return ProgressEvent.defaultSuccessHandler(doUpdateProfile(proxy, request, this.client, logger));
    }

    private ResourceModel doUpdateProfile(AmazonWebServicesClientProxy proxy,
                                      ResourceHandlerRequest<ResourceModel> request,
                                      BaseRolesAnywhereClient client,
                                      Logger logger) {

        ResourceModel model = request.getDesiredResourceState();
        ResourceModel responseResourceModel = ResourceModel.builder().build();

        boolean requestedEnableOp = model.getEnabled() != null;
        boolean requestedUpdateOp = model.getName() != null
                || model.getRequireInstanceProperties() != null || model.getTags() != null
                || model.getRoleArns() != null
                || model.getDurationSeconds() != null || model.getManagedPolicyArns() != null;

        if (requestedEnableOp) {
            if (model.getEnabled()) {
                EnableProfileResponse response = client.enableProfile(model);
                ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.profile().profileArn());
                responseResourceModel = ResourceModel.builder()
                        .profileId(response.profile().profileId())
                        .profileArn(response.profile().profileArn())
                        .enabled(response.profile().enabled())
                        .durationSeconds(response.profile().durationSeconds().doubleValue())
                        .managedPolicyArns(response.profile().managedPolicyArns())
                        .sessionPolicy(response.profile().sessionPolicy())
                        .roleArns(response.profile().roleArns())
                        // reserved for future use
                        .requireInstanceProperties(false)
                        .tags(tagListToModelTagList(tagResponse.tags()))
                        .name(response.profile().name())
                        .build();
            }
            else if (!model.getEnabled()) {
                DisableProfileResponse response = client.disableProfile(model);
                ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.profile().profileArn());
                responseResourceModel = ResourceModel.builder()
                        .profileId(response.profile().profileId())
                        .profileArn(response.profile().profileArn())
                        .enabled(response.profile().enabled())
                        .durationSeconds(response.profile().durationSeconds().doubleValue())
                        .managedPolicyArns(response.profile().managedPolicyArns())
                        .sessionPolicy(response.profile().sessionPolicy())
                        .roleArns(response.profile().roleArns())
                        .requireInstanceProperties(response.profile().requireInstanceProperties())
                        .tags(tagListToModelTagList(tagResponse.tags()))
                        .name(response.profile().name())
                        .build();
            }
        }
        if (requestedUpdateOp) {
            UpdateProfileResponse response = client.updateProfile(request);
            ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.profile().profileArn());
            responseResourceModel = ResourceModel.builder()
                    .profileId(response.profile().profileId())
                    .profileArn(response.profile().profileArn())
                    .enabled(response.profile().enabled())
                    .sessionPolicy(response.profile().sessionPolicy())
                    .durationSeconds(response.profile().durationSeconds().doubleValue())
                    .managedPolicyArns(response.profile().managedPolicyArns())
                    .roleArns(response.profile().roleArns())
                    .requireInstanceProperties(response.profile().requireInstanceProperties())
                    .tags(tagListToModelTagList(tagResponse.tags()))
                    .name(response.profile().name())
                    .build();
        }

        return responseResourceModel;

    }
}
