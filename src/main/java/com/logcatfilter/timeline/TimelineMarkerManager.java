package com.logcatfilter.timeline;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages timeline markers: add, remove, query by time range or label.
 */
public class TimelineMarkerManager {

    private final Map<String, TimelineMarker> markersById = new LinkedHashMap<>();

    public TimelineMarker addMarker(String id, String label, Instant timestamp, String color, String note) {
        TimelineMarker marker = new TimelineMarker(id, label, timestamp, color, note);
        markersById.put(id, marker);
        return marker;
    }

    public boolean removeMarker(String id) {
        return markersById.remove(id) != null;
    }

    public Optional<TimelineMarker> findById(String id) {
        return Optional.ofNullable(markersById.get(id));
    }

    public List<TimelineMarker> findByLabel(String label) {
        if (label == null) return Collections.emptyList();
        return markersById.values().stream()
                .filter(m -> m.getLabel().equalsIgnoreCase(label))
                .sorted(Comparator.comparing(TimelineMarker::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<TimelineMarker> findInRange(Instant from, Instant to) {
        if (from == null || to == null || from.isAfter(to)) return Collections.emptyList();
        return markersById.values().stream()
                .filter(m -> !m.getTimestamp().isBefore(from) && !m.getTimestamp().isAfter(to))
                .sorted(Comparator.comparing(TimelineMarker::getTimestamp))
                .collect(Collectors.toList());
    }

    public List<TimelineMarker> getAllMarkers() {
        return markersById.values().stream()
                .sorted(Comparator.comparing(TimelineMarker::getTimestamp))
                .collect(Collectors.toList());
    }

    public Optional<TimelineMarker> nearest(Instant timestamp) {
        if (timestamp == null || markersById.isEmpty()) return Optional.empty();
        return markersById.values().stream()
                .min(Comparator.comparingLong(m -> Math.abs(m.getTimestamp().toEpochMilli() - timestamp.toEpochMilli())));
    }

    public void clear() {
        markersById.clear();
    }

    public int size() {
        return markersById.size();
    }
}
