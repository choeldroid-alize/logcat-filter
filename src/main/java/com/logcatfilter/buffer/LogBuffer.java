package com.logcatfilter.buffer;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * A fixed-capacity circular buffer for storing recent LogcatEntry objects.
 * When the buffer is full, the oldest entry is evicted.
 */
public class LogBuffer {

    private final int capacity;
    private final Deque<LogcatEntry> deque;

    public LogBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive, got: " + capacity);
        }
        this.capacity = capacity;
        this.deque = new ArrayDeque<>(capacity);
    }

    /**
     * Adds an entry to the buffer. If the buffer is at capacity, the oldest
     * entry is removed first.
     */
    public synchronized void add(LogcatEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry must not be null");
        }
        if (deque.size() >= capacity) {
            deque.pollFirst();
        }
        deque.addLast(entry);
    }

    /**
     * Returns a snapshot of all entries currently in the buffer, ordered
     * from oldest to newest.
     */
    public synchronized List<LogcatEntry> snapshot() {
        return new ArrayList<>(deque);
    }

    /**
     * Returns the number of entries currently stored.
     */
    public synchronized int size() {
        return deque.size();
    }

    /**
     * Removes all entries from the buffer.
     */
    public synchronized void clear() {
        deque.clear();
    }

    public int getCapacity() {
        return capacity;
    }
}
