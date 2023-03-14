package software.amazon.rolesanywhere.profile;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import software.amazon.awssdk.services.rolesanywhere.model.ProfileDetail;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;

public class BaseTest {

    @Mock
    AmazonWebServicesClientProxy proxy;

    @Mock
    Logger logger;

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

    protected static final String testProfileId = "00000000-0000-0000-0000-000000000000";

    protected static final String testProfileArn = "arn:aws:rolesanywhere:us-east-1:accountId:profile/00000000-0000-0000-0000-000000000000";

    protected static final String testName = "CFN Test Profile";

    protected static final Boolean testRequireInstanceProperties = false;

    protected static final Boolean testEnabled = true;

    protected static final Double testDurationSeconds = 900.0;

    protected static final List<String> testManagedPolicyArns = Collections.singletonList("Test Managed Policy Arn");

    protected static final List<String> testRoleArns = Collections.singletonList("Test RDR Role Arn");

    protected static final String testSessionPolicy = "Test session policy";

    protected static final String testNextToken = "Test Pagination Token";

    protected static final Tag testTag = Tag.builder()
            .key("TagKey1")
            .value("TagValue1")
            .build();

    protected static ProfileDetail testProfile = ProfileDetail.builder()
            .profileId(testProfileId)
            .profileArn(testProfileArn)
            .durationSeconds(testDurationSeconds.intValue())
            .enabled(testEnabled)
            .managedPolicyArns(testManagedPolicyArns)
            .name(testName)
            .requireInstanceProperties(testRequireInstanceProperties)
            .roleArns(testRoleArns)
            .sessionPolicy(testSessionPolicy)
            .build();
}
