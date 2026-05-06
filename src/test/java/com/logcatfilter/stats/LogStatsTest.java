package com.logcatfilter.stats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LogStatsTest {

    private LogStats stats;

    @BeforeEach
    void setUp() {
        stats = new LogStats();
    }

    @Test
    void testRecordLineIncrementsTotal() {
        stats.recordLine(LogStats.Level.INFO, true);
        stats.recordLine(LogStats.Level.DEBUG, true);
        assertEquals(2, stats.getTotalLines());
    }

    @Test
    void testPassedLineIncreasesMatchedNotFiltered() {
        stats.recordLine(LogStats.Level.INFO, true);
        assertEquals(1, stats.getMatchedLines());
        assertEquals(0, stats.getFilteredLines());
    }

    @Test
    void testFilteredLineIncreasesFilteredNotMatched() {
        stats.recordLine(LogStats.Level.DEBUG, false);
        assertEquals(0, stats.getMatchedLines());
        assertEquals(1, stats.getFilteredLines());
    }

    @Test
    void testLevelCountAccumulates() {
        stats.recordLine(LogStats.Level.ERROR, true);
        stats.recordLine(LogStats.Level.ERROR, false);
        stats.recordLine(LogStats.Level.WARN, true);
        assertEquals(2, stats.getCountForLevel(LogStats.Level.ERROR));
        assertEquals(1, stats.getCountForLevel(LogStats.Level.WARN));
        assertEquals(0, stats.getCountForLevel(LogStats.Level.INFO));
    }

    @Test
    void testGetLevelSnapshotIsUnmodifiable() {
        stats.recordLine(LogStats.Level.VERBOSE, true);
        Map<LogStats.Level, Long> snapshot = stats.getLevelSnapshot();
        assertEquals(1L, snapshot.get(LogStats.Level.VERBOSE));
        assertThrows(UnsupportedOperationException.class,
            () -> snapshot.put(LogStats.Level.DEBUG, 99L));
    }

    @Test
    void testResetZeroesAllFields() {
        stats.recordLine(LogStats.Level.FATAL, true);
        stats.recordLine(LogStats.Level.ERROR, false);
        stats.reset();
        assertEquals(0, stats.getTotalLines());
        assertEquals(0, stats.getMatchedLines());
        assertEquals(0, stats.getFilteredLines());
        for (LogStats.Level level : LogStats.Level.values()) {
            assertEquals(0, stats.getCountForLevel(level));
        }
    }
}
