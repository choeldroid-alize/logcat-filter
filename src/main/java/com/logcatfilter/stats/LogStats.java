package com.logcatfilter.stats;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tracks real-time statistics about processed logcat entries.
 */
public class LogStats {

    public enum Level { VERBOSE, DEBUG, INFO, WARN, ERROR, FATAL, UNKNOWN }

    private final AtomicLong totalLines = new AtomicLong(0);
    private final AtomicLong filteredLines = new AtomicLong(0);
    private final AtomicLong matchedLines = new AtomicLong(0);
    private final Map<Level, AtomicLong> levelCounts;

    public LogStats() {
        levelCounts = new EnumMap<>(Level.class);
        for (Level level : Level.values()) {
            levelCounts.put(level, new AtomicLong(0));
        }
    }

    public void recordLine(Level level, boolean passed) {
        totalLines.incrementAndGet();
        levelCounts.get(level).incrementAndGet();
        if (!passed) {
            filteredLines.incrementAndGet();
        } else {
            matchedLines.incrementAndGet();
        }
    }

    public long getTotalLines() {
        return totalLines.get();
    }

    public long getFilteredLines() {
        return filteredLines.get();
    }

    public long getMatchedLines() {
        return matchedLines.get();
    }

    public long getCountForLevel(Level level) {
        return levelCounts.get(level).get();
    }

    public Map<Level, Long> getLevelSnapshot() {
        Map<Level, Long> snapshot = new EnumMap<>(Level.class);
        for (Map.Entry<Level, AtomicLong> entry : levelCounts.entrySet()) {
            snapshot.put(entry.getKey(), entry.getValue().get());
        }
        return Collections.unmodifiableMap(snapshot);
    }

    public void reset() {
        totalLines.set(0);
        filteredLines.set(0);
        matchedLines.set(0);
        levelCounts.values().forEach(c -> c.set(0));
    }
}
