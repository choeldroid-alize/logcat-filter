package com.logcatfilter.watch;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Manages a collection of WatchPatterns and evaluates them against incoming LogcatEntry objects.
 * Fires registered alert callbacks when a pattern matches.
 */
public class WatchManager {

    private final Map<String, WatchPattern> patterns = new LinkedHashMap<>();
    private final List<BiConsumer<LogcatEntry, WatchPattern>> alertListeners = new ArrayList<>();

    public void addPattern(WatchPattern pattern) {
        if (pattern == null) throw new IllegalArgumentException("pattern must not be null");
        patterns.put(pattern.getId(), pattern);
    }

    public boolean removePattern(String id) {
        return patterns.remove(id) != null;
    }

    public WatchPattern getPattern(String id) {
        return patterns.get(id);
    }

    public List<WatchPattern> getAllPatterns() {
        return Collections.unmodifiableList(new ArrayList<>(patterns.values()));
    }

    public void addAlertListener(BiConsumer<LogcatEntry, WatchPattern> listener) {
        if (listener != null) alertListeners.add(listener);
    }

    /**
     * Evaluates all enabled patterns against the given entry.
     * Fires listeners for every matching pattern.
     *
     * @return list of patterns that matched
     */
    public List<WatchPattern> evaluate(LogcatEntry entry) {
        if (entry == null) return Collections.emptyList();
        List<WatchPattern> matched = new ArrayList<>();
        String searchText = buildSearchText(entry);
        for (WatchPattern pattern : patterns.values()) {
            if (pattern.matches(searchText)) {
                matched.add(pattern);
                for (BiConsumer<LogcatEntry, WatchPattern> listener : alertListeners) {
                    listener.accept(entry, pattern);
                }
            }
        }
        return Collections.unmodifiableList(matched);
    }

    private String buildSearchText(LogcatEntry entry) {
        StringBuilder sb = new StringBuilder();
        if (entry.getTag() != null) sb.append(entry.getTag()).append(' ');
        if (entry.getMessage() != null) sb.append(entry.getMessage());
        return sb.toString();
    }

    public int size() {
        return patterns.size();
    }

    public void clear() {
        patterns.clear();
    }
}
