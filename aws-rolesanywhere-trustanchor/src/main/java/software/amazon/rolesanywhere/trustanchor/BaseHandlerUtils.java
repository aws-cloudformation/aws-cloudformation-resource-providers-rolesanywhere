package software.amazon.rolesanywhere.trustanchor;


import software.amazon.awssdk.services.rolesanywhere.model.Tag;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class BaseHandlerUtils {

    public static Source sourceToModelSource(
            software.amazon.awssdk.services.rolesanywhere.model.Source inputSource) {

        SourceData outputSourceData;
        Source source;

        if (null == inputSource.sourceData()) {
            outputSourceData = null;

        } else {

            outputSourceData = SourceData.builder()
                    .x509CertificateData(inputSource.sourceData().x509CertificateData())
                    .acmPcaArn(inputSource.sourceData().acmPcaArn())
                    .build();
        }

        if (null == inputSource.sourceType()) {
            source = Source.builder()
                    .sourceData(outputSourceData)
                    .sourceType(null)
                    .build();

            return source;
        } else {

            source = Source.builder()
                    .sourceData(outputSourceData)
                    .sourceType(inputSource.sourceTypeAsString())
                    .build();
        }

        return source;
    }

    public static Tag modelTagToTag(software.amazon.rolesanywhere.trustanchor.Tag tag) {
        return Tag.builder()
                .key(tag.getKey())
                .value(tag.getValue())
                .build();
    }

    public static List<Tag> modelTagListToTagList(List<software.amazon.rolesanywhere.trustanchor.Tag> modelTagList) {
        List<Tag> tagList = new ArrayList<>();

        if (modelTagList == null) {
            return tagList;
        }
        for (software.amazon.rolesanywhere.trustanchor.Tag modelTag: modelTagList) {
            tagList.add(modelTagToTag(modelTag));
        }

        return tagList;
    }

    public static software.amazon.rolesanywhere.trustanchor.Tag tagToModelTag(Tag tag) {
        return software.amazon.rolesanywhere.trustanchor.Tag.builder()
                .key(tag.key())
                .value(tag.value())
                .build();
    }

    public static List<software.amazon.rolesanywhere.trustanchor.Tag> tagListToModelTagList(List<Tag> tagList) {
        List<software.amazon.rolesanywhere.trustanchor.Tag> modelTagList = new ArrayList<>();

        if (tagList == null) {
            return modelTagList;
        }
        for (Tag tag: tagList) {
            modelTagList.add(tagToModelTag(tag));
        }

        return modelTagList;
    }
}
