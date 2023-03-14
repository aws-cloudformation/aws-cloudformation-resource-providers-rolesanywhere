package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.DisableProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateProfileResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends BaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void handleUpdateRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        List<Tag> previousResourceTags = Collections.singletonList(Tag.builder()
                .key("Key")
                .value("Value")
                .build());

        final ResourceModel model = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .tags(Collections.singletonList(Tag.builder()
                        .key("UpdatedKey")
                        .value("UpdatedValue")
                        .build()))
                .profileId(testProfileId)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .tags(previousResourceTags)
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .previousResourceTags(TagUtils.convertToMap(BaseHandlerUtils.modelTagListToTagList(previousResourceTags)))
                .region(testRegion)
                .build();

        doReturn(UpdateProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UpdateProfileRequest.class), any(Function.class));

        doReturn(TagResourceResponse.builder().build())
                .when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any(Function.class));

        doReturn(UntagResourceResponse.builder().build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getProfileId()).isNotNull();
        assertThat(response.getResourceModel().getName()).isEqualTo(request.getDesiredResourceState().getName());
        assertThat(response.getResourceModel().getManagedPolicyArns()).isEqualTo(request.getDesiredResourceState().getManagedPolicyArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getSessionPolicy()).isEqualTo(request.getDesiredResourceState().getSessionPolicy());
        assertThat(response.getResourceModel().getDurationSeconds()).isEqualTo(request.getDesiredResourceState().getDurationSeconds());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleEnableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .enabled(true)
                .profileId(testProfileId)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .previousResourceTags(null)
                .region(testRegion)
                .build();

        doReturn(EnableProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(EnableProfileRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getProfileId()).isNotNull();
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(request.getDesiredResourceState().getEnabled());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleDisableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .enabled(false)
                .profileId(testProfileId)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .previousResourceTags(null)
                .region(testRegion)
                .build();

        doReturn(DisableProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(DisableProfileRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);

        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getProfileId()).isEqualTo(request.getDesiredResourceState().getProfileId());
        assertThat(response.getResourceModel().getEnabled()).isNotEqualTo(request.getDesiredResourceState().getEnabled());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleUpdateTagsRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        List<Tag> previousResourceTags = List.of(
                Tag.builder()
                    .key("key1")
                    .value("value1")
                .build(),
                Tag.builder()
                    .key("key2")
                    .value("value2")
                .build());

        List<Tag> desiredResourceTags = List.of(
                Tag.builder()
                        .key("key1")
                        .value("value1")
                        .build(),
                Tag.builder()
                        .key("key3")
                        .value("value3")
                        .build());

        final ResourceModel model = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .tags(desiredResourceTags)
                .profileId(testProfileId)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .tags(previousResourceTags)
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .previousResourceTags(TagUtils.convertToMap(BaseHandlerUtils.modelTagListToTagList(previousResourceTags)))
                .region(testRegion)
                .build();

        doReturn(UpdateProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UpdateProfileRequest.class), any(Function.class));

        doReturn(TagResourceResponse.builder().build())
                .when(proxy).injectCredentialsAndInvokeV2(any(TagResourceRequest.class), any(Function.class));

        doReturn(UntagResourceResponse.builder().build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UntagResourceRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getProfileId()).isNotNull();
        assertThat(response.getResourceModel().getName()).isEqualTo(request.getDesiredResourceState().getName());
        assertThat(response.getResourceModel().getManagedPolicyArns()).isEqualTo(request.getDesiredResourceState().getManagedPolicyArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getSessionPolicy()).isEqualTo(request.getDesiredResourceState().getSessionPolicy());
        assertThat(response.getResourceModel().getDurationSeconds()).isEqualTo(request.getDesiredResourceState().getDurationSeconds());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleUpdateRequest_DurationSecondsNull() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(null)
                .profileId(testProfileId)
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .region(testRegion)
                .build();

        doReturn(UpdateProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UpdateProfileRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getProfileId()).isNotNull();
        assertThat(response.getResourceModel().getName()).isEqualTo(request.getDesiredResourceState().getName());
        assertThat(response.getResourceModel().getManagedPolicyArns()).isEqualTo(request.getDesiredResourceState().getManagedPolicyArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getSessionPolicy()).isEqualTo(request.getDesiredResourceState().getSessionPolicy());
        assertThat(response.getResourceModel().getDurationSeconds()).isEqualTo(previousModel.getDurationSeconds());
        assertThat(response.getResourceModel().getRequireInstanceProperties()).isEqualTo(false);
    }
}
