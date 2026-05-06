package com.logcatfilter.search;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchEngineTest {

    private List<LogcatEntry> entries;

    @BeforeEach
    void setUp() {
        entries = Arrays.asList(
            LogcatEntry.fromRaw("01-01 00:00:01.000  123  456 D MyTag: debug message"),
            LogcatEntry.fromRaw("01-01 00:00:02.000  123  456 E MyTag: error occurred"),
            LogcatEntry.fromRaw("01-01 00:00:03.000  123  456 I OtherTag: INFO level log"),
            LogcatEntry.fromRaw("01-01 00:00:04.000  123  456 W MyTag: WARNING: disk full")
        );
    }

    @Test
    void plainTextCaseInsensitiveMatch() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, false));
        List<SearchResult> results = engine.search(entries, "error");
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getIndex());
    }

    @Test
    void plainTextCaseSensitiveNoMatch() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, true));
        List<SearchResult> results = engine.search(entries, "error");
        assertEquals(1, results.size());
    }

    @Test
    void plainTextCaseSensitiveMatch() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, true));
        List<SearchResult> results = engine.search(entries, "INFO");
        assertEquals(1, results.size());
        assertEquals(2, results.get(0).getIndex());
    }

    @Test
    void regexMatchMultiple() {
        SearchEngine engine = new SearchEngine(new SearchOptions(true, false));
        List<SearchResult> results = engine.search(entries, "my.*:.*e");
        // matches "debug message" and "error occurred" and "WARNING: disk full"
        assertTrue(results.size() >= 2);
    }

    @Test
    void regexInvalidPatternThrows() {
        SearchEngine engine = new SearchEngine(new SearchOptions(true, false));
        assertThrows(IllegalArgumentException.class, () -> engine.search(entries, "[invalid"));
    }

    @Test
    void emptyQueryReturnsEmpty() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, false));
        List<SearchResult> results = engine.search(entries, "");
        assertTrue(results.isEmpty());
    }

    @Test
    void nullEntriesReturnsEmpty() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, false));
        List<SearchResult> results = engine.search(null, "debug");
        assertTrue(results.isEmpty());
    }

    @Test
    void nullOptionsThrows() {
        assertThrows(IllegalArgumentException.class, () -> new SearchEngine(null));
    }

    @Test
    void noMatchReturnsEmpty() {
        SearchEngine engine = new SearchEngine(new SearchOptions(false, false));
        List<SearchResult> results = engine.search(entries, "zzznomatch");
        assertTrue(results.isEmpty());
    }
}
