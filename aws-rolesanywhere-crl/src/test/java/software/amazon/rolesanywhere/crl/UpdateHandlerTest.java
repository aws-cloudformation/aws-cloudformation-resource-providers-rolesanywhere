package software.amazon.rolesanywhere.crl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rolesanywhere.model.DisableCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.TagResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UntagResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateCrlResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Arrays;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.modelTagToTag;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleUpdateRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .crlId(testCrlId)
                .name(testName)
                .crlData(testCrlData)
                .trustAnchorArn(testTrustAnchorArn)
                .tags(Arrays.asList(testTag1, testTag3))
                .build();

        final ResourceModel previousModel = ResourceModel.builder()
                .crlId(testCrlId)
                .name(testName)
                .crlData(testCrlData)
                .trustAnchorArn(testTrustAnchorArn)
                .tags(Arrays.asList(testTag1, testTag2))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .previousResourceState(previousModel)
                .region(testRegion)
                .build();

        doReturn(UpdateCrlResponse.builder().crl(testCrl).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UpdateCrlRequest.class), any(Function.class));

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

        assertThat(response.getResourceModel().getCrlId()).isEqualTo(request.getDesiredResourceState().getCrlId());
        assertThat(response.getResourceModel().getName()).isEqualTo(testName);
        assertThat(response.getResourceModel().getCrlData()).isEqualTo(testCrlData);
        assertThat(response.getResourceModel().getTrustAnchorArn()).isEqualTo(request.getDesiredResourceState().getTrustAnchorArn());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleEnableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .crlId(testCrlId)
                .enabled(true)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .build();

        doReturn(EnableCrlResponse.builder().crl(testCrl).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(EnableCrlRequest.class), any(Function.class));

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

        assertThat(response.getResourceModel().getCrlId()).isEqualTo(request.getDesiredResourceState().getCrlId());
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(true);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleDisableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .crlId(testCrlId)
                .enabled(false)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .build();

        doReturn(DisableCrlResponse.builder().crl(testCrl).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(DisableCrlRequest.class), any(Function.class));

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

        assertThat(response.getResourceModel().getCrlId()).isEqualTo(request.getDesiredResourceState().getCrlId());
        assertThat(response.getResourceModel().getEnabled()).isNotEqualTo(request.getDesiredResourceState().getEnabled());

    }
}
