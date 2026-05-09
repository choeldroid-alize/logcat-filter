package com.logcatfilter.pause;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PauseStateTest {

    private PauseState state;

    @BeforeEach
    void setUp() {
        state = new PauseState();
    }

    @Test
    void initiallyNotPaused() {
        assertFalse(state.isPaused());
        assertEquals(0, state.getMissedEntryCount());
        assertNull(state.getPausedAt());
        assertNull(state.getResumedAt());
    }

    @Test
    void pauseSetsStateAndTimestamp() {
        state.pause();
        assertTrue(state.isPaused());
        assertNotNull(state.getPausedAt());
    }

    @Test
    void pauseIsIdempotent() {
        state.pause();
        var first = state.getPausedAt();
        state.pause();
        assertEquals(first, state.getPausedAt());
    }

    @Test
    void resumeClearsStateAndResetsMissed() {
        state.pause();
        state.incrementMissed();
        state.incrementMissed();
        state.resume();
        assertFalse(state.isPaused());
        assertEquals(0, state.getMissedEntryCount());
        assertNotNull(state.getResumedAt());
    }

    @Test
    void incrementMissedOnlyWhenPaused() {
        state.incrementMissed();
        assertEquals(0, state.getMissedEntryCount());
        state.pause();
        state.incrementMissed();
        state.incrementMissed();
        assertEquals(2, state.getMissedEntryCount());
    }

    @Test
    void pausedDurationIsPositiveWhilePaused() throws InterruptedException {
        state.pause();
        Thread.sleep(10);
        assertTrue(state.getPausedDurationMillis() > 0);
    }

    @Test
    void pausedDurationIsZeroWhenNeverPaused() {
        assertEquals(0L, state.getPausedDurationMillis());
    }

    @Test
    void toStringContainsPausedFlag() {
        state.pause();
        assertTrue(state.toString().contains("paused=true"));
    }
}
