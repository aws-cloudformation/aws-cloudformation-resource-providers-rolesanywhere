package software.amazon.rolesanywhere.profile;

import software.amazon.awssdk.services.rolesanywhere.model.CreateProfileRequest;
import software.amazon.awssdk.services.rolesanywhere.model.CreateProfileResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;

import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static software.amazon.rolesanywhere.profile.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends BaseTest {

    @SuppressWarnings("unchecked")
    @Test
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .name(testName)
                .requireInstanceProperties(testRequireInstanceProperties)
                .enabled(testEnabled)
                .managedPolicyArns(testManagedPolicyArns)
                .roleArns(testRoleArns)
                .sessionPolicy(testSessionPolicy)
                .durationSeconds(testDurationSeconds)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .systemTags(Map.of())
                .build();

        doReturn(CreateProfileResponse.builder().profile(testProfile).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(CreateProfileRequest.class), any(Function.class));

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

        assertThat(response.getResourceModel().getName()).isEqualTo(request.getDesiredResourceState().getName());
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(request.getDesiredResourceState().getEnabled());
        assertThat(response.getResourceModel().getManagedPolicyArns()).isEqualTo(request.getDesiredResourceState().getManagedPolicyArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getRoleArns()).isEqualTo(request.getDesiredResourceState().getRoleArns());
        assertThat(response.getResourceModel().getSessionPolicy()).isEqualTo(request.getDesiredResourceState().getSessionPolicy());
        assertThat(response.getResourceModel().getDurationSeconds()).isEqualTo(request.getDesiredResourceState().getDurationSeconds());
        assertThat(response.getResourceModel().getRequireInstanceProperties()).isEqualTo(false);
        assertThat(response.getResourceModel().getProfileArn()).isEqualTo(testProfileArn);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void handleRequest_NullValues() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel modelOne = ResourceModel.builder()
                .name(testName)
                .roleArns(null)
                .build();

        final ResourceModel modelTwo = ResourceModel.builder()
                .name(null)
                .roleArns(testRoleArns)
                .build();


        final ResourceHandlerRequest<ResourceModel> requestOne = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(modelOne)
                .region(testRegion)
                .build();

        final ResourceHandlerRequest<ResourceModel> requestTwo = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(modelTwo)
                .region(testRegion)
                .build();

        try {
            handler.handleRequest(proxy, requestOne, null, logger);
            assertThat(true).isEqualTo(false);
        }
        catch (CfnInvalidRequestException e) {
            //no-op
        }

        try {
            handler.handleRequest(proxy, requestTwo, null, logger);
            assertThat(true).isEqualTo(false);
        }
        catch (CfnInvalidRequestException e) {
            //no-op
        }
    }
}
