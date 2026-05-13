package com.logcatfilter.colorscheme;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry that holds named {@link ColorScheme} instances and tracks the active scheme.
 */
public class ColorSchemeRegistry {

    private final Map<String, ColorScheme> schemes = new LinkedHashMap<>();
    private String activeScheme;

    public ColorSchemeRegistry() {
        // Register built-in schemes
        register(ColorScheme.defaultDark());
        register(ColorScheme.plain());
        activeScheme = "dark";
    }

    /**
     * Registers a color scheme. Overwrites any existing scheme with the same name.
     */
    public void register(ColorScheme scheme) {
        if (scheme == null) {
            throw new IllegalArgumentException("ColorScheme must not be null");
        }
        schemes.put(scheme.getName(), scheme);
    }

    /**
     * Sets the active color scheme by name.
     *
     * @throws IllegalArgumentException if the scheme name is not registered
     */
    public void setActive(String name) {
        if (!schemes.containsKey(name)) {
            throw new IllegalArgumentException("Unknown color scheme: " + name);
        }
        this.activeScheme = name;
    }

    /** Returns the currently active {@link ColorScheme}. */
    public ColorScheme getActive() {
        return schemes.get(activeScheme);
    }

    public Optional<ColorScheme> get(String name) {
        return Optional.ofNullable(schemes.get(name));
    }

    public boolean contains(String name) {
        return schemes.containsKey(name);
    }

    public Collection<String> getSchemeNames() {
        return Collections.unmodifiableCollection(schemes.keySet());
    }

    public void remove(String name) {
        if (name.equals(activeScheme)) {
            throw new IllegalStateException("Cannot remove the active color scheme: " + name);
        }
        schemes.remove(name);
    }

    public int size() {
        return schemes.size();
    }
}
