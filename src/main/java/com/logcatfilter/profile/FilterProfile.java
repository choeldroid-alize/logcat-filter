package com.logcatfilter.profile;

import com.logcatfilter.filter.FilterRule;
import com.logcatfilter.highlight.HighlightRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a named collection of filter and highlight rules
 * that can be saved, loaded, and switched at runtime.
 */
public class FilterProfile {

    private final String name;
    private final String description;
    private final List<FilterRule> filterRules;
    private final List<HighlightRule> highlightRules;
    private final long createdAt;
    private long updatedAt;

    public FilterProfile(String name, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Profile name must not be blank");
        }
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.filterRules = new ArrayList<>();
        this.highlightRules = new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public long getCreatedAt() { return createdAt; }
    public long getUpdatedAt() { return updatedAt; }

    public List<FilterRule> getFilterRules() {
        return Collections.unmodifiableList(filterRules);
    }

    public List<HighlightRule> getHighlightRules() {
        return Collections.unmodifiableList(highlightRules);
    }

    public void addFilterRule(FilterRule rule) {
        Objects.requireNonNull(rule, "FilterRule must not be null");
        filterRules.add(rule);
        updatedAt = System.currentTimeMillis();
    }

    public void removeFilterRule(FilterRule rule) {
        if (filterRules.remove(rule)) {
            updatedAt = System.currentTimeMillis();
        }
    }

    public void addHighlightRule(HighlightRule rule) {
        Objects.requireNonNull(rule, "HighlightRule must not be null");
        highlightRules.add(rule);
        updatedAt = System.currentTimeMillis();
    }

    public void removeHighlightRule(HighlightRule rule) {
        if (highlightRules.remove(rule)) {
            updatedAt = System.currentTimeMillis();
        }
    }

    public void clearRules() {
        filterRules.clear();
        highlightRules.clear();
        updatedAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return String.format("FilterProfile{name='%s', filters=%d, highlights=%d}",
                name, filterRules.size(), highlightRules.size());
    }
}
