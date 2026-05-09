package com.logcatfilter.pause;

import java.time.Instant;

/**
 * Tracks the paused/live state of the logcat stream.
 * When paused, new entries are queued but not displayed.
 */
public class PauseState {

    private boolean paused;
    private Instant pausedAt;
    private Instant resumedAt;
    private int missedEntryCount;

    public PauseState() {
        this.paused = false;
        this.missedEntryCount = 0;
    }

    public void pause() {
        if (!paused) {
            paused = true;
            pausedAt = Instant.now();
            resumedAt = null;
        }
    }

    public void resume() {
        if (paused) {
            paused = false;
            resumedAt = Instant.now();
            missedEntryCount = 0;
        }
    }

    public void incrementMissed() {
        if (paused) {
            missedEntryCount++;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public Instant getPausedAt() {
        return pausedAt;
    }

    public Instant getResumedAt() {
        return resumedAt;
    }

    public int getMissedEntryCount() {
        return missedEntryCount;
    }

    public long getPausedDurationMillis() {
        if (pausedAt == null) return 0L;
        Instant end = (resumedAt != null) ? resumedAt : Instant.now();
        return end.toEpochMilli() - pausedAt.toEpochMilli();
    }

    @Override
    public String toString() {
        return "PauseState{paused=" + paused +
               ", missedEntryCount=" + missedEntryCount +
               ", pausedAt=" + pausedAt + "}";
    }
}
