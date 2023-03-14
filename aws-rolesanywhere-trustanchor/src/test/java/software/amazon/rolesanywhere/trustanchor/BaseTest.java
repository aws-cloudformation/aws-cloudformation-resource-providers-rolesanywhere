package software.amazon.rolesanywhere.trustanchor;

import static org.mockito.Mockito.mock;
import org.mockito.Mock;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rolesanywhere.model.TrustAnchorDetail;
import software.amazon.awssdk.services.rolesanywhere.model.Source;
import software.amazon.awssdk.services.rolesanywhere.model.SourceData;
import software.amazon.awssdk.services.rolesanywhere.model.TrustAnchorType;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import org.junit.jupiter.api.BeforeEach;

import java.time.Instant;

public class BaseTest {
    @Mock AmazonWebServicesClientProxy proxy;
    @Mock Logger logger;
    static String testRegion;

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

    protected static final Instant testCreatedAt = Instant.now();

    protected static final String testTrustAnchorId = "00000000-0000-0000-0000-000000000000";

    protected static final String testTrustAnchorName = "test TA name";

    protected static final String testCert = "test cert";

    protected static final Boolean testEnabled = true;

    protected static final SourceData testSourceData = SourceData.builder()
            .x509CertificateData(testCert)
            .acmPcaArn(null)
            .build();

    protected static final Source testSource = Source.builder()
            .sourceType(TrustAnchorType.CERTIFICATE_BUNDLE.toString())
            .sourceData(testSourceData)
            .build();

    protected static final Source testSourceNullSourceType = Source.builder()
            .sourceType((String) null)
            .sourceData(testSourceData)
            .build();

    protected static final Source testSourceNullSourceData = Source.builder()
            .sourceType(TrustAnchorType.CERTIFICATE_BUNDLE.toString())
            .sourceData((SourceData) null)
            .build();

    protected static final TrustAnchorDetail testTrustAnchor = TrustAnchorDetail.builder()
            .name(testTrustAnchorName)
            .enabled(testEnabled)
            .source(testSource)
            .createdAt(testCreatedAt)
            .trustAnchorId(testTrustAnchorId)
            .build();

    protected static final String testNextToken = "test pagination token";

    protected static final Tag testTag = Tag.builder()
            .key("TagKey1")
            .value("TagValue1")
            .build();
}
