package com.logcatfilter.stats;

import com.logcatfilter.parser.LogcatEntry;
import com.logcatfilter.stats.LogStats.Level;

/**
 * Collects statistics from logcat entries and filter decisions.
 */
public class StatsCollector {

    private final LogStats stats;

    public StatsCollector() {
        this.stats = new LogStats();
    }

    public StatsCollector(LogStats stats) {
        this.stats = stats;
    }

    /**
     * Records an entry with its filter result.
     *
     * @param entry  the parsed logcat entry
     * @param passed true if the entry passed the filter chain
     */
    public void collect(LogcatEntry entry, boolean passed) {
        if (entry == null) {
            return;
        }
        Level level = mapLevel(entry.getLevel());
        stats.recordLine(level, passed);
    }

    private Level mapLevel(String rawLevel) {
        if (rawLevel == null) return Level.UNKNOWN;
        switch (rawLevel.trim().toUpperCase()) {
            case "V": return Level.VERBOSE;
            case "D": return Level.DEBUG;
            case "I": return Level.INFO;
            case "W": return Level.WARN;
            case "E": return Level.ERROR;
            case "F": return Level.FATAL;
            default:  return Level.UNKNOWN;
        }
    }

    public LogStats getStats() {
        return stats;
    }

    /**
     * Returns a formatted summary string suitable for display in the TUI status bar.
     */
    public String getSummary() {
        long total   = stats.getTotalLines();
        long matched = stats.getMatchedLines();
        long errors  = stats.getCountForLevel(Level.ERROR)
                     + stats.getCountForLevel(Level.FATAL);
        return String.format(
            "Lines: %d | Shown: %d | Errors: %d",
            total, matched, errors
        );
    }
}
