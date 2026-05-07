package com.logcatfilter.dedup;

/**
 * Configuration for log line deduplication behavior.
 */
public class DeduplicationConfig {

    private boolean enabled;
    private int windowSize;
    private boolean countOccurrences;
    private boolean matchOnMessageOnly;

    public DeduplicationConfig() {
        this.enabled = true;
        this.windowSize = 50;
        this.countOccurrences = true;
        this.matchOnMessageOnly = false;
    }

    public DeduplicationConfig(boolean enabled, int windowSize, boolean countOccurrences, boolean matchOnMessageOnly) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }
        this.enabled = enabled;
        this.windowSize = windowSize;
        this.countOccurrences = countOccurrences;
        this.matchOnMessageOnly = matchOnMessageOnly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be positive");
        }
        this.windowSize = windowSize;
    }

    public boolean isCountOccurrences() {
        return countOccurrences;
    }

    public void setCountOccurrences(boolean countOccurrences) {
        this.countOccurrences = countOccurrences;
    }

    public boolean isMatchOnMessageOnly() {
        return matchOnMessageOnly;
    }

    public void setMatchOnMessageOnly(boolean matchOnMessageOnly) {
        this.matchOnMessageOnly = matchOnMessageOnly;
    }

    @Override
    public String toString() {
        return "DeduplicationConfig{enabled=" + enabled +
                ", windowSize=" + windowSize +
                ", countOccurrences=" + countOccurrences +
                ", matchOnMessageOnly=" + matchOnMessageOnly + "}";
    }
}
