package com.logcatfilter.alias;

import java.util.*;

/**
 * Registry for managing tag/filter aliases.
 * Allows users to define short names that expand to longer filter expressions.
 */
public class AliasRegistry {

    private final Map<String, String> aliases = new LinkedHashMap<>();

    /**
     * Registers an alias mapping a short name to an expansion.
     *
     * @param name      the alias name (e.g. "err")
     * @param expansion the full expression it expands to (e.g. "level:ERROR")
     * @throws IllegalArgumentException if name is null/blank or expansion is null/blank
     */
    public void register(String name, String expansion) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Alias name must not be blank");
        }
        if (expansion == null || expansion.isBlank()) {
            throw new IllegalArgumentException("Alias expansion must not be blank");
        }
        aliases.put(name.trim(), expansion.trim());
    }

    /**
     * Removes an alias by name.
     *
     * @param name the alias to remove
     * @return true if the alias existed and was removed
     */
    public boolean remove(String name) {
        if (name == null) return false;
        return aliases.remove(name.trim()) != null;
    }

    /**
     * Resolves an alias name to its expansion.
     *
     * @param name the alias name
     * @return the expansion, or empty if not found
     */
    public Optional<String> resolve(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(aliases.get(name.trim()));
    }

    /**
     * Expands all known aliases found in the given input string.
     * Replaces each occurrence of a registered alias token with its expansion.
     *
     * @param input the raw filter/tag string
     * @return the string with aliases substituted
     */
    public String expand(String input) {
        if (input == null || input.isBlank()) return input;
        String result = input;
        for (Map.Entry<String, String> entry : aliases.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Returns an unmodifiable view of all registered aliases.
     */
    public Map<String, String> getAll() {
        return Collections.unmodifiableMap(aliases);
    }

    /**
     * Clears all registered aliases.
     */
    public void clear() {
        aliases.clear();
    }

    public int size() {
        return aliases.size();
    }
}
