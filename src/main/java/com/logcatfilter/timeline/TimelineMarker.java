package com.logcatfilter.timeline;

import java.time.Instant;
import java.util.Objects;

/**
 * Represents a named marker at a specific point in the log timeline.
 */
public class TimelineMarker {

    private final String id;
    private final String label;
    private final Instant timestamp;
    private final String color;
    private final String note;

    public TimelineMarker(String id, String label, Instant timestamp, String color, String note) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Marker id must not be blank");
        if (label == null || label.isBlank()) throw new IllegalArgumentException("Marker label must not be blank");
        if (timestamp == null) throw new IllegalArgumentException("Marker timestamp must not be null");
        this.id = id;
        this.label = label;
        this.timestamp = timestamp;
        this.color = (color != null && !color.isBlank()) ? color : "WHITE";
        this.note = (note != null) ? note : "";
    }

    public String getId() { return id; }
    public String getLabel() { return label; }
    public Instant getTimestamp() { return timestamp; }
    public String getColor() { return color; }
    public String getNote() { return note; }

    public boolean hasNote() { return !note.isEmpty(); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimelineMarker)) return false;
        TimelineMarker that = (TimelineMarker) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "TimelineMarker{id='" + id + "', label='" + label + "', timestamp=" + timestamp + ", color='" + color + "'}";
    }
}
