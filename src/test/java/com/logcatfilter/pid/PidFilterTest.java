package com.logcatfilter.pid;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PidFilterTest {

    private PidFilter filter;

    private LogcatEntry entryWithPidTid(int pid, int tid) {
        return new LogcatEntry("2024-01-01 00:00:00.000", pid, tid, "I", "TestTag", "message");
    }

    @BeforeEach
    void setUp() {
        filter = new PidFilter();
    }

    @Test
    void disabledFilterAcceptsAll() {
        assertFalse(filter.isEnabled());
        assertTrue(filter.accepts(entryWithPidTid(1234, 5678)));
    }

    @Test
    void allowlistAcceptsMatchingPid() {
        filter.addPid(1234);
        assertTrue(filter.accepts(entryWithPidTid(1234, 9999)));
        assertFalse(filter.accepts(entryWithPidTid(9999, 9999)));
    }

    @Test
    void allowlistAcceptsMatchingTid() {
        filter.addTid(5678);
        assertTrue(filter.accepts(entryWithPidTid(9999, 5678)));
        assertFalse(filter.accepts(entryWithPidTid(9999, 9998)));
    }

    @Test
    void blocklistRejectsMatchingPid() {
        filter.addPid(1234);
        filter.setMode(PidFilter.Mode.BLOCKLIST);
        assertFalse(filter.accepts(entryWithPidTid(1234, 9999)));
        assertTrue(filter.accepts(entryWithPidTid(9999, 9999)));
    }

    @Test
    void clearResetsFilter() {
        filter.addPid(1234);
        filter.clear();
        assertFalse(filter.isEnabled());
        assertTrue(filter.accepts(entryWithPidTid(1234, 0)));
    }

    @Test
    void configParsesAllowlistPids() {
        PidFilterConfig config = PidFilterConfig.parse("100,200,300");
        assertEquals(3, config.getPids().size());
        assertTrue(config.getPids().contains(100));
        assertEquals(PidFilter.Mode.ALLOWLIST, config.getMode());
    }

    @Test
    void configParsesBlocklistTids() {
        PidFilterConfig config = PidFilterConfig.parse("block:tid:111,222");
        assertEquals(PidFilter.Mode.BLOCKLIST, config.getMode());
        assertEquals(2, config.getTids().size());
        assertTrue(config.getTids().contains(111));
        assertTrue(config.getPids().isEmpty());
    }

    @Test
    void configBuildsWorkingFilter() {
        PidFilterConfig config = PidFilterConfig.parse("42,99");
        PidFilter built = config.buildFilter();
        assertTrue(built.accepts(entryWithPidTid(42, 0)));
        assertFalse(built.accepts(entryWithPidTid(1, 0)));
    }

    @Test
    void emptyConfigProducesDisabledFilter() {
        PidFilterConfig config = PidFilterConfig.parse("");
        PidFilter built = config.buildFilter();
        assertFalse(built.isEnabled());
    }
}
