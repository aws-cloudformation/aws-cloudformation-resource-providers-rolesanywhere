package software.amazon.rolesanywhere.trustanchor;

import org.assertj.core.api.Assertions;
import software.amazon.awssdk.services.rolesanywhere.model.DisableTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.DisableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.EnableTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.EnableTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.UpdateTrustAnchorResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class UpdateHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trustAnchorId(testTrustAnchorId)
                .name(testTrustAnchorName)
                .source(BaseHandlerUtils.sourceToModelSource(testSource))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .region(testRegion)
            .build();

        doReturn(UpdateTrustAnchorResponse.builder().trustAnchor(testTrustAnchor).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(UpdateTrustAnchorRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(BaseHandlerUtils.modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
            = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        Assertions.assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getTrustAnchorId()).isEqualTo(request.getDesiredResourceState().getTrustAnchorId());
        assertThat(response.getResourceModel().getName()).isEqualTo(testTrustAnchorName);
        assertThat(response.getResourceModel().getSource()).isEqualTo(BaseHandlerUtils.sourceToModelSource(testSource));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleEnableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trustAnchorId(testTrustAnchorId)
                .enabled(true)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .build();

        doReturn(EnableTrustAnchorResponse.builder().trustAnchor(testTrustAnchor).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(EnableTrustAnchorRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        Assertions.assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getTrustAnchorId()).isEqualTo(request.getDesiredResourceState().getTrustAnchorId());
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(true);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleDisableRequest_SimpleSuccess() {
        final UpdateHandler handler = new UpdateHandler();

        final ResourceModel model = ResourceModel.builder()
                .trustAnchorId(testTrustAnchorId)
                .enabled(false)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .build();

        doReturn(DisableTrustAnchorResponse.builder().trustAnchor(testTrustAnchor).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(DisableTrustAnchorRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response
                = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        Assertions.assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModel().getTrustAnchorId()).isEqualTo(request.getDesiredResourceState().getTrustAnchorId());
        assertThat(response.getResourceModel().getEnabled()).isNotEqualTo(request.getDesiredResourceState().getEnabled());

    }
}
