package com.logcatfilter.notify;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Defines a rule that triggers a notification when a log entry matches.
 */
public class NotificationRule {

    public enum MatchField { TAG, MESSAGE, LEVEL, ANY }

    private final String id;
    private final String patternString;
    private final Pattern compiledPattern;
    private final MatchField matchField;
    private final String label;
    private final boolean soundEnabled;

    public NotificationRule(String id, String patternString, MatchField matchField,
                            String label, boolean soundEnabled) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.patternString = Objects.requireNonNull(patternString, "pattern must not be null");
        this.compiledPattern = Pattern.compile(patternString);
        this.matchField = Objects.requireNonNull(matchField, "matchField must not be null");
        this.label = Objects.requireNonNull(label, "label must not be null");
        this.soundEnabled = soundEnabled;
    }

    public String getId() { return id; }
    public String getPatternString() { return patternString; }
    public Pattern getCompiledPattern() { return compiledPattern; }
    public MatchField getMatchField() { return matchField; }
    public String getLabel() { return label; }
    public boolean isSoundEnabled() { return soundEnabled; }

    public boolean matches(String tag, String level, String message) {
        return switch (matchField) {
            case TAG     -> compiledPattern.matcher(tag).find();
            case LEVEL   -> compiledPattern.matcher(level).find();
            case MESSAGE -> compiledPattern.matcher(message).find();
            case ANY     -> compiledPattern.matcher(tag).find()
                            || compiledPattern.matcher(level).find()
                            || compiledPattern.matcher(message).find();
        };
    }

    @Override
    public String toString() {
        return "NotificationRule{id='" + id + "', pattern='" + patternString +
               "', field=" + matchField + ", label='" + label + "'}";
    }
}
