package com.logcatfilter.buffer;

/**
 * Configuration for the LogBuffer, including capacity and optional scroll-back limit.
 */
public class BufferConfig {

    public static final int DEFAULT_CAPACITY = 5000;
    public static final int MIN_CAPACITY = 100;
    public static final int MAX_CAPACITY = 100_000;

    private final int capacity;
    private final boolean autoClearOnOverflow;

    private BufferConfig(Builder builder) {
        this.capacity = builder.capacity;
        this.autoClearOnOverflow = builder.autoClearOnOverflow;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isAutoClearOnOverflow() {
        return autoClearOnOverflow;
    }

    public static BufferConfig defaults() {
        return new Builder().build();
    }

    public static class Builder {
        private int capacity = DEFAULT_CAPACITY;
        private boolean autoClearOnOverflow = false;

        public Builder capacity(int capacity) {
            if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) {
                throw new IllegalArgumentException(
                    "Capacity must be between " + MIN_CAPACITY + " and " + MAX_CAPACITY);
            }
            this.capacity = capacity;
            return this;
        }

        public Builder autoClearOnOverflow(boolean autoClear) {
            this.autoClearOnOverflow = autoClear;
            return this;
        }

        public BufferConfig build() {
            return new BufferConfig(this);
        }
    }
}
