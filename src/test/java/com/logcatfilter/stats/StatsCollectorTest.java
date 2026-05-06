package com.logcatfilter.stats;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatsCollectorTest {

    private StatsCollector collector;

    @BeforeEach
    void setUp() {
        collector = new StatsCollector();
    }

    private LogcatEntry entry(String level) {
        return new LogcatEntry("2024-01-01 10:00:00.000", "1234", "5678", level, "MyTag", "message");
    }

    @Test
    void testInitialStatsAreZero() {
        LogStats stats = collector.getStats();
        assertEquals(0, stats.getTotalLines());
        assertEquals(0, stats.getMatchedLines());
        assertEquals(0, stats.getFilteredLines());
    }

    @Test
    void testCollectPassedEntry() {
        collector.collect(entry("I"), true);
        assertEquals(1, collector.getStats().getTotalLines());
        assertEquals(1, collector.getStats().getMatchedLines());
        assertEquals(0, collector.getStats().getFilteredLines());
    }

    @Test
    void testCollectFilteredEntry() {
        collector.collect(entry("D"), false);
        assertEquals(1, collector.getStats().getTotalLines());
        assertEquals(0, collector.getStats().getMatchedLines());
        assertEquals(1, collector.getStats().getFilteredLines());
    }

    @Test
    void testLevelCountsAreTracked() {
        collector.collect(entry("E"), true);
        collector.collect(entry("E"), false);
        collector.collect(entry("W"), true);
        assertEquals(2, collector.getStats().getCountForLevel(LogStats.Level.ERROR));
        assertEquals(1, collector.getStats().getCountForLevel(LogStats.Level.WARN));
    }

    @Test
    void testNullEntryIsIgnored() {
        assertDoesNotThrow(() -> collector.collect(null, true));
        assertEquals(0, collector.getStats().getTotalLines());
    }

    @Test
    void testUnknownLevelMappedCorrectly() {
        collector.collect(entry("X"), true);
        assertEquals(1, collector.getStats().getCountForLevel(LogStats.Level.UNKNOWN));
    }

    @Test
    void testSummaryFormat() {
        collector.collect(entry("I"), true);
        collector.collect(entry("E"), true);
        collector.collect(entry("D"), false);
        String summary = collector.getSummary();
        assertTrue(summary.contains("Lines: 3"));
        assertTrue(summary.contains("Shown: 2"));
        assertTrue(summary.contains("Errors: 1"));
    }

    @Test
    void testResetClearsAllCounts() {
        collector.collect(entry("I"), true);
        collector.collect(entry("E"), true);
        collector.getStats().reset();
        assertEquals(0, collector.getStats().getTotalLines());
        assertEquals(0, collector.getStats().getMatchedLines());
        assertEquals(0, collector.getStats().getCountForLevel(LogStats.Level.ERROR));
    }
}
