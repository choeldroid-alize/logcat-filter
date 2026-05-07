package com.logcatfilter.bookmark;

import com.logcatfilter.parser.LogcatEntry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Manages the lifecycle of bookmarks: creation, retrieval, and removal.
 */
public class BookmarkManager {

    private final Map<String, Bookmark> bookmarks = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(0);

    /**
     * Creates a bookmark for the given entry with an optional label.
     *
     * @param entry the log entry to bookmark
     * @param label optional descriptive label
     * @return the newly created Bookmark
     */
    public Bookmark addBookmark(LogcatEntry entry, String label) {
        String id = "bm-" + idCounter.incrementAndGet();
        Bookmark bookmark = new Bookmark(id, entry, label);
        bookmarks.put(id, bookmark);
        return bookmark;
    }

    /**
     * Removes a bookmark by its id.
     *
     * @param id the bookmark id
     * @return true if a bookmark was removed, false if not found
     */
    public boolean removeBookmark(String id) {
        return bookmarks.remove(id) != null;
    }

    /**
     * Retrieves a bookmark by id.
     */
    public Optional<Bookmark> getBookmark(String id) {
        return Optional.ofNullable(bookmarks.get(id));
    }

    /**
     * Returns all bookmarks ordered by creation time (insertion order via id counter).
     */
    public List<Bookmark> getAllBookmarks() {
        return bookmarks.values().stream()
                .sorted(Comparator.comparing(Bookmark::getCreatedAt))
                .collect(Collectors.toList());
    }

    /**
     * Finds bookmarks whose label contains the given query (case-insensitive).
     */
    public List<Bookmark> findByLabel(String query) {
        if (query == null || query.isBlank()) {
            return getAllBookmarks();
        }
        String lower = query.toLowerCase(Locale.ROOT);
        return bookmarks.values().stream()
                .filter(b -> b.getLabel().toLowerCase(Locale.ROOT).contains(lower))
                .sorted(Comparator.comparing(Bookmark::getCreatedAt))
                .collect(Collectors.toList());
    }

    /**
     * Removes all bookmarks.
     */
    public void clear() {
        bookmarks.clear();
    }

    public int size() {
        return bookmarks.size();
    }
}
