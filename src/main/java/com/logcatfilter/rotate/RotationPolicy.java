package com.logcatfilter.rotate;

import java.time.Duration;
import java.util.Objects;

/**
 * Defines the policy for rotating (archiving/clearing) the log buffer.
 * Rotation can be triggered by size, age, or both.
 */
public class RotationPolicy {

    public enum TriggerMode {
        SIZE_ONLY,
        AGE_ONLY,
        EITHER,
        BOTH
    }

    private final int maxEntries;
    private final Duration maxAge;
    private final TriggerMode triggerMode;
    private final boolean archiveOnRotate;

    private RotationPolicy(Builder builder) {
        this.maxEntries = builder.maxEntries;
        this.maxAge = builder.maxAge;
        this.triggerMode = builder.triggerMode;
        this.archiveOnRotate = builder.archiveOnRotate;
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public Duration getMaxAge() {
        return maxAge;
    }

    public TriggerMode getTriggerMode() {
        return triggerMode;
    }

    public boolean isArchiveOnRotate() {
        return archiveOnRotate;
    }

    public boolean shouldRotateBySize(int currentSize) {
        if (triggerMode == TriggerMode.AGE_ONLY) return false;
        return maxEntries > 0 && currentSize >= maxEntries;
    }

    public boolean shouldRotateByAge(Duration currentAge) {
        if (triggerMode == TriggerMode.SIZE_ONLY) return false;
        return maxAge != null && !currentAge.isNegative() && currentAge.compareTo(maxAge) >= 0;
    }

    public boolean shouldRotate(int currentSize, Duration currentAge) {
        boolean bySize = shouldRotateBySize(currentSize);
        boolean byAge = shouldRotateByAge(currentAge);
        if (triggerMode == TriggerMode.BOTH) return bySize && byAge;
        return bySize || byAge;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int maxEntries = 10_000;
        private Duration maxAge = Duration.ofHours(1);
        private TriggerMode triggerMode = TriggerMode.EITHER;
        private boolean archiveOnRotate = false;

        public Builder maxEntries(int maxEntries) {
            if (maxEntries <= 0) throw new IllegalArgumentException("maxEntries must be positive");
            this.maxEntries = maxEntries;
            return this;
        }

        public Builder maxAge(Duration maxAge) {
            this.maxAge = Objects.requireNonNull(maxAge, "maxAge must not be null");
            return this;
        }

        public Builder triggerMode(TriggerMode triggerMode) {
            this.triggerMode = Objects.requireNonNull(triggerMode);
            return this;
        }

        public Builder archiveOnRotate(boolean archiveOnRotate) {
            this.archiveOnRotate = archiveOnRotate;
            return this;
        }

        public RotationPolicy build() {
            return new RotationPolicy(this);
        }
    }
}
