package com.logcatfilter.highlight;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represents a single highlight rule that maps a regex pattern to an ANSI color code.
 */
public class HighlightRule {

    public enum Color {
        RED("\033[31m"),
        GREEN("\033[32m"),
        YELLOW("\033[33m"),
        BLUE("\033[34m"),
        MAGENTA("\033[35m"),
        CYAN("\033[36m"),
        WHITE("\033[37m"),
        BOLD("\033[1m");

        private final String ansiCode;

        Color(String ansiCode) {
            this.ansiCode = ansiCode;
        }

        public String getAnsiCode() {
            return ansiCode;
        }
    }

    private static final String ANSI_RESET = "\033[0m";

    private final Pattern pattern;
    private final Color color;
    private final String label;

    public HighlightRule(String regex, Color color, String label) {
        Objects.requireNonNull(regex, "regex must not be null");
        Objects.requireNonNull(color, "color must not be null");
        this.pattern = Pattern.compile(regex);
        this.color = color;
        this.label = label != null ? label : "";
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Color getColor() {
        return color;
    }

    public String getLabel() {
        return label;
    }

    public String applyTo(String text) {
        return pattern.matcher(text)
                .replaceAll(match -> color.getAnsiCode() + match.group() + ANSI_RESET);
    }

    @Override
    public String toString() {
        return "HighlightRule{pattern='" + pattern.pattern() + "', color=" + color + ", label='" + label + "'}";
    }
}
