package com.logcatfilter.filter;

import com.logcatfilter.parser.LogcatEntry;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a single filter rule that can be applied to a LogcatEntry.
 * Supports filtering by tag, message content, log level, and PID.
 */
public class FilterRule {

    public enum Field {
        TAG, MESSAGE, LEVEL, PID
    }

    private final Field field;
    private final String rawValue;
    private final Pattern pattern;
    private final boolean negate;

    public FilterRule(Field field, String value, boolean negate) {
        this.field = field;
        this.rawValue = value;
        this.negate = negate;
        try {
            this.pattern = Pattern.compile(value, Pattern.CASE_INSENSITIVE);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + value, e);
        }
    }

    public FilterRule(Field field, String value) {
        this(field, value, false);
    }

    public boolean matches(LogcatEntry entry) {
        String target = switch (field) {
            case TAG -> entry.getTag();
            case MESSAGE -> entry.getMessage();
            case LEVEL -> String.valueOf(entry.getLevel());
            case PID -> String.valueOf(entry.getPid());
        };
        if (target == null) return negate;
        boolean matched = pattern.matcher(target).find();
        return negate != matched;
    }

    public Field getField() { return field; }
    public String getRawValue() { return rawValue; }
    public boolean isNegate() { return negate; }

    @Override
    public String toString() {
        return (negate ? "!" : "") + field.name().toLowerCase() + ":" + rawValue;
    }
}
