package com.logcatfilter.pipeline;

import com.logcatfilter.parser.LogcatEntry;
import java.util.Objects;

/**
 * Immutable value object produced by {@link ProcessingPipeline} for each
 * accepted log entry, carrying the original entry, its highlighted
 * representation, and any pipeline metadata.
 */
public final class PipelineResult {

    private final LogcatEntry entry;
    private final String highlightedText;
    private final boolean throttleSuppressed;

    public PipelineResult(LogcatEntry entry, String highlightedText, boolean throttleSuppressed) {
        this.entry = Objects.requireNonNull(entry, "entry must not be null");
        this.highlightedText = Objects.requireNonNull(highlightedText, "highlightedText must not be null");
        this.throttleSuppressed = throttleSuppressed;
    }

    /** The original, parsed log entry. */
    public LogcatEntry getEntry() {
        return entry;
    }

    /**
     * The entry's message with ANSI highlight codes applied (or plain text
     * if no rules matched).
     */
    public String getHighlightedText() {
        return highlightedText;
    }

    /**
     * {@code true} when the throttle engine allowed the entry through but
     * marked it as part of a suppressed burst (e.g. "… N similar lines").
     */
    public boolean isThrottleSuppressed() {
        return throttleSuppressed;
    }

    @Override
    public String toString() {
        return "PipelineResult{" +
               "tag='" + entry.getTag() + '\'' +
               ", level=" + entry.getLevel() +
               ", throttleSuppressed=" + throttleSuppressed +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PipelineResult)) return false;
        PipelineResult that = (PipelineResult) o;
        return throttleSuppressed == that.throttleSuppressed &&
               Objects.equals(entry, that.entry) &&
               Objects.equals(highlightedText, that.highlightedText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entry, highlightedText, throttleSuppressed);
    }
}
