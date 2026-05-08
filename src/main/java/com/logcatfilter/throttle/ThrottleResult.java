package com.logcatfilter.throttle;

/**
 * Outcome of a throttle check for a single log entry.
 */
public class ThrottleResult {

    public enum Action {
        ALLOW,
        BUFFER,
        DROP
    }

    private final Action action;
    private final int currentCount;
    private final int limit;
    private final long windowResetMs;

    public ThrottleResult(Action action, int currentCount, int limit, long windowResetMs) {
        this.action = action;
        this.currentCount = currentCount;
        this.limit = limit;
        this.windowResetMs = windowResetMs;
    }

    public Action getAction() {
        return action;
    }

    public boolean isAllowed() {
        return action == Action.ALLOW;
    }

    public boolean isDropped() {
        return action == Action.DROP;
    }

    public int getCurrentCount() {
        return currentCount;
    }

    public int getLimit() {
        return limit;
    }

    public long getWindowResetMs() {
        return windowResetMs;
    }

    @Override
    public String toString() {
        return "ThrottleResult{action=" + action
                + ", count=" + currentCount + "/" + limit
                + ", resetIn=" + windowResetMs + "ms}";
    }
}
