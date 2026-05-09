package com.logcatfilter.pause;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PauseManagerTest {

    private PauseManager manager;

    @BeforeEach
    void setUp() {
        manager = new PauseManager(5);
    }

    @Test
    void constructorRejectsNonPositiveQueueSize() {
        assertThrows(IllegalArgumentException.class, () -> new PauseManager(0));
        assertThrows(IllegalArgumentException.class, () -> new PauseManager(-1));
    }

    @Test
    void offerReturnsTrueWhenNotPaused() {
        LogcatEntry entry = mock(LogcatEntry.class);
        assertTrue(manager.offer(entry));
        assertEquals(0, manager.getQueueSize());
    }

    @Test
    void offerReturnsFalseAndQueuesWhenPaused() {
        manager.pause();
        LogcatEntry entry = mock(LogcatEntry.class);
        assertFalse(manager.offer(entry));
        assertEquals(1, manager.getQueueSize());
    }

    @Test
    void resumeReturnsDrainedEntries() {
        manager.pause();
        LogcatEntry e1 = mock(LogcatEntry.class);
        LogcatEntry e2 = mock(LogcatEntry.class);
        manager.offer(e1);
        manager.offer(e2);
        List<LogcatEntry> drained = manager.resume();
        assertEquals(2, drained.size());
        assertSame(e1, drained.get(0));
        assertSame(e2, drained.get(1));
        assertEquals(0, manager.getQueueSize());
        assertFalse(manager.isPaused());
    }

    @Test
    void queueDoesNotExceedMaxSize() {
        manager.pause();
        for (int i = 0; i < 10; i++) {
            manager.offer(mock(LogcatEntry.class));
        }
        assertTrue(manager.getQueueSize() <= manager.getMaxQueueSize());
    }

    @Test
    void missedCountTracksOfferedEntriesWhilePaused() {
        manager.pause();
        for (int i = 0; i < 3; i++) {
            manager.offer(mock(LogcatEntry.class));
        }
        assertEquals(3, manager.getState().getMissedEntryCount());
    }

    @Test
    void resumeAfterResumeReturnsEmptyList() {
        manager.pause();
        manager.resume();
        List<LogcatEntry> drained = manager.resume();
        assertTrue(drained.isEmpty());
    }
}
