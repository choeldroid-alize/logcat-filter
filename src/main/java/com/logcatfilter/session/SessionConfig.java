package com.logcatfilter.session;

import com.logcatfilter.filter.FilterRule;
import com.logcatfilter.tag.TagDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the runtime configuration for a logcat-filter session,
 * including active filter rules and tag definitions.
 */
public class SessionConfig {

    private final List<FilterRule> filterRules;
    private final List<TagDefinition> tagDefinitions;
    private boolean paused;
    private int maxBufferLines;

    public static final int DEFAULT_MAX_BUFFER_LINES = 5000;

    public SessionConfig() {
        this.filterRules = new ArrayList<>();
        this.tagDefinitions = new ArrayList<>();
        this.paused = false;
        this.maxBufferLines = DEFAULT_MAX_BUFFER_LINES;
    }

    public void addFilterRule(FilterRule rule) {
        if (rule == null) throw new IllegalArgumentException("FilterRule must not be null");
        filterRules.add(rule);
    }

    public void removeFilterRule(FilterRule rule) {
        filterRules.remove(rule);
    }

    public List<FilterRule> getFilterRules() {
        return Collections.unmodifiableList(filterRules);
    }

    public void addTagDefinition(TagDefinition tag) {
        if (tag == null) throw new IllegalArgumentException("TagDefinition must not be null");
        tagDefinitions.add(tag);
    }

    public void removeTagDefinition(TagDefinition tag) {
        tagDefinitions.remove(tag);
    }

    public List<TagDefinition> getTagDefinitions() {
        return Collections.unmodifiableList(tagDefinitions);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getMaxBufferLines() {
        return maxBufferLines;
    }

    public void setMaxBufferLines(int maxBufferLines) {
        if (maxBufferLines < 1) throw new IllegalArgumentException("maxBufferLines must be >= 1");
        this.maxBufferLines = maxBufferLines;
    }

    public void clear() {
        filterRules.clear();
        tagDefinitions.clear();
        paused = false;
        maxBufferLines = DEFAULT_MAX_BUFFER_LINES;
    }
}
