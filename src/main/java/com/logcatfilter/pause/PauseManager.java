package com.logcatfilter.pause;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayDeque;
 import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Manages pause/resume lifecycle and queues entries received while paused.
 */
public class PauseManager {

    private final PauseState state;
    private final Deque<LogcatEntry> pauseQueue;
    private final int maxQueueSize;

    public PauseManager(int maxQueueSize) {
        if (maxQueueSize <= 0) {
            throw new IllegalArgumentException("maxQueueSize must be positive");
        }
        this.maxQueueSize = maxQueueSize;
        this.state = new PauseState();
        this.pauseQueue = new ArrayDeque<>();
    }

    public void pause() {
        state.pause();
    }

    /**
     * Resumes the stream and returns all queued entries in order.
     */
    public List<LogcatEntry> resume() {
        state.resume();
        List<LogcatEntry> drained = new ArrayList<>(pauseQueue);
        pauseQueue.clear();
        return drained;
    }

    /**
     * Offer an entry. If paused, queues it (up to maxQueueSize).
     * Returns true if the entry should be displayed immediately.
     */
    public boolean offer(LogcatEntry entry) {
        if (!state.isPaused()) {
            return true;
        }
        state.incrementMissed();
        if (pauseQueue.size() < maxQueueSize) {
            pauseQueue.addLast(entry);
        }
        // drop oldest if overflow
        if (pauseQueue.size() > maxQueueSize) {
            pauseQueue.pollFirst();
        }
        return false;
    }

    public boolean isPaused() {
        return state.isPaused();
    }

    public PauseState getState() {
        return state;
    }

    public int getQueueSize() {
        return pauseQueue.size();
    }

    public int getMaxQueueSize() {
        return maxQueueSize;
    }
}
