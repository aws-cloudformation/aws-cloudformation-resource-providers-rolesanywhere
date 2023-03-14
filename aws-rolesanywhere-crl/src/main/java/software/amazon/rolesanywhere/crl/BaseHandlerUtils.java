package software.amazon.rolesanywhere.crl;

import software.amazon.awssdk.services.rolesanywhere.model.Tag;

import java.util.ArrayList;
import java.util.List;


public class BaseHandlerUtils {

    public static Tag modelTagToTag(software.amazon.rolesanywhere.crl.Tag tag) {
        return Tag.builder()
                .key(tag.getKey())
                .value(tag.getValue())
                .build();
    }

    public static List<Tag> modelTagListToTagList(List<software.amazon.rolesanywhere.crl.Tag> modelTagList) {
        List<Tag> tagList = new ArrayList<>();

        if (modelTagList == null) {
            return tagList;
        }
        for (software.amazon.rolesanywhere.crl.Tag modelTag: modelTagList) {
            tagList.add(modelTagToTag(modelTag));
        }

        return tagList;
    }

    public static software.amazon.rolesanywhere.crl.Tag tagToModelTag(Tag tag) {
        return software.amazon.rolesanywhere.crl.Tag.builder()
                .key(tag.key())
                .value(tag.value())
                .build();
    }

    public static List<software.amazon.rolesanywhere.crl.Tag> tagListToModelTagList(List<Tag> tagList) {
        List<software.amazon.rolesanywhere.crl.Tag> modelTagList = new ArrayList<>();

        if (tagList == null) {
            return modelTagList;
        }
        for (Tag tag: tagList) {
            modelTagList.add(tagToModelTag(tag));
        }

        return modelTagList;
    }


}
