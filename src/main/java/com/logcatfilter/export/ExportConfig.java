package com.logcatfilter.export;

/**
 * Configuration for log export operations.
 */
public class ExportConfig {

    public enum Format {
        PLAIN_TEXT,
        CSV,
        JSON
    }

    private final Format format;
    private final String outputPath;
    private final boolean includeTimestamp;
    private final boolean includeTag;
    private final boolean includePid;
    private final int maxLines;

    private ExportConfig(Builder builder) {
        this.format = builder.format;
        this.outputPath = builder.outputPath;
        this.includeTimestamp = builder.includeTimestamp;
        this.includeTag = builder.includeTag;
        this.includePid = builder.includePid;
        this.maxLines = builder.maxLines;
    }

    public Format getFormat() { return format; }
    public String getOutputPath() { return outputPath; }
    public boolean isIncludeTimestamp() { return includeTimestamp; }
    public boolean isIncludeTag() { return includeTag; }
    public boolean isIncludePid() { return includePid; }
    public int getMaxLines() { return maxLines; }

    public static Builder builder(String outputPath) {
        return new Builder(outputPath);
    }

    public static class Builder {
        private final String outputPath;
        private Format format = Format.PLAIN_TEXT;
        private boolean includeTimestamp = true;
        private boolean includeTag = true;
        private boolean includePid = false;
        private int maxLines = Integer.MAX_VALUE;

        public Builder(String outputPath) {
            if (outputPath == null || outputPath.isBlank()) {
                throw new IllegalArgumentException("Output path must not be blank");
            }
            this.outputPath = outputPath;
        }

        public Builder format(Format format) { this.format = format; return this; }
        public Builder includeTimestamp(boolean v) { this.includeTimestamp = v; return this; }
        public Builder includeTag(boolean v) { this.includeTag = v; return this; }
        public Builder includePid(boolean v) { this.includePid = v; return this; }
        public Builder maxLines(int maxLines) {
            if (maxLines <= 0) throw new IllegalArgumentException("maxLines must be positive");
            this.maxLines = maxLines;
            return this;
        }

        public ExportConfig build() { return new ExportConfig(this); }
    }
}
