package software.amazon.rolesanywhere.crl;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rolesanywhere.model.CrlDetail;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.mockito.Mockito.mock;

public class BaseTest {

    @Mock AmazonWebServicesClientProxy proxy;

    @Mock Logger logger;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);

        if (System.getenv("AWS_REGION") != null) {
            testRegion = System.getenv("AWS_REGION");
        } else {
            testRegion = "us-west-2";
        }
    }

    static String testRegion;

    protected static final String testCrlArn = "Test CRL Arn";

    protected static final String testTrustAnchorArn = "Test TrustAnchor Arn";

    protected static final String testCrlId = "00000000-0000-0000-0000-000000000000";

    protected static final String testName = "CFN Test CRL";

    protected static final Boolean testEnabled = true;

    protected static final String testCrlData = "Test CRL data";

    protected static final Instant testCreatedAt = Instant.now();

    protected static final Instant testUpdatedAt = Instant.now();

    protected static final Tag testTag = Tag.builder()
            .key("TagKey1")
            .value("TagValue1")
            .build();

    protected static final Tag testTag1 = Tag.builder()
            .key("TagKey1")
            .value("TagValue1")
            .build();

    protected static final Tag testTag2 = Tag.builder()
            .key("TagKey2")
            .value("TagValue2")
            .build();

    protected static final Tag testTag3 = Tag.builder()
            .key("TagKey3")
            .value("TagValue3")
            .build();

    protected static CrlDetail testCrl = CrlDetail.builder()
            .crlId(testCrlId)
            .crlArn(testCrlArn)
            .name(testName)
            .enabled(testEnabled)
            .crlData(SdkBytes.fromByteArray(testCrlData.getBytes(StandardCharsets.UTF_8)))
            .trustAnchorArn(testTrustAnchorArn)
            .createdAt(testCreatedAt)
            .updatedAt(testUpdatedAt)
            .build();

    protected static final String testNextToken = "test pagination token";
}
