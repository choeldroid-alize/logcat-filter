package com.logcatfilter.bookmark;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BookmarkManagerTest {

    private BookmarkManager manager;
    private LogcatEntry sampleEntry;

    @BeforeEach
    void setUp() {
        manager = new BookmarkManager();
        sampleEntry = new LogcatEntry("2024-01-15 10:00:00.000", "D", "MyTag", "1234", "Sample log message");
    }

    @Test
    void addBookmark_shouldReturnBookmarkWithGeneratedId() {
        Bookmark bm = manager.addBookmark(sampleEntry, "my label");
        assertNotNull(bm);
        assertNotNull(bm.getId());
        assertTrue(bm.getId().startsWith("bm-"));
        assertEquals("my label", bm.getLabel());
        assertEquals(sampleEntry, bm.getEntry());
    }

    @Test
    void addBookmark_withNullLabel_shouldStoreEmptyLabel() {
        Bookmark bm = manager.addBookmark(sampleEntry, null);
        assertEquals("", bm.getLabel());
        assertFalse(bm.hasLabel());
    }

    @Test
    void addBookmark_incrementsSize() {
        manager.addBookmark(sampleEntry, "first");
        manager.addBookmark(sampleEntry, "second");
        assertEquals(2, manager.size());
    }

    @Test
    void getBookmark_existingId_shouldReturnBookmark() {
        Bookmark bm = manager.addBookmark(sampleEntry, "label");
        Optional<Bookmark> result = manager.getBookmark(bm.getId());
        assertTrue(result.isPresent());
        assertEquals(bm.getId(), result.get().getId());
    }

    @Test
    void getBookmark_unknownId_shouldReturnEmpty() {
        Optional<Bookmark> result = manager.getBookmark("bm-999");
        assertFalse(result.isPresent());
    }

    @Test
    void removeBookmark_existingId_shouldReturnTrueAndDecreaseSize() {
        Bookmark bm = manager.addBookmark(sampleEntry, "to remove");
        assertTrue(manager.removeBookmark(bm.getId()));
        assertEquals(0, manager.size());
    }

    @Test
    void removeBookmark_unknownId_shouldReturnFalse() {
        assertFalse(manager.removeBookmark("bm-does-not-exist"));
    }

    @Test
    void findByLabel_shouldReturnMatchingBookmarks() {
        manager.addBookmark(sampleEntry, "network error");
        manager.addBookmark(sampleEntry, "crash report");
        manager.addBookmark(sampleEntry, "Network timeout");

        List<Bookmark> results = manager.findByLabel("network");
        assertEquals(2, results.size());
    }

    @Test
    void findByLabel_emptyQuery_shouldReturnAll() {
        manager.addBookmark(sampleEntry, "a");
        manager.addBookmark(sampleEntry, "b");
        List<Bookmark> results = manager.findByLabel("");
        assertEquals(2, results.size());
    }

    @Test
    void clear_shouldRemoveAllBookmarks() {
        manager.addBookmark(sampleEntry, "x");
        manager.addBookmark(sampleEntry, "y");
        manager.clear();
        assertEquals(0, manager.size());
    }

    @Test
    void bookmark_constructorWithBlankId_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bookmark("", sampleEntry, "label"));
    }

    @Test
    void bookmark_constructorWithNullEntry_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new Bookmark("bm-1", null, "label"));
    }
}
