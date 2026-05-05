package com.logcatfilter.tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Registry that maps log tags (e.g. "MyApp") to user-defined labels and ANSI colors.
 * Thread-safe for concurrent read access during live logcat streaming.
 */
public class TagRegistry {

    private final Map<String, TagDefinition> entries = new HashMap<>();

    /**
     * Register or overwrite a tag definition.
     *
     * @param tag   the logcat tag to match (case-sensitive)
     * @param label human-readable label shown in the UI
     * @param color ANSI color code (e.g. "\033[32m" for green)
     */
    public void register(String tag, String label, String color) {
        if (tag == null || tag.isBlank()) {
            throw new IllegalArgumentException("Tag must not be null or blank");
        }
        entries.put(tag, new TagDefinition(tag, label, color));
    }

    /**
     * Remove a previously registered tag.
     *
     * @return true if the tag existed and was removed
     */
    public boolean unregister(String tag) {
        return entries.remove(tag) != null;
    }

    /**
     * Look up the definition for a given tag.
     */
    public Optional<TagDefinition> lookup(String tag) {
        return Optional.ofNullable(entries.get(tag));
    }

    /**
     * Returns an unmodifiable view of all registered tags.
     */
    public Set<String> registeredTags() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    /**
     * Returns an unmodifiable snapshot of all definitions.
     */
    public Map<String, TagDefinition> all() {
        return Collections.unmodifiableMap(entries);
    }

    public int size() {
        return entries.size();
    }
}
