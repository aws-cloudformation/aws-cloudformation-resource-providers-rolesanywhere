package software.amazon.rolesanywhere.crl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rolesanywhere.model.ImportCrlRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ImportCrlResponse;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceRequest;
import software.amazon.awssdk.services.rolesanywhere.model.ListTagsForResourceResponse;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static software.amazon.rolesanywhere.crl.BaseHandlerUtils.modelTagToTag;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest extends BaseTest {

    @Test
    @SuppressWarnings("unchecked")
    public void handleRequest_SimpleSuccess() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel model = ResourceModel.builder()
                .name(testName)
                .enabled(testEnabled)
                .crlData(testCrlData)
                .trustAnchorArn(testTrustAnchorArn)
                .tags(new ArrayList<>(Collections.singletonList(testTag)))
                .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .region(testRegion)
                .systemTags(Map.of())
                .build();

        doReturn(ImportCrlResponse.builder().crl(testCrl).build())
                .when(proxy).injectCredentialsAndInvokeV2(any(ImportCrlRequest.class), any(Function.class));

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
        assertThat(response.getResourceModel().getCrlData()).isEqualTo(request.getDesiredResourceState().getCrlData());
        assertThat(response.getResourceModel().getTrustAnchorArn()).isEqualTo(request.getDesiredResourceState().getTrustAnchorArn());
        assertThat(response.getResourceModel().getCrlId()).isNotNull();
        assertThat(response.getResourceModel().getTags().equals(testTag));
    }


    @SuppressWarnings("unchecked")
    @Test
    public void handleRequest_NullValues() {
        final CreateHandler handler = new CreateHandler();

        final ResourceModel modelOne = ResourceModel.builder()
                .name(testName)
                .crlData(null)
                .build();

        final ResourceModel modelTwo = ResourceModel.builder()
                .name(null)
                .crlData(testCrlData)
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
