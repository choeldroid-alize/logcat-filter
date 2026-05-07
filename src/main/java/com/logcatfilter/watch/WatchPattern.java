package com.logcatfilter.watch;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a watch pattern that triggers alerts when matched in logcat output.
 */
public class WatchPattern {

    public enum AlertLevel {
        INFO, WARNING, ERROR, CRITICAL
    }

    private final String id;
    private final String rawPattern;
    private final Pattern compiledPattern;
    private final AlertLevel alertLevel;
    private final String label;
    private boolean enabled;

    public WatchPattern(String id, String rawPattern, AlertLevel alertLevel, String label) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Watch pattern id must not be blank");
        if (rawPattern == null || rawPattern.isBlank()) throw new IllegalArgumentException("Pattern must not be blank");
        this.id = id;
        this.rawPattern = rawPattern;
        this.alertLevel = Objects.requireNonNull(alertLevel, "alertLevel must not be null");
        this.label = label != null ? label : id;
        this.enabled = true;
        try {
            this.compiledPattern = Pattern.compile(rawPattern, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + rawPattern, e);
        }
    }

    public boolean matches(String text) {
        if (!enabled || text == null) return false;
        return compiledPattern.matcher(text).find();
    }

    public String getId() { return id; }
    public String getRawPattern() { return rawPattern; }
    public AlertLevel getAlertLevel() { return alertLevel; }
    public String getLabel() { return label; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "WatchPattern{id='" + id + "', pattern='" + rawPattern + "', level=" + alertLevel + ", enabled=" + enabled + "}";
    }
}
