package com.logcatfilter.notify;

import com.logcatfilter.parser.LogcatEntry;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable event produced when a {@link NotificationRule} matches a log entry.
 */
public class NotificationEvent {

    private final NotificationRule rule;
    private final LogcatEntry entry;
    private final Instant timestamp;

    public NotificationEvent(NotificationRule rule, LogcatEntry entry) {
        this.rule      = Objects.requireNonNull(rule,  "rule must not be null");
        this.entry     = Objects.requireNonNull(entry, "entry must not be null");
        this.timestamp = Instant.now();
    }

    public NotificationRule getRule()      { return rule; }
    public LogcatEntry      getEntry()     { return entry; }
    public Instant          getTimestamp() { return timestamp; }

    /** Convenience: the human-readable label from the triggering rule. */
    public String getLabel() { return rule.getLabel(); }

    @Override
    public String toString() {
        return "NotificationEvent{rule='" + rule.getId() +
               "', label='" + rule.getLabel() +
               "', at=" + timestamp + "}";
    }
}
