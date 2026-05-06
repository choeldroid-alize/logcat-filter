package com.logcatfilter.buffer;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BufferManagerTest {

    private LogcatEntry entry(String msg) {
        return new LogcatEntry("01-01 00:00:00.000", 1, 1, "I", "TAG", msg);
    }

    @Test
    void defaultConfigHasExpectedCapacity() {
        BufferConfig cfg = BufferConfig.defaults();
        assertEquals(BufferConfig.DEFAULT_CAPACITY, cfg.getCapacity());
        assertFalse(cfg.isAutoClearOnOverflow());
    }

    @Test
    void builderRejectsOutOfRangeCapacity() {
        assertThrows(IllegalArgumentException.class,
            () -> new BufferConfig.Builder().capacity(50).build());
        assertThrows(IllegalArgumentException.class,
            () -> new BufferConfig.Builder().capacity(200_000).build());
    }

    @Test
    void ingestTracksCounters() {
        BufferConfig cfg = new BufferConfig.Builder().capacity(200).build();
        BufferManager mgr = new BufferManager(cfg);
        mgr.ingest(entry("X"));
        mgr.ingest(entry("Y"));
        assertEquals(2, mgr.getTotalIngested());
        assertEquals(0, mgr.getTotalEvicted());
        assertEquals(2, mgr.currentSize());
    }

    @Test
    void autoClearOnOverflowClearsBeforeAdd() {
        BufferConfig cfg = new BufferConfig.Builder()
            .capacity(100)
            .autoClearOnOverflow(true)
            .build();
        BufferManager mgr = new BufferManager(cfg);
        for (int i = 0; i < 100; i++) {
            mgr.ingest(entry("msg" + i));
        }
        assertEquals(100, mgr.currentSize());
        // next ingest should trigger clear
        mgr.ingest(entry("overflow"));
        assertEquals(1, mgr.currentSize());
        assertEquals("overflow", mgr.getEntries().get(0).getMessage());
        assertEquals(101, mgr.getTotalIngested());
    }

    @Test
    void clearResetsSize() {
        BufferConfig cfg = BufferConfig.defaults();
        BufferManager mgr = new BufferManager(cfg);
        mgr.ingest(entry("A"));
        mgr.ingest(entry("B"));
        mgr.clear();
        assertEquals(0, mgr.currentSize());
        assertEquals(2, mgr.getTotalEvicted());
    }
}
