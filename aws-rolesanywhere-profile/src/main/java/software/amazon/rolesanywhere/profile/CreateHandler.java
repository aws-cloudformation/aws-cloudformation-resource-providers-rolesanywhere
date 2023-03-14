package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.CreateProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.tagListToModelTagList;

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

        CreateProfileResponse response = client.createProfile(request);
        logger.log("apiResponse: " + response);
        ListTagsForResourceResponse tagResponse = client.listTagsForResource(response.profile().profileArn());
        // TODO: removing logging statement below
        logger.log("tagResponse: " + tagResponse);

        final ResourceModel responseResourceModel = ResourceModel.builder()
                .profileId(response.profile().profileId())
                .profileArn(response.profile().profileArn())
                .name(response.profile().name())
                .enabled(response.profile().enabled())
                .durationSeconds(response.profile().durationSeconds().doubleValue())
                .sessionPolicy(response.profile().sessionPolicy())
                .managedPolicyArns(response.profile().managedPolicyArns())
                .roleArns(response.profile().roleArns())
                // reserved for future use
                .requireInstanceProperties(false)
                .build();

        if(request.getDesiredResourceState().getTags() != null ) {
            responseResourceModel.setTags(tagListToModelTagList(tagResponse.tags()));
        }

        return ProgressEvent.defaultSuccessHandler(responseResourceModel);
    }
}
