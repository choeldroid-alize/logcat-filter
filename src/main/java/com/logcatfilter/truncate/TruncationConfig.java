package com.logcatfilter.truncate;

/**
 * Configuration for log message truncation behavior.
 */
public class TruncationConfig {

    public static final int DEFAULT_MAX_LENGTH = 512;
    public static final String DEFAULT_ELLIPSIS = "...";

    private final int maxLength;
    private final String ellipsis;
    private final boolean truncateTagField;
    private final boolean truncateMessageField;
    private final boolean enabled;

    private TruncationConfig(Builder builder) {
        this.maxLength = builder.maxLength;
        this.ellipsis = builder.ellipsis;
        this.truncateTagField = builder.truncateTagField;
        this.truncateMessageField = builder.truncateMessageField;
        this.enabled = builder.enabled;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getEllipsis() {
        return ellipsis;
    }

    public boolean isTruncateTagField() {
        return truncateTagField;
    }

    public boolean isTruncateMessageField() {
        return truncateMessageField;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static TruncationConfig defaultConfig() {
        return builder().build();
    }

    public static class Builder {
        private int maxLength = DEFAULT_MAX_LENGTH;
        private String ellipsis = DEFAULT_ELLIPSIS;
        private boolean truncateTagField = false;
        private boolean truncateMessageField = true;
        private boolean enabled = true;

        public Builder maxLength(int maxLength) {
            if (maxLength <= 0) throw new IllegalArgumentException("maxLength must be positive");
            this.maxLength = maxLength;
            return this;
        }

        public Builder ellipsis(String ellipsis) {
            if (ellipsis == null) throw new IllegalArgumentException("ellipsis must not be null");
            this.ellipsis = ellipsis;
            return this;
        }

        public Builder truncateTagField(boolean truncateTagField) {
            this.truncateTagField = truncateTagField;
            return this;
        }

        public Builder truncateMessageField(boolean truncateMessageField) {
            this.truncateMessageField = truncateMessageField;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public TruncationConfig build() {
            return new TruncationConfig(this);
        }
    }
}
