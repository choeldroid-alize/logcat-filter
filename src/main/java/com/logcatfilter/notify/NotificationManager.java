package com.logcatfilter.notify;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Manages notification rules and dispatches alerts when log entries match.
 */
public class NotificationManager {

    private final Map<String, NotificationRule> rules = new LinkedHashMap<>();
    private final List<Consumer<NotificationEvent>> listeners = new ArrayList<>();

    public void addRule(NotificationRule rule) {
        if (rule == null) throw new IllegalArgumentException("rule must not be null");
        rules.put(rule.getId(), rule);
    }

    public boolean removeRule(String ruleId) {
        return rules.remove(ruleId) != null;
    }

    public NotificationRule getRule(String ruleId) {
        return rules.get(ruleId);
    }

    public List<NotificationRule> getRules() {
        return Collections.unmodifiableList(new ArrayList<>(rules.values()));
    }

    public void addListener(Consumer<NotificationEvent> listener) {
        if (listener != null) listeners.add(listener);
    }

    /**
     * Evaluates all rules against the given log entry and fires events for matches.
     *
     * @return list of events that were triggered
     */
    public List<NotificationEvent> evaluate(LogcatEntry entry) {
        if (entry == null) return Collections.emptyList();
        List<NotificationEvent> fired = new ArrayList<>();
        String tag     = entry.getTag()     != null ? entry.getTag()     : "";
        String level   = entry.getLevel()   != null ? entry.getLevel()   : "";
        String message = entry.getMessage() != null ? entry.getMessage() : "";
        for (NotificationRule rule : rules.values()) {
            if (rule.matches(tag, level, message)) {
                NotificationEvent event = new NotificationEvent(rule, entry);
                fired.add(event);
                listeners.forEach(l -> l.accept(event));
            }
        }
        return fired;
    }

    public void clearRules() {
        rules.clear();
    }

    public int ruleCount() {
        return rules.size();
    }
}
