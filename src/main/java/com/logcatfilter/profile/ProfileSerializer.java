package com.logcatfilter.profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Serializes and deserializes FilterProfile to/from a simple text format
 * suitable for saving in config files.
 *
 * Format:
 *   [profile]
 *   name=<name>
 *   description=<desc>
 *   filter=<rule>
 *   highlight=<rule>
 */
public class ProfileSerializer {

    private static final String SECTION_HEADER = "[profile]";
    private static final String KEY_NAME = "name=";
    private static final String KEY_DESC = "description=";
    private static final String KEY_FILTER = "filter=";
    private static final String KEY_HIGHLIGHT = "highlight=";

    public String serialize(FilterProfile profile) {
        if (profile == null) throw new IllegalArgumentException("Profile must not be null");
        StringBuilder sb = new StringBuilder();
        sb.append(SECTION_HEADER).append("\n");
        sb.append(KEY_NAME).append(profile.getName()).append("\n");
        sb.append(KEY_DESC).append(profile.getDescription()).append("\n");
        profile.getFilterRules().forEach(r ->
                sb.append(KEY_FILTER).append(r.toString()).append("\n"));
        profile.getHighlightRules().forEach(r ->
                sb.append(KEY_HIGHLIGHT).append(r.toString()).append("\n"));
        return sb.toString();
    }

    public List<String> serializeAll(List<FilterProfile> profiles) {
        List<String> results = new ArrayList<>();
        for (FilterProfile p : profiles) {
            results.add(serialize(p));
        }
        return results;
    }

    public ProfileParseResult deserialize(String text) {
        if (text == null || text.isBlank()) {
            return ProfileParseResult.failure("Empty input");
        }
        String name = null;
        String description = "";
        List<String> filterLines = new ArrayList<>();
        List<String> highlightLines = new ArrayList<>();

        for (String line : text.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith(KEY_NAME)) {
                name = trimmed.substring(KEY_NAME.length()).trim();
            } else if (trimmed.startsWith(KEY_DESC)) {
                description = trimmed.substring(KEY_DESC.length()).trim();
            } else if (trimmed.startsWith(KEY_FILTER)) {
                filterLines.add(trimmed.substring(KEY_FILTER.length()).trim());
            } else if (trimmed.startsWith(KEY_HIGHLIGHT)) {
                highlightLines.add(trimmed.substring(KEY_HIGHLIGHT.length()).trim());
            }
        }
        if (name == null || name.isBlank()) {
            return ProfileParseResult.failure("Missing profile name");
        }
        return ProfileParseResult.success(name, description, filterLines, highlightLines);
    }
}
