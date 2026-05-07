package com.logcatfilter.dedup;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class DeduplicationEngineTest {

    private LogcatEntry entryA;
    private LogcatEntry entryB;
    private LogcatEntry entryASameMessage;

    @BeforeEach
    void setUp() {
        entryA = new LogcatEntry("12-01 10:00:00.000", 1000, 1000, "D", "MyTag", "Hello world");
        entryB = new LogcatEntry("12-01 10:00:01.000", 1000, 1000, "I", "OtherTag", "Different message");
        entryASameMessage = new LogcatEntry("12-01 10:00:02.000", 1001, 1001, "W", "AnotherTag", "Hello world");
    }

    @Test
    void firstOccurrenceIsAlwaysEmitted() {
        DeduplicationEngine engine = new DeduplicationEngine(new DeduplicationConfig());
        Optional<LogcatEntry> result = engine.process(entryA);
        assertTrue(result.isPresent());
        assertEquals(entryA, result.get());
    }

    @Test
    void duplicateIsSupressedWhenEnabled() {
        DeduplicationEngine engine = new DeduplicationEngine(new DeduplicationConfig());
        engine.process(entryA);
        Optional<LogcatEntry> result = engine.process(entryA);
        assertFalse(result.isPresent());
    }

    @Test
    void occurrenceCountIncreasesOnDuplicate() {
        DeduplicationEngine engine = new DeduplicationEngine(new DeduplicationConfig());
        engine.process(entryA);
        engine.process(entryA);
        engine.process(entryA);
        assertEquals(3, engine.getOccurrenceCount(entryA));
    }

    @Test
    void distinctEntriesAreNotDeduplicated() {
        DeduplicationEngine engine = new DeduplicationEngine(new DeduplicationConfig());
        engine.process(entryA);
        Optional<LogcatEntry> result = engine.process(entryB);
        assertTrue(result.isPresent());
    }

    @Test
    void disabledEnginePassesAllEntries() {
        DeduplicationConfig config = new DeduplicationConfig(false, 50, true, false);
        DeduplicationEngine engine = new DeduplicationEngine(config);
        engine.process(entryA);
        Optional<LogcatEntry> result = engine.process(entryA);
        assertTrue(result.isPresent());
    }

    @Test
    void matchOnMessageOnlyIgnoresLevelAndTag() {
        DeduplicationConfig config = new DeduplicationConfig(true, 50, true, true);
        DeduplicationEngine engine = new DeduplicationEngine(config);
        engine.process(entryA);
        Optional<LogcatEntry> result = engine.process(entryASameMessage);
        assertFalse(result.isPresent(), "Same message with different tag/level should be deduplicated");
    }

    @Test
    void resetClearsWindow() {
        DeduplicationEngine engine = new DeduplicationEngine(new DeduplicationConfig());
        engine.process(entryA);
        engine.reset();
        assertEquals(0, engine.getWindowOccupancy());
        Optional<LogcatEntry> result = engine.process(entryA);
        assertTrue(result.isPresent(), "Entry should be emitted again after reset");
    }

    @Test
    void windowSizeEvictsOldEntries() {
        DeduplicationConfig config = new DeduplicationConfig(true, 2, false, false);
        DeduplicationEngine engine = new DeduplicationEngine(config);
        engine.process(entryA);
        engine.process(entryB);
        // Adding a third entry evicts entryA from the window
        LogcatEntry entryC = new LogcatEntry("12-01 10:00:03.000", 1000, 1000, "E", "Tag", "Third");
        engine.process(entryC);
        // entryA should no longer be in window, so it should be emitted again
        Optional<LogcatEntry> result = engine.process(entryA);
        assertTrue(result.isPresent(), "Evicted entry should be treated as new");
    }

    @Test
    void invalidWindowSizeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new DeduplicationConfig(true, 0, true, false));
    }
}
