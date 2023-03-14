package software.amazon.rolesanywhere.trustanchor;

import org.assertj.core.api.Assertions;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTrustAnchorsRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTrustAnchorsResponse;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final ListHandler handler = new ListHandler();

        final ResourceModel model = ResourceModel.builder().build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .region(testRegion)
            .build();

        doReturn(ListTrustAnchorsResponse.builder()
                .trustAnchors(Collections.singletonList(testTrustAnchor))
                .nextToken(testNextToken)
                .build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTrustAnchorsRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ListTagsForResourceRequest.class), any(Function.class));

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        Assertions.assertThat(response.getResourceModels()).isNotNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();

        assertThat(response.getResourceModels().get(0).getTrustAnchorId()).isEqualTo(testTrustAnchorId);
    }
}
