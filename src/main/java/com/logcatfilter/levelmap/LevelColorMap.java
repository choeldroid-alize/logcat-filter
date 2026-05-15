package com.logcatfilter.levelmap;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Maps Android log levels (V/D/I/W/E/F) to display colors (ANSI codes or named colors).
 */
public class LevelColorMap {

    public enum LogLevel {
        VERBOSE, DEBUG, INFO, WARN, ERROR, FATAL
    }

    private final Map<LogLevel, String> colorMap;

    public LevelColorMap() {
        this.colorMap = new EnumMap<>(LogLevel.class);
    }

    public LevelColorMap(Map<LogLevel, String> initial) {
        this.colorMap = new EnumMap<>(initial);
    }

    public void setColor(LogLevel level, String ansiColor) {
        if (level == null) throw new IllegalArgumentException("Level must not be null");
        if (ansiColor == null || ansiColor.isBlank()) throw new IllegalArgumentException("Color must not be blank");
        colorMap.put(level, ansiColor);
    }

    public Optional<String> getColor(LogLevel level) {
        return Optional.ofNullable(colorMap.get(level));
    }

    public void removeColor(LogLevel level) {
        colorMap.remove(level);
    }

    public boolean hasColor(LogLevel level) {
        return colorMap.containsKey(level);
    }

    public Map<LogLevel, String> asUnmodifiableMap() {
        return Collections.unmodifiableMap(colorMap);
    }

    public static LogLevel fromChar(char c) {
        return switch (Character.toUpperCase(c)) {
            case 'V' -> LogLevel.VERBOSE;
            case 'D' -> LogLevel.DEBUG;
            case 'I' -> LogLevel.INFO;
            case 'W' -> LogLevel.WARN;
            case 'E' -> LogLevel.ERROR;
            case 'F' -> LogLevel.FATAL;
            default -> throw new IllegalArgumentException("Unknown log level char: " + c);
        };
    }

    public int size() {
        return colorMap.size();
    }
}
