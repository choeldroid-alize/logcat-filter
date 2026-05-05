package com.logcatfilter.filter;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds an ordered list of FilterRules and evaluates them against a LogcatEntry.
 * All rules must match for an entry to pass (AND semantics).
 */
public class FilterChain {

    private final List<FilterRule> rules = new ArrayList<>();

    public void addRule(FilterRule rule) {
        if (rule == null) throw new IllegalArgumentException("Rule must not be null");
        rules.add(rule);
    }

    public boolean removeRule(FilterRule rule) {
        return rules.remove(rule);
    }

    public void clear() {
        rules.clear();
    }

    /**
     * Returns true if the entry passes all rules in the chain.
     * An empty chain accepts everything.
     */
    public boolean accepts(LogcatEntry entry) {
        if (entry == null) return false;
        for (FilterRule rule : rules) {
            if (!rule.matches(entry)) {
                return false;
            }
        }
        return true;
    }

    public List<FilterRule> getRules() {
        return Collections.unmodifiableList(rules);
    }

    public int size() {
        return rules.size();
    }

    public boolean isEmpty() {
        return rules.isEmpty();
    }

    @Override
    public String toString() {
        return "FilterChain" + rules;
    }
}
