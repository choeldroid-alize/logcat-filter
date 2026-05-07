package com.logcatfilter.bookmark;

import com.logcatfilter.parser.LogcatEntry;
import java.time.Instant;
import java.util.Objects;

/**
 * Represents a user-defined bookmark on a specific log entry.
 */
public class Bookmark {

    private final String id;
    private final LogcatEntry entry;
    private final String label;
    private final Instant createdAt;

    public Bookmark(String id, LogcatEntry entry, String label) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Bookmark id must not be blank");
        }
        if (entry == null) {
            throw new IllegalArgumentException("Bookmark entry must not be null");
        }
        this.id = id;
        this.entry = entry;
        this.label = (label == null || label.isBlank()) ? "" : label.trim();
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public LogcatEntry getEntry() {
        return entry;
    }

    public String getLabel() {
        return label;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public boolean hasLabel() {
        return !label.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Bookmark)) return false;
        Bookmark other = (Bookmark) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Bookmark{id='" + id + "', label='" + label + "', createdAt=" + createdAt + "}";
    }
}
