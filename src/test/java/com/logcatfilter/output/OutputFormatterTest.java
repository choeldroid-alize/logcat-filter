package com.logcatfilter.output;

import com.logcatfilter.parser.LogcatEntry;
import com.logcatfilter.tag.TagDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OutputFormatterTest {

    private LogcatEntry entry;

    @BeforeEach
    void setUp() {
        entry = new LogcatEntry(
                "2024-01-15 10:23:45.123",
                "1234",
                "5678",
                "I",
                "MyApp",
                "Application started successfully"
        );
    }

    @Test
    void formatWithoutColorContainsAllFields() {
        OutputFormatter formatter = new OutputFormatter(false);
        String result = formatter.format(entry, Collections.emptyList());

        assertTrue(result.contains("2024-01-15 10:23:45.123"));
        assertTrue(result.contains("1234/5678"));
        assertTrue(result.contains("I/MyApp"));
        assertTrue(result.contains("Application started successfully"));
    }

    @Test
    void formatWithoutColorHasNoAnsiCodes() {
        OutputFormatter formatter = new OutputFormatter(false);
        String result = formatter.format(entry, Collections.emptyList());

        assertFalse(result.contains("\u001B["));
    }

    @Test
    void formatWithColorContainsAnsiCodes() {
        OutputFormatter formatter = new OutputFormatter(true);
        String result = formatter.format(entry, Collections.emptyList());

        assertTrue(result.contains("\u001B["));
        // INFO level should use green
        assertTrue(result.contains("\u001B[32m"));
        // Should end with reset
        assertTrue(result.endsWith("\u001B[0m"));
    }

    @Test
    void formatWithTagsAppendsBracketedLabels() {
        OutputFormatter formatter = new OutputFormatter(false);
        TagDefinition t1 = new TagDefinition("auth",    "AUTH",    null, null);
        TagDefinition t2 = new TagDefinition("network", "NETWORK", null, null);

        String result = formatter.format(entry, Arrays.asList(t1, t2));

        assertTrue(result.contains("[AUTH, NETWORK]"));
    }

    @Test
    void formatErrorLevelUsesRedColor() {
        LogcatEntry errorEntry = new LogcatEntry(
                "2024-01-15 10:23:45.123", "1234", "5678",
                "E", "CrashHandler", "NullPointerException");
        OutputFormatter formatter = new OutputFormatter(true);
        String result = formatter.format(errorEntry, Collections.emptyList());

        assertTrue(result.contains("\u001B[31m"));
    }

    @Test
    void formatNoTagsProducesNoSquareBrackets() {
        OutputFormatter formatter = new OutputFormatter(false);
        String result = formatter.format(entry, Collections.emptyList());

        assertFalse(result.contains("["));
    }

    @Test
    void formatWarnLevelUsesYellowColor() {
        LogcatEntry warnEntry = new LogcatEntry(
                "2024-01-15 10:23:45.123", "1234", "5678",
                "W", "NetworkManager", "Connection timed out");
        OutputFormatter formatter = new OutputFormatter(true);
        String result = formatter.format(warnEntry, Collections.emptyList());

        // WARN level should use yellow
        assertTrue(result.contains("\u001B[33m"));
    }

    @Test
    void formatDebugLevelUsesDefaultColor() {
        LogcatEntry debugEntry = new LogcatEntry(
                "2024-01-15 10:23:45.123", "1234", "5678",
                "D", "DebugTag", "Debug message");
        OutputFormatter formatter = new OutputFormatter(true);
        String result = formatter.format(debugEntry, Collections.emptyList());

        // DEBUG level should not use red or green or yellow
        assertFalse(result.contains("\u001B[31m"));
        assertFalse(result.contains("\u001B[32m"));
        assertFalse(result.contains("\u001B[33m"));
        // Should still end with reset
        assertTrue(result.endsWith("\u001B[0m"));
    }
}
