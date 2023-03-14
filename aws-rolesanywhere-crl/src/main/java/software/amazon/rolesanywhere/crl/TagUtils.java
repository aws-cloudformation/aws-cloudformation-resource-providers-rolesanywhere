package software.amazon.rolesanywhere.crl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;

import software.amazon.awssdk.services.rolesanywhere.model.Tag;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class TagUtils {
    private static final String AWS_PREFIXED_TAGS_ERROR_MSG = "aws: prefixed tag key names are not allowed for external use.";

    /**
     * convertToMap
     *
     * Converts a collection of Tag objects to a tag-name -> tag-value map.
     *
     * Note: Tag objects with null tag values will not be included in the output
     * map.
     *
     * @param tags Collection of tags to convert
     * @return Converted Map of tags
     */
    public static Map<String, String> convertToMap(final Collection<Tag> tags) {
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyMap();
        }
        return tags.stream()
                .filter(tag -> tag.value() != null)
                .collect(Collectors.toMap(
                        Tag::key,
                        Tag::value,
                        (oldValue, newValue) -> newValue));
    }

    /**
     * convertToSet
     *
     * Converts a tag map to a set of Tag objects.
     *
     * Note: Like convertToMap, convertToSet filters out value-less tag entries.
     *
     * @param tagMap Map of tags to convert
     * @return Set of Tag objects
     */
    public static Set<Tag> convertToSet(final Map<String, String> tagMap) {
        if (MapUtils.isEmpty(tagMap)) {
            return Collections.emptySet();
        }
        return tagMap.entrySet().stream()
                .filter(tag -> tag.getValue() != null)
                .map(tag -> Tag.builder()
                        .key(tag.getKey())
                        .value(tag.getValue())
                        .build())
                .collect(Collectors.toSet());
    }

    /**
     * Checks to see whether system tags were passed into the request.
     * @param tagKeySet
     *   The set of tag keys in the request.
     */
    public static void checkInputTagsForSystemTags(Set<String> tagKeySet) {
        for (String tag : tagKeySet) {
            if (tag.startsWith("aws:")) {
                throw new CfnInvalidRequestException(TagUtils.AWS_PREFIXED_TAGS_ERROR_MSG);
            }
        }
    }

    /**
     * generateTagsForCreate
     *
     * Generate tags to put into resource creation request.
     * This includes user defined tags and system tags as well.
     */
    public static final Set<Tag> generateTagsForCreate(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> tagMap = new HashMap<>();

        if (handlerRequest.getDesiredResourceTags() != null) {
            checkInputTagsForSystemTags(handlerRequest.getDesiredResourceTags().keySet());
        }

        // merge system tags with desired resource tags if your service supports CloudFormation system tags
        if (handlerRequest.getSystemTags() != null) {
            tagMap.putAll(handlerRequest.getSystemTags());
        }

        if (handlerRequest.getDesiredResourceTags() != null) {
            tagMap.putAll(handlerRequest.getDesiredResourceTags());
        }

        // TODO: get tags from resource model based on your tag property name
        // TODO: tagMap.putAll(convertToMap(resourceModel.getTags()));
        if (resourceModel.getTags() != null) {
            tagMap.putAll(convertToMap(BaseHandlerUtils.modelTagListToTagList(resourceModel.getTags())));
        }
        return convertToSet(Collections.unmodifiableMap(tagMap));
    }

    /**
     * shouldUpdateTags
     *
     * Determines whether user defined tags have been changed during update.
     */
    public final static boolean shouldUpdateTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        final Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        final Map<String, String> desiredTags = getNewDesiredTags(resourceModel, handlerRequest);
        return ObjectUtils.notEqual(previousTags, desiredTags);
    }

    /**
     * getPreviouslyAttachedTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get previous attached user defined tags from both handlerRequest.getPreviousResourceTags (stack tags)
     * and handlerRequest.getPreviousResourceState (resource tags).
     */
    public static Map<String, String> getPreviouslyAttachedTags(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        // get previous stack level tags from handlerRequest
        HashMap<String, String> previousTags = handlerRequest.getPreviousResourceTags() != null ?
                (HashMap<String, String>) handlerRequest.getPreviousResourceTags() : new HashMap<String, String>();

        if (handlerRequest.getPreviousResourceState() != null) {
            previousTags.putAll(convertToMap(BaseHandlerUtils.modelTagListToTagList(handlerRequest.getPreviousResourceState().getTags())));
        }
        return previousTags;
    }

    /**
     * getNewDesiredTags
     *
     * If stack tags and resource tags are not merged together in Configuration class,
     * we will get new user defined tags from both resource model and previous stack tags.
     */
    public static Map<String, String> getNewDesiredTags(final ResourceModel resourceModel, final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        // get new stack level tags from handlerRequest
        HashMap<String, String> desiredTags = handlerRequest.getDesiredResourceTags() != null ?
                (HashMap<String, String>) handlerRequest.getDesiredResourceTags() : new HashMap<String, String>();

        desiredTags.putAll(convertToMap(BaseHandlerUtils.modelTagListToTagList(resourceModel.getTags())));
        return desiredTags;
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public static Map<String, String> generateTagsToAdd(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        return desiredTags.entrySet().stream()
                .filter(e -> !previousTags.containsKey(e.getKey()) || !Objects.equals(previousTags.get(e.getKey()), e.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue));
    }

    /**
     * generateTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public Set<String> generateTagsToRemove(final Map<String, String> previousTags, final Map<String, String> desiredTags) {
        final Set<String> desiredTagNames = desiredTags.keySet();

        return previousTags.keySet().stream()
                .filter(tagName -> !desiredTagNames.contains(tagName))
                .collect(Collectors.toSet());
    }

    /**
     * generateTagsToAdd
     *
     * Determines the tags the customer desired to define or redefine.
     */
    public static Set<Tag> generateTagsToAdd(final Set<Tag> previousTags, final Set<Tag> desiredTags) {
        return Sets.difference(new HashSet<>(desiredTags), new HashSet<>(previousTags));
    }

    /**
     * generateTagsToRemove
     *
     * Determines the tags the customer desired to remove from the function.
     */
    public static Set<Tag> generateTagsToRemove(final Set<Tag> previousTags, final Set<Tag> desiredTags) {
        return Sets.difference(new HashSet<>(previousTags), new HashSet<>(desiredTags));
    }

    /**
     * Uses the handler request to retrieve the tags that should be removed in an update operation
     * @param handlerRequest
     * @return
     */
    public static Set<Tag> getTagsToRemove(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        Map<String, String> desiredTags =  getNewDesiredTags(handlerRequest.getDesiredResourceState(), handlerRequest);

        Set<Tag> previousTagSet = convertToSet(previousTags);
        Set<Tag> desiredTagSet = convertToSet(desiredTags);

        return generateTagsToRemove(previousTagSet, desiredTagSet);
    }

    /**
     * Uses the handler request to retrieve the tags that should be created in an update operation
     * @param handlerRequest
     * @return
     */
    public static Set<Tag> getTagsToAdd(final ResourceHandlerRequest<ResourceModel> handlerRequest) {
        Map<String, String> previousTags = getPreviouslyAttachedTags(handlerRequest);
        Map<String, String> desiredTags =  getNewDesiredTags(handlerRequest.getDesiredResourceState(), handlerRequest);

        Set<Tag> previousTagSet = convertToSet(previousTags);
        Set<Tag> desiredTagSet = convertToSet(desiredTags);

        return generateTagsToAdd(previousTagSet, desiredTagSet);
    }


}
