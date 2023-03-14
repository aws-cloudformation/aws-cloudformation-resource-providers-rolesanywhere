package software.amazon.rolesanywhere.trustanchor;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.rolesanywhere.model.TrustAnchorDetail;
import software.amazon.awssdk.services.rolesanywhere.model.Source;
import software.amazon.awssdk.services.rolesanywhere.model.SourceData;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class BaseTestUtils {

    public static Source modelSourceToSource(
            software.amazon.rolesanywhere.trustanchor.Source modelSource
    ) {
        SourceData sourceData = SourceData.builder()
                .acmPcaArn(modelSource.getSourceData().getAcmPcaArn())
                .x509CertificateData(modelSource.getSourceData().getX509CertificateData())
                .build();

        Source source = Source.builder()
                .sourceType(modelSource.getSourceType())
                .sourceData(sourceData)
                .build();

        return source;
    }
}
