package com.logcatfilter.column;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnFormatterTest {

    private LogcatEntry sampleEntry;

    @BeforeEach
    void setUp() {
        sampleEntry = new LogcatEntry(
                "2024-01-15 10:23:45.123",
                1234,
                5678,
                LogcatEntry.Level.DEBUG,
                "MyApplication",
                "Hello from the app"
        );
    }

    @Test
    void formatsDefaultColumnsWithSeparators() {
        ColumnConfig config = ColumnConfig.defaultConfig().build();
        ColumnFormatter formatter = new ColumnFormatter(config);
        String result = formatter.format(sampleEntry);
        assertTrue(result.contains(" | "));
        assertTrue(result.contains("MyApplication"));
        assertTrue(result.contains("Hello from the app"));
    }

    @Test
    void levelColumnShowsSingleChar() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.LEVEL)
                .build();
        ColumnFormatter formatter = new ColumnFormatter(config);
        String result = formatter.format(sampleEntry);
        assertEquals("D", result.trim());
    }

    @Test
    void tagIsTruncatedWhenTooLong() {
        LogcatEntry entry = new LogcatEntry(
                "2024-01-15 10:23:45.123", 1, 1,
                LogcatEntry.Level.INFO,
                "VeryLongTagNameThatExceedsLimit",
                "msg"
        );
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.TAG)
                .tagWidth(10)
                .build();
        ColumnFormatter formatter = new ColumnFormatter(config);
        String result = formatter.format(entry);
        assertEquals(10, result.length());
        assertTrue(result.endsWith("…"));
    }

    @Test
    void nullEntryThrows() {
        ColumnFormatter formatter = new ColumnFormatter(ColumnConfig.defaultConfig().build());
        assertThrows(IllegalArgumentException.class, () -> formatter.format(null));
    }

    @Test
    void nullConfigThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ColumnFormatter(null));
    }

    @Test
    void pidAndTidColumnsIncluded() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.PID)
                .addColumn(ColumnConfig.Column.TID)
                .pidWidth(6)
                .build();
        ColumnFormatter formatter = new ColumnFormatter(config);
        String result = formatter.format(sampleEntry);
        assertTrue(result.contains("1234"));
        assertTrue(result.contains("5678"));
    }

    @Test
    void messageColumnAppendsFullText() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.MESSAGE)
                .build();
        ColumnFormatter formatter = new ColumnFormatter(config);
        String result = formatter.format(sampleEntry);
        assertEquals("Hello from the app", result);
    }
}
