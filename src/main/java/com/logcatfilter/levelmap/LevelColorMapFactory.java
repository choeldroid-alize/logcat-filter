package com.logcatfilter.levelmap;

import com.logcatfilter.levelmap.LevelColorMap.LogLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory that creates pre-configured {@link LevelColorMap} instances.
 */
public class LevelColorMapFactory {

    // Standard ANSI escape codes
    public static final String ANSI_RESET   = "\033[0m";
    public static final String ANSI_WHITE   = "\033[37m";
    public static final String ANSI_CYAN    = "\033[36m";
    public static final String ANSI_GREEN   = "\033[32m";
    public static final String ANSI_YELLOW  = "\033[33m";
    public static final String ANSI_RED     = "\033[31m";
    public static final String ANSI_MAGENTA = "\033[35m";

    private LevelColorMapFactory() {}

    /** Returns a map with sensible defaults matching typical logcat colour conventions. */
    public static LevelColorMap createDefault() {
        Map<LogLevel, String> defaults = new EnumMap<>(LogLevel.class);
        defaults.put(LogLevel.VERBOSE, ANSI_WHITE);
        defaults.put(LogLevel.DEBUG,   ANSI_CYAN);
        defaults.put(LogLevel.INFO,    ANSI_GREEN);
        defaults.put(LogLevel.WARN,    ANSI_YELLOW);
        defaults.put(LogLevel.ERROR,   ANSI_RED);
        defaults.put(LogLevel.FATAL,   ANSI_MAGENTA);
        return new LevelColorMap(defaults);
    }

    /** Returns an empty map (all levels will fall back to terminal default). */
    public static LevelColorMap createEmpty() {
        return new LevelColorMap();
    }

    /** Returns a monochrome map where every level maps to the reset/default colour. */
    public static LevelColorMap createMonochrome() {
        Map<LogLevel, String> mono = new EnumMap<>(LogLevel.class);
        for (LogLevel level : LogLevel.values()) {
            mono.put(level, ANSI_RESET);
        }
        return new LevelColorMap(mono);
    }
}
