package com.logcatfilter.tag;

import java.util.Objects;

/**
 * Immutable value object representing a user-defined tag annotation.
 */
public final class TagDefinition {

    /** ANSI reset sequence. */
    public static final String ANSI_RESET = "\033[0m";

    private final String tag;
    private final String label;
    private final String ansiColor;

    public TagDefinition(String tag, String label, String ansiColor) {
        this.tag = Objects.requireNonNull(tag, "tag");
        this.label = label != null ? label : tag;
        this.ansiColor = ansiColor != null ? ansiColor : "";
    }

    public String getTag() {
        return tag;
    }

    public String getLabel() {
        return label;
    }

    public String getAnsiColor() {
        return ansiColor;
    }

    /**
     * Wraps the given text with this definition's ANSI color and a reset suffix.
     * If no color is set the text is returned unchanged.
     */
    public String colorize(String text) {
        if (ansiColor == null || ansiColor.isEmpty()) {
            return text;
        }
        return ansiColor + text + ANSI_RESET;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagDefinition)) return false;
        TagDefinition that = (TagDefinition) o;
        return tag.equals(that.tag)
                && Objects.equals(label, that.label)
                && Objects.equals(ansiColor, that.ansiColor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag, label, ansiColor);
    }

    @Override
    public String toString() {
        return "TagDefinition{tag='" + tag + "', label='" + label + "', color='" + ansiColor + "'}";
    }
}
