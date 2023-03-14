package software.amazon.rolesanywhere.trustanchor;

import org.assertj.core.api.Assertions;
import software.amazon.awssdk.services.rolesanywhere.model.CreateTrustAnchorRequest;
import software.amazon.awssdk.services.rolesanywhere.model.CreateTrustAnchorResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.ArgumentMatchers.any;
import static software.amazon.rolesanywhere.trustanchor.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .name(testTrustAnchorName)
                .enabled(testEnabled)
                .source(BaseHandlerUtils.sourceToModelSource(testSource))
                .tags(List.of(testTag))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .systemTags(Map.of())
                .build();

        doReturn(CreateTrustAnchorResponse.builder().trustAnchor(testTrustAnchor).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(CreateTrustAnchorRequest.class), any(Function.class));

        doReturn(ListTagsForResourceResponse.builder().tags(modelTagToTag(testTag)).build())
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

        assertThat(response.getResourceModel().getName()).isEqualTo(request.getDesiredResourceState().getName());
        assertThat(response.getResourceModel().getEnabled()).isEqualTo(request.getDesiredResourceState().getEnabled());
        assertThat(response.getResourceModel().getSource()).isEqualTo(request.getDesiredResourceState().getSource());
        assertThat(response.getResourceModel().getTrustAnchorId()).isNotNull();
        assertThat(response.getResourceModel().getTags().equals(testTag));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_NullValues() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel modelOne = ResourceModel.builder()
                .name(testTrustAnchorName)
                .enabled(testEnabled)
                .source(null)
                .tags(List.of(testTag))
                .build();

        final ResourceModel modelTwo = ResourceModel.builder()
                .name(testTrustAnchorName)
                .enabled(testEnabled)
                .source(BaseHandlerUtils.sourceToModelSource(testSourceNullSourceData))
                .tags(List.of(testTag))
                .build();

        final ResourceModel modelThree = ResourceModel.builder()
                .name(testTrustAnchorName)
                .enabled(testEnabled)
                .source(BaseHandlerUtils.sourceToModelSource(testSourceNullSourceType))
                .tags(List.of(testTag))
                .build();

        final ResourceHandlerRequest<ResourceModel> requestOne = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(modelOne)
                .region(testRegion)
                .build();

        final ResourceHandlerRequest<ResourceModel> requestTwo = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(modelTwo)
                .region(testRegion)
                .build();

        final ResourceHandlerRequest<ResourceModel> requestThree = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(modelThree)
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

        try {
            handler.handleRequest(proxy, requestThree, null, logger);
            assertThat(true).isEqualTo(false);
        }
        catch (CfnInvalidRequestException e) {
            //no-op
        }
    }
}
