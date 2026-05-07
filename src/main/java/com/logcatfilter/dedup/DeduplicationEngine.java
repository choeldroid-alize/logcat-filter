package com.logcatfilter.dedup;

import com.logcatfilter.parser.LogcatEntry;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Detects and suppresses duplicate log lines within a sliding window.
 * When a duplicate is detected, it increments the occurrence count instead
 * of emitting the line again.
 */
public class DeduplicationEngine {

    private final DeduplicationConfig config;
    private final LinkedHashMap<String, Integer> window;

    public DeduplicationEngine(DeduplicationConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("DeduplicationConfig must not be null");
        }
        this.config = config;
        this.window = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
                return size() > config.getWindowSize();
            }
        };
    }

    /**
     * Processes a log entry.
     *
     * @param entry the incoming log entry
     * @return an Optional containing the entry to display, or empty if suppressed as duplicate
     */
    public Optional<LogcatEntry> process(LogcatEntry entry) {
        if (!config.isEnabled()) {
            return Optional.of(entry);
        }

        String key = buildKey(entry);

        if (window.containsKey(key)) {
            if (config.isCountOccurrences()) {
                window.merge(key, 1, Integer::sum);
            }
            return Optional.empty();
        }

        window.put(key, 1);
        return Optional.of(entry);
    }

    /**
     * Returns the number of times the given entry has been seen within the current window.
     */
    public int getOccurrenceCount(LogcatEntry entry) {
        String key = buildKey(entry);
        return window.getOrDefault(key, 0);
    }

    /**
     * Clears the deduplication window.
     */
    public void reset() {
        window.clear();
    }

    public int getWindowOccupancy() {
        return window.size();
    }

    private String buildKey(LogcatEntry entry) {
        if (config.isMatchOnMessageOnly()) {
            return entry.getMessage();
        }
        return entry.getLevel() + "|" + entry.getTag() + "|" + entry.getMessage();
    }
}
