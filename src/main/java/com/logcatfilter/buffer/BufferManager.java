package com.logcatfilter.buffer;

import com.logcatfilter.parser.LogcatEntry;

import java.util.List;

/**
 * Manages a LogBuffer using a BufferConfig. Provides higher-level operations
 * such as auto-clear on overflow detection.
 */
public class BufferManager {

    private final LogBuffer buffer;
    private final BufferConfig config;
    private long totalIngested = 0;
    private long totalEvicted = 0;

    public BufferManager(BufferConfig config) {
        this.config = config;
        this.buffer = new LogBuffer(config.getCapacity());
    }

    /**
     * Ingests a new entry. If autoClearOnOverflow is enabled and the buffer
     * is at capacity, the buffer is cleared before adding.
     *
     * @throws IllegalArgumentException if entry is null
     */
    public void ingest(LogcatEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Cannot ingest a null LogcatEntry");
        }
        if (config.isAutoClearOnOverflow() && buffer.size() >= config.getCapacity()) {
            totalEvicted += buffer.size();
            buffer.clear();
        }
        int sizeBefore = buffer.size();
        buffer.add(entry);
        if (buffer.size() == sizeBefore) {
            // size did not grow → an eviction occurred in the circular buffer
            totalEvicted++;
        }
        totalIngested++;
    }

    /**
     * Ingests multiple entries in order. Null entries within the list are
     * skipped rather than throwing, but a null list itself throws.
     *
     * @param entries the list of LogcatEntry objects to ingest
     * @throws IllegalArgumentException if the entries list is null
     */
    public void ingestAll(List<LogcatEntry> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("Cannot ingest a null list of entries");
        }
        for (LogcatEntry entry : entries) {
            if (entry != null) {
                ingest(entry);
            }
        }
    }

    public List<LogcatEntry> getEntries() {
        return buffer.snapshot();
    }

    public void clear() {
        totalEvicted += buffer.size();
        buffer.clear();
    }

    public int currentSize() {
        return buffer.size();
    }

    public long getTotalIngested() {
        return totalIngested;
    }

    public long getTotalEvicted() {
        return totalEvicted;
    }

    public BufferConfig getConfig() {
        return config;
    }

    /**
     * Returns the ratio of evicted entries to total ingested entries,
     * representing how often overflow has occurred. Returns 0.0 if nothing
     * has been ingested yet.
     *
     * @return eviction rate as a value between 0.0 and 1.0
     */
    public double getEvictionRate() {
        if (totalIngested == 0) {
            return 0.0;
        }
        return (double) totalEvicted / totalIngested;
    }
}
