package com.logcatfilter.profile;

import java.util.Collections;
import java.util.List;

/**
 * Holds the result of deserializing a profile from text.
 */
public class ProfileParseResult {

    private final boolean success;
    private final String errorMessage;
    private final String name;
    private final String description;
    private final List<String> filterRuleStrings;
    private final List<String> highlightRuleStrings;

    private ProfileParseResult(boolean success, String errorMessage, String name,
                               String description, List<String> filterRuleStrings,
                               List<String> highlightRuleStrings) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.name = name;
        this.description = description;
        this.filterRuleStrings = filterRuleStrings != null
                ? Collections.unmodifiableList(filterRuleStrings) : Collections.emptyList();
        this.highlightRuleStrings = highlightRuleStrings != null
                ? Collections.unmodifiableList(highlightRuleStrings) : Collections.emptyList();
    }

    public static ProfileParseResult success(String name, String description,
                                             List<String> filterRules,
                                             List<String> highlightRules) {
        return new ProfileParseResult(true, null, name, description, filterRules, highlightRules);
    }

    public static ProfileParseResult failure(String errorMessage) {
        return new ProfileParseResult(false, errorMessage, null, null, null, null);
    }

    public boolean isSuccess() { return success; }
    public String getErrorMessage() { return errorMessage; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getFilterRuleStrings() { return filterRuleStrings; }
    public List<String> getHighlightRuleStrings() { return highlightRuleStrings; }

    @Override
    public String toString() {
        if (!success) return "ProfileParseResult{FAILURE: " + errorMessage + "}";
        return String.format("ProfileParseResult{name='%s', filters=%d, highlights=%d}",
                name, filterRuleStrings.size(), highlightRuleStrings.size());
    }
}
