package com.logcatfilter.throttle;

/**
 * Configuration for log throttling — limits the rate of log entries
 * processed per time window to prevent UI overload.
 */
public class ThrottleConfig {

    public static final int DEFAULT_MAX_LINES_PER_SECOND = 500;
    public static final long DEFAULT_WINDOW_MS = 1000L;
    public static final boolean DEFAULT_DROP_EXCESS = false;

    private final int maxLinesPerWindow;
    private final long windowMillis;
    private final boolean dropExcess; // if false, buffer excess; if true, discard

    public ThrottleConfig(int maxLinesPerWindow, long windowMillis, boolean dropExcess) {
        if (maxLinesPerWindow <= 0) {
            throw new IllegalArgumentException("maxLinesPerWindow must be positive");
        }
        if (windowMillis <= 0) {
            throw new IllegalArgumentException("windowMillis must be positive");
        }
        this.maxLinesPerWindow = maxLinesPerWindow;
        this.windowMillis = windowMillis;
        this.dropExcess = dropExcess;
    }

    public static ThrottleConfig defaults() {
        return new ThrottleConfig(DEFAULT_MAX_LINES_PER_SECOND, DEFAULT_WINDOW_MS, DEFAULT_DROP_EXCESS);
    }

    public int getMaxLinesPerWindow() {
        return maxLinesPerWindow;
    }

    public long getWindowMillis() {
        return windowMillis;
    }

    public boolean isDropExcess() {
        return dropExcess;
    }

    @Override
    public String toString() {
        return "ThrottleConfig{maxLines=" + maxLinesPerWindow
                + ", windowMs=" + windowMillis
                + ", dropExcess=" + dropExcess + "}";
    }
}
