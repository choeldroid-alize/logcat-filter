package com.logcatfilter.parser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LogcatLineParserTest {

    private LogcatLineParser parser;

    @BeforeEach
    void setUp() {
        parser = new LogcatLineParser(2024);
    }

    @Test
    void shouldParseValidDebugLine() {
        String line = "01-15 12:34:56.789  1234  5678 D MyTag: Hello world";
        Optional<LogcatEntry> result = parser.parse(line);

        assertTrue(result.isPresent());
        LogcatEntry entry = result.get();
        assertEquals(LogcatEntry.Level.DEBUG, entry.getLevel());
        assertEquals("MyTag", entry.getTag());
        assertEquals("Hello world", entry.getMessage());
        assertEquals(1234, entry.getPid());
        assertEquals(5678, entry.getTid());
        assertEquals(2024, entry.getTimestamp().getYear());
    }

    @Test
    void shouldParseErrorLevel() {
        String line = "03-22 08:00:00.000  9999  0001 E CrashTag: NullPointerException";
        Optional<LogcatEntry> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals(LogcatEntry.Level.ERROR, result.get().getLevel());
    }

    @Test
    void shouldParseMessageWithColons() {
        String line = "01-01 00:00:00.000     1     1 I Network: http://example.com:8080/path";
        Optional<LogcatEntry> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals("http://example.com:8080/path", result.get().getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "not a logcat line",
            "01-15 12:34:56.789 INVALID D Tag: msg",
            "--------- beginning of system"
    })
    void shouldReturnEmptyForInvalidLines(String line) {
        Optional<LogcatEntry> result = parser.parse(line);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldReturnEmptyForNullInput() {
        Optional<LogcatEntry> result = parser.parse(null);
        assertFalse(result.isPresent());
    }

    @Test
    void shouldPreserveRawLine() {
        String line = "01-15 12:34:56.789  1234  5678 W SomeTag: raw content";
        Optional<LogcatEntry> result = parser.parse(line);

        assertTrue(result.isPresent());
        assertEquals(line, result.get().getRawLine());
    }
}
