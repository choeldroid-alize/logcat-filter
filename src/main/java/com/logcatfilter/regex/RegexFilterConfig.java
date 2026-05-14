package com.logcatfilter.regex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds configuration for building a {@link RegexFilter}.
 * Supports fluent construction and basic validation.
 */
public class RegexFilterConfig {

    private final List<String> patterns;
    private final RegexFilter.Mode mode;
    private final boolean caseSensitive;

    private RegexFilterConfig(Builder builder) {
        this.patterns      = Collections.unmodifiableList(new ArrayList<>(builder.patterns));
        this.mode          = builder.mode;
        this.caseSensitive = builder.caseSensitive;
    }

    public List<String> getPatterns()      { return patterns; }
    public RegexFilter.Mode getMode()      { return mode; }
    public boolean isCaseSensitive()       { return caseSensitive; }

    /** Builds a ready-to-use {@link RegexFilter} from this config. */
    public RegexFilter buildFilter() {
        List<String> effective = patterns;
        if (!caseSensitive) {
            List<String> ci = new ArrayList<>();
            for (String p : patterns) {
                ci.add("(?i)" + p);
            }
            effective = ci;
        }
        return new RegexFilter(effective, mode);
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private final List<String> patterns = new ArrayList<>();
        private RegexFilter.Mode mode = RegexFilter.Mode.INCLUDE;
        private boolean caseSensitive = true;

        public Builder addPattern(String pattern) {
            if (pattern != null && !pattern.isBlank()) patterns.add(pattern);
            return this;
        }

        public Builder mode(RegexFilter.Mode mode) {
            this.mode = mode;
            return this;
        }

        public Builder caseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public RegexFilterConfig build() {
            if (patterns.isEmpty()) {
                throw new IllegalStateException("At least one pattern is required");
            }
            return new RegexFilterConfig(this);
        }
    }
}
