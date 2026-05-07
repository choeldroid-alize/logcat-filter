package com.logcatfilter.watch;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WatchManagerTest {

    private WatchManager manager;

    @BeforeEach
    void setUp() {
        manager = new WatchManager();
    }

    private LogcatEntry entry(String tag, String message) {
        return new LogcatEntry("2024-01-01 12:00:00.000", 1, 1, "D", tag, message);
    }

    @Test
    void addAndRetrievePattern() {
        WatchPattern p = new WatchPattern("p1", "OutOfMemory", WatchPattern.AlertLevel.CRITICAL, "OOM");
        manager.addPattern(p);
        assertEquals(1, manager.size());
        assertSame(p, manager.getPattern("p1"));
    }

    @Test
    void removePattern() {
        manager.addPattern(new WatchPattern("p1", "crash", WatchPattern.AlertLevel.ERROR, null));
        assertTrue(manager.removePattern("p1"));
        assertEquals(0, manager.size());
        assertFalse(manager.removePattern("p1"));
    }

    @Test
    void evaluateMatchesCorrectPattern() {
        manager.addPattern(new WatchPattern("oom", "OutOfMemory", WatchPattern.AlertLevel.CRITICAL, "OOM"));
        manager.addPattern(new WatchPattern("anr", "ANR in", WatchPattern.AlertLevel.ERROR, "ANR"));

        List<WatchPattern> matched = manager.evaluate(entry("ActivityManager", "ANR in com.example.app"));
        assertEquals(1, matched.size());
        assertEquals("anr", matched.get(0).getId());
    }

    @Test
    void evaluateFiresListeners() {
        manager.addPattern(new WatchPattern("crash", "FATAL", WatchPattern.AlertLevel.CRITICAL, "Fatal"));
        List<String> fired = new ArrayList<>();
        manager.addAlertListener((e, p) -> fired.add(p.getId()));

        manager.evaluate(entry("System", "FATAL EXCEPTION in thread"));
        assertEquals(1, fired.size());
        assertEquals("crash", fired.get(0));
    }

    @Test
    void disabledPatternDoesNotMatch() {
        WatchPattern p = new WatchPattern("p1", "error", WatchPattern.AlertLevel.ERROR, null);
        p.setEnabled(false);
        manager.addPattern(p);

        List<WatchPattern> matched = manager.evaluate(entry("App", "an error occurred"));
        assertTrue(matched.isEmpty());
    }

    @Test
    void evaluateNullEntryReturnsEmpty() {
        manager.addPattern(new WatchPattern("p1", "test", WatchPattern.AlertLevel.INFO, null));
        assertTrue(manager.evaluate(null).isEmpty());
    }

    @Test
    void invalidPatternThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new WatchPattern("bad", "[invalid", WatchPattern.AlertLevel.INFO, null));
    }

    @Test
    void getAllPatternsIsUnmodifiable() {
        manager.addPattern(new WatchPattern("p1", "foo", WatchPattern.AlertLevel.INFO, null));
        assertThrows(UnsupportedOperationException.class, () -> manager.getAllPatterns().clear());
    }
}
