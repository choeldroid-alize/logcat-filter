package com.logcatfilter.buffer;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogBufferTest {

    private LogBuffer buffer;

    private LogcatEntry entry(String msg) {
        return new LogcatEntry("01-01 00:00:00.000", 1, 1, "D", "TAG", msg);
    }

    @BeforeEach
    void setUp() {
        buffer = new LogBuffer(3);
    }

    @Test
    void newBufferIsEmpty() {
        assertEquals(0, buffer.size());
        assertTrue(buffer.snapshot().isEmpty());
    }

    @Test
    void addAndRetrieve() {
        buffer.add(entry("A"));
        buffer.add(entry("B"));
        List<LogcatEntry> snap = buffer.snapshot();
        assertEquals(2, snap.size());
        assertEquals("A", snap.get(0).getMessage());
        assertEquals("B", snap.get(1).getMessage());
    }

    @Test
    void evictsOldestWhenFull() {
        buffer.add(entry("A"));
        buffer.add(entry("B"));
        buffer.add(entry("C"));
        buffer.add(entry("D")); // evicts A
        List<LogcatEntry> snap = buffer.snapshot();
        assertEquals(3, snap.size());
        assertEquals("B", snap.get(0).getMessage());
        assertEquals("D", snap.get(2).getMessage());
    }

    @Test
    void clearEmptiesBuffer() {
        buffer.add(entry("A"));
        buffer.clear();
        assertEquals(0, buffer.size());
    }

    @Test
    void rejectsNullEntry() {
        assertThrows(IllegalArgumentException.class, () -> buffer.add(null));
    }

    @Test
    void rejectsNonPositiveCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new LogBuffer(0));
        assertThrows(IllegalArgumentException.class, () -> new LogBuffer(-5));
    }

    @Test
    void snapshotIsIndependentCopy() {
        buffer.add(entry("A"));
        List<LogcatEntry> snap = buffer.snapshot();
        buffer.add(entry("B"));
        assertEquals(1, snap.size()); // snapshot unaffected
    }
}
