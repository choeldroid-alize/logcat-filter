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
     */
    public void ingest(LogcatEntry entry) {
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
}
