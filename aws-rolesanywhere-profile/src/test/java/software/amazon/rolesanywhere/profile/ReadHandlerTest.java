package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.GetProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.GetProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends BaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .profileId(testProfileId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .region(testRegion)
            .build();

        doReturn(GetProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(GetProfileRequest.class), any(Function.class));

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

        assertThat(response.getResourceModel().getProfileId()).isEqualTo(testProfile.profileId());
        assertThat(response.getResourceModel().getName()).isEqualTo(testProfile.name());
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(testProfile.enabled());
        assertThat(response.getResourceModel().getManagedPolicyArns()).isEqualTo(testProfile.managedPolicyArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(testProfile.roleArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(testProfile.roleArns());
        assertThat(response.getResourceModel().getSessionPolicy()).isEqualTo(testProfile.sessionPolicy());
        assertThat(response.getResourceModel().getDurationSeconds()).isEqualTo(testProfile.durationSeconds().intValue());
        assertThat(response.getResourceModel().getRequireInstanceProperties()).isEqualTo(false);
    }
}
