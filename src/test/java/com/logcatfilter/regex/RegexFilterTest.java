package com.logcatfilter.regex;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegexFilterTest {

    private LogcatEntry entry(String tag, String message) {
        LogcatEntry e = new LogcatEntry();
        e.setTag(tag);
        e.setMessage(message);
        return e;
    }

    // --- INCLUDE mode ---

    @Test
    void includeMode_matchingEntry_isAccepted() {
        RegexFilter filter = new RegexFilter(List.of("NetworkError"), RegexFilter.Mode.INCLUDE);
        assertTrue(filter.accepts(entry("Network", "NetworkError occurred")));
    }

    @Test
    void includeMode_nonMatchingEntry_isRejected() {
        RegexFilter filter = new RegexFilter(List.of("CrashReport"), RegexFilter.Mode.INCLUDE);
        assertFalse(filter.accepts(entry("UI", "Button clicked")));
    }

    @Test
    void includeMode_matchesTag() {
        RegexFilter filter = new RegexFilter(List.of("^MyApp$"), RegexFilter.Mode.INCLUDE);
        assertTrue(filter.accepts(entry("MyApp", "started")));
    }

    // --- EXCLUDE mode ---

    @Test
    void excludeMode_matchingEntry_isRejected() {
        RegexFilter filter = new RegexFilter(List.of("verbose"), RegexFilter.Mode.EXCLUDE);
        assertFalse(filter.accepts(entry("Tag", "verbose debug info")));
    }

    @Test
    void excludeMode_nonMatchingEntry_isAccepted() {
        RegexFilter filter = new RegexFilter(List.of("verbose"), RegexFilter.Mode.EXCLUDE);
        assertTrue(filter.accepts(entry("Tag", "important error")));
    }

    // --- Multiple patterns ---

    @Test
    void multiplePatterns_anyMatchSuffices() {
        RegexFilter filter = new RegexFilter(List.of("alpha", "beta"), RegexFilter.Mode.INCLUDE);
        assertTrue(filter.accepts(entry("T", "beta log line")));
        assertTrue(filter.accepts(entry("T", "alpha log line")));
        assertFalse(filter.accepts(entry("T", "gamma log line")));
    }

    // --- Config builder ---

    @Test
    void configBuilder_caseInsensitive_matchesMixedCase() {
        RegexFilterConfig config = RegexFilterConfig.builder()
                .addPattern("exception")
                .mode(RegexFilter.Mode.INCLUDE)
                .caseSensitive(false)
                .build();
        RegexFilter filter = config.buildFilter();
        assertTrue(filter.accepts(entry("App", "EXCEPTION thrown")));
    }

    @Test
    void configBuilder_emptyPatterns_throwsException() {
        assertThrows(IllegalStateException.class, () ->
                RegexFilterConfig.builder().build());
    }

    @Test
    void invalidRegex_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () ->
                new RegexFilter(List.of("[invalid"), RegexFilter.Mode.INCLUDE));
    }

    @Test
    void nullEntry_returnsFalse() {
        RegexFilter filter = new RegexFilter(List.of("any"), RegexFilter.Mode.INCLUDE);
        assertFalse(filter.accepts(null));
    }
}
