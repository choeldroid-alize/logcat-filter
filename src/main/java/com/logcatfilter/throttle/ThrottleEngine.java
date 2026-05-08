package com.logcatfilter.throttle;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayDeque;
 import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Enforces rate limiting on incoming log entries based on a sliding window.
 * Thread-safe for single-producer use; external synchronization needed for
 * multi-threaded producers.
 */
public class ThrottleEngine {

    private final ThrottleConfig config;
    private long windowStart;
    private final AtomicInteger windowCount = new AtomicInteger(0);
    private final Deque<LogcatEntry> overflowBuffer = new ArrayDeque<>();

    public ThrottleEngine(ThrottleConfig config) {
        this.config = config;
        this.windowStart = System.currentTimeMillis();
    }

    /**
     * Evaluates whether the given entry should be allowed, buffered, or dropped.
     * Also drains any buffered entries whose window has since passed.
     */
    public synchronized ThrottleResult evaluate(LogcatEntry entry) {
        long now = System.currentTimeMillis();
        maybeResetWindow(now);

        int count = windowCount.incrementAndGet();
        long resetIn = (windowStart + config.getWindowMillis()) - now;

        if (count <= config.getMaxLinesPerWindow()) {
            return new ThrottleResult(ThrottleResult.Action.ALLOW, count, config.getMaxLinesPerWindow(), resetIn);
        }

        if (config.isDropExcess()) {
            return new ThrottleResult(ThrottleResult.Action.DROP, count, config.getMaxLinesPerWindow(), resetIn);
        }

        overflowBuffer.addLast(entry);
        return new ThrottleResult(ThrottleResult.Action.BUFFER, count, config.getMaxLinesPerWindow(), resetIn);
    }

    /**
     * Returns and removes the next buffered entry if the window has reset,
     * or null if the buffer is empty / window still active.
     */
    public synchronized LogcatEntry pollBuffered() {
        long now = System.currentTimeMillis();
        maybeResetWindow(now);
        if (overflowBuffer.isEmpty()) {
            return null;
        }
        // Only release buffered entries once we have capacity again
        if (windowCount.get() < config.getMaxLinesPerWindow()) {
            windowCount.incrementAndGet();
            return overflowBuffer.pollFirst();
        }
        return null;
    }

    public synchronized int getBufferedCount() {
        return overflowBuffer.size();
    }

    public synchronized void reset() {
        windowCount.set(0);
        windowStart = System.currentTimeMillis();
        overflowBuffer.clear();
    }

    private void maybeResetWindow(long now) {
        if (now >= windowStart + config.getWindowMillis()) {
            windowStart = now;
            windowCount.set(0);
        }
    }

    public ThrottleConfig getConfig() {
        return config;
    }
}
