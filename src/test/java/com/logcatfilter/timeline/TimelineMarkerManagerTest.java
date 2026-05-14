package com.logcatfilter.timeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TimelineMarkerManagerTest {

    private TimelineMarkerManager manager;
    private final Instant t1 = Instant.ofEpochSecond(1000);
    private final Instant t2 = Instant.ofEpochSecond(2000);
    private final Instant t3 = Instant.ofEpochSecond(3000);

    @BeforeEach
    void setUp() {
        manager = new TimelineMarkerManager();
    }

    @Test
    void addAndFindById() {
        manager.addMarker("m1", "Start", t1, "GREEN", "boot complete");
        Optional<TimelineMarker> result = manager.findById("m1");
        assertTrue(result.isPresent());
        assertEquals("Start", result.get().getLabel());
        assertEquals("GREEN", result.get().getColor());
        assertEquals("boot complete", result.get().getNote());
        assertTrue(result.get().hasNote());
    }

    @Test
    void removeMarker() {
        manager.addMarker("m1", "Start", t1, null, null);
        assertTrue(manager.removeMarker("m1"));
        assertFalse(manager.findById("m1").isPresent());
        assertFalse(manager.removeMarker("m1"));
    }

    @Test
    void findByLabel_caseInsensitive() {
        manager.addMarker("m1", "Crash", t1, "RED", "");
        manager.addMarker("m2", "crash", t2, "RED", "");
        manager.addMarker("m3", "Start", t3, "GREEN", "");
        List<TimelineMarker> crashes = manager.findByLabel("CRASH");
        assertEquals(2, crashes.size());
    }

    @Test
    void findInRange_returnsMarkersInWindow() {
        manager.addMarker("m1", "A", t1, "WHITE", "");
        manager.addMarker("m2", "B", t2, "WHITE", "");
        manager.addMarker("m3", "C", t3, "WHITE", "");
        List<TimelineMarker> range = manager.findInRange(t1, t2);
        assertEquals(2, range.size());
        assertEquals("m1", range.get(0).getId());
        assertEquals("m2", range.get(1).getId());
    }

    @Test
    void findInRange_invalidRangeReturnsEmpty() {
        manager.addMarker("m1", "A", t1, "WHITE", "");
        assertTrue(manager.findInRange(t3, t1).isEmpty());
    }

    @Test
    void nearest_findsClosestMarker() {
        manager.addMarker("m1", "A", t1, "WHITE", "");
        manager.addMarker("m2", "B", t3, "WHITE", "");
        Instant query = Instant.ofEpochSecond(2800);
        Optional<TimelineMarker> nearest = manager.nearest(query);
        assertTrue(nearest.isPresent());
        assertEquals("m2", nearest.get().getId());
    }

    @Test
    void getAllMarkers_sortedByTimestamp() {
        manager.addMarker("m3", "C", t3, "WHITE", "");
        manager.addMarker("m1", "A", t1, "WHITE", "");
        manager.addMarker("m2", "B", t2, "WHITE", "");
        List<TimelineMarker> all = manager.getAllMarkers();
        assertEquals(3, all.size());
        assertEquals("m1", all.get(0).getId());
        assertEquals("m2", all.get(1).getId());
        assertEquals("m3", all.get(2).getId());
    }

    @Test
    void defaultColorIsWhiteWhenNull() {
        manager.addMarker("m1", "A", t1, null, null);
        assertEquals("WHITE", manager.findById("m1").get().getColor());
    }

    @Test
    void markerConstructor_throwsOnBlankId() {
        assertThrows(IllegalArgumentException.class,
                () -> new TimelineMarker("", "label", t1, "RED", ""));
    }

    @Test
    void clearResetsState() {
        manager.addMarker("m1", "A", t1, "WHITE", "");
        manager.clear();
        assertEquals(0, manager.size());
    }
}
