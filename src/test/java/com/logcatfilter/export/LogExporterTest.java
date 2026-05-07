package com.logcatfilter.export;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogExporterTest {

    @TempDir
    Path tempDir;

    private LogcatEntry entry(String tag, String level, String message) {
        return new LogcatEntry("2024-01-15 10:00:00.000", "1234", "5678", level, tag, message);
    }

    @Test
    void exportPlainText_writesFormattedLines() throws IOException {
        Path out = tempDir.resolve("output.txt");
        ExportConfig config = ExportConfig.builder(out.toString()).format(ExportConfig.Format.PLAIN_TEXT).build();
        LogExporter exporter = new LogExporter(config);

        List<LogcatEntry> entries = List.of(
            entry("MyTag", "D", "Hello world"),
            entry("OtherTag", "E", "An error occurred")
        );

        int count = exporter.export(entries);
        assertEquals(2, count);

        List<String> lines = Files.readAllLines(out);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).contains("MyTag"));
        assertTrue(lines.get(0).contains("Hello world"));
        assertTrue(lines.get(1).contains("An error occurred"));
    }

    @Test
    void exportCsv_includesHeaderAndEscapesCommas() throws IOException {
        Path out = tempDir.resolve("output.csv");
        ExportConfig config = ExportConfig.builder(out.toString())
            .format(ExportConfig.Format.CSV)
            .includeTimestamp(true)
            .includePid(false)
            .build();
        LogExporter exporter = new LogExporter(config);

        List<LogcatEntry> entries = List.of(entry("Tag,WithComma", "I", "msg"));
        exporter.export(entries);

        List<String> lines = Files.readAllLines(out);
        assertEquals(2, lines.size());
        assertTrue(lines.get(0).startsWith("timestamp"));
        assertTrue(lines.get(1).contains("\"Tag,WithComma\""));
    }

    @Test
    void exportJson_wrapsInArray() throws IOException {
        Path out = tempDir.resolve("output.json");
        ExportConfig config = ExportConfig.builder(out.toString()).format(ExportConfig.Format.JSON).build();
        LogExporter exporter = new LogExporter(config);

        List<LogcatEntry> entries = List.of(entry("TagA", "W", "warn msg"));
        exporter.export(entries);

        String content = Files.readString(out);
        assertTrue(content.trim().startsWith("["));
        assertTrue(content.trim().endsWith("]"));
        assertTrue(content.contains("\"tag\":\"TagA\""));
        assertTrue(content.contains("\"level\":\"W\""));
    }

    @Test
    void exportRespectsMaxLines() throws IOException {
        Path out = tempDir.resolve("limited.txt");
        ExportConfig config = ExportConfig.builder(out.toString()).maxLines(2).build();
        LogExporter exporter = new LogExporter(config);

        List<LogcatEntry> entries = List.of(
            entry("T", "D", "line1"), entry("T", "D", "line2"), entry("T", "D", "line3")
        );
        int count = exporter.export(entries);
        assertEquals(2, count);
        assertEquals(2, Files.readAllLines(out).size());
    }

    @Test
    void exportEmptyList_returnsZero() throws IOException {
        Path out = tempDir.resolve("empty.txt");
        ExportConfig config = ExportConfig.builder(out.toString()).build();
        LogExporter exporter = new LogExporter(config);
        int count = exporter.export(List.of());
        assertEquals(0, count);
    }

    @Test
    void exportConfig_throwsOnBlankPath() {
        assertThrows(IllegalArgumentException.class, () -> ExportConfig.builder("").build());
        assertThrows(IllegalArgumentException.class, () -> ExportConfig.builder(null).build());
    }

    @Test
    void exportConfig_throwsOnNonPositiveMaxLines() {
        assertThrows(IllegalArgumentException.class,
            () -> ExportConfig.builder("/tmp/out.txt").maxLines(0).build());
    }
}
