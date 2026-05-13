package com.logcatfilter.colorscheme;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a named color scheme mapping log levels and UI elements to ANSI color codes.
 */
public class ColorScheme {

    public enum Element {
        LEVEL_VERBOSE, LEVEL_DEBUG, LEVEL_INFO, LEVEL_WARN, LEVEL_ERROR, LEVEL_FATAL,
        TIMESTAMP, TAG, PID, MESSAGE, HIGHLIGHT, BOOKMARK, SEARCH_MATCH, BACKGROUND
    }

    private final String name;
    private final Map<Element, String> colorMap;

    public ColorScheme(String name, Map<Element, String> colorMap) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Color scheme name must not be blank");
        }
        this.name = name;
        this.colorMap = Collections.unmodifiableMap(new HashMap<>(colorMap));
    }

    public String getName() {
        return name;
    }

    /**
     * Returns the ANSI escape code for the given element, or empty string if not defined.
     */
    public String getColor(Element element) {
        return colorMap.getOrDefault(element, "");
    }

    public boolean hasColor(Element element) {
        return colorMap.containsKey(element);
    }

    public Map<Element, String> getColorMap() {
        return colorMap;
    }

    /** Builds a default "dark" color scheme suitable for dark terminals. */
    public static ColorScheme defaultDark() {
        Map<Element, String> m = new HashMap<>();
        m.put(Element.LEVEL_VERBOSE, "\u001B[37m");   // white
        m.put(Element.LEVEL_DEBUG,   "\u001B[36m");   // cyan
        m.put(Element.LEVEL_INFO,    "\u001B[32m");   // green
        m.put(Element.LEVEL_WARN,    "\u001B[33m");   // yellow
        m.put(Element.LEVEL_ERROR,   "\u001B[31m");   // red
        m.put(Element.LEVEL_FATAL,   "\u001B[35m");   // magenta
        m.put(Element.TIMESTAMP,     "\u001B[90m");   // dark gray
        m.put(Element.TAG,           "\u001B[94m");   // bright blue
        m.put(Element.PID,           "\u001B[90m");   // dark gray
        m.put(Element.MESSAGE,       "\u001B[0m");    // reset
        m.put(Element.HIGHLIGHT,     "\u001B[43m");   // yellow bg
        m.put(Element.BOOKMARK,      "\u001B[44m");   // blue bg
        m.put(Element.SEARCH_MATCH,  "\u001B[42m");   // green bg
        return new ColorScheme("dark", m);
    }

    /** Builds a minimal "plain" scheme with no ANSI codes (useful for file export). */
    public static ColorScheme plain() {
        return new ColorScheme("plain", new HashMap<>());
    }

    @Override
    public String toString() {
        return "ColorScheme{name='" + name + "', entries=" + colorMap.size() + "}";
    }
}
