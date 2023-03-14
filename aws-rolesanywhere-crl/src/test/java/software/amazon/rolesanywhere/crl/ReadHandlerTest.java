package software.amazon.rolesanywhere.crl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rolesanywhere.model.GetCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.GetCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.modelTagToTag;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ReadHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final ReadHandler handler = new ReadHandler();

        final ResourceModel model = ResourceModel.builder()
                .crlId(testCrlId)
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .build();

        doReturn(GetCrlResponse.builder().crl(testCrl).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(GetCrlRequest.class), any(Function.class));

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
        assertThat(response.getResourceModel().getTrustAnchorArn()).isEqualTo(testTrustAnchorArn);
        assertThat(response.getResourceModel().getName()).isEqualTo(testName);
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(testEnabled);
        assertThat(response.getResourceModel().getCrlData()).isEqualTo(testCrlData);

    }
}
