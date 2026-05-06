package com.logcatfilter.highlight;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Applies a list of {@link HighlightRule}s to logcat entry messages in order.
 */
public class HighlightEngine {

    private final List<HighlightRule> rules;

    public HighlightEngine() {
        this.rules = new ArrayList<>();
    }

    public HighlightEngine(List<HighlightRule> rules) {
        this.rules = new ArrayList<>(rules);
    }

    public void addRule(HighlightRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    public void removeRule(HighlightRule rule) {
        rules.remove(rule);
    }

    public List<HighlightRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    /**
     * Applies all highlight rules sequentially to the message of the given entry.
     *
     * @param entry the logcat entry whose message should be highlighted
     * @return the message string with ANSI highlight codes applied
     */
    public String highlight(LogcatEntry entry) {
        if (entry == null) {
            return "";
        }
        String text = entry.getMessage();
        for (HighlightRule rule : rules) {
            text = rule.applyTo(text);
        }
        return text;
    }

    /**
     * Applies all highlight rules to a raw string.
     */
    public String highlightRaw(String text) {
        if (text == null) {
            return "";
        }
        for (HighlightRule rule : rules) {
            text = rule.applyTo(text);
        }
        return text;
    }

    public void clearRules() {
        rules.clear();
    }

    public boolean hasRules() {
        return !rules.isEmpty();
    }
}
