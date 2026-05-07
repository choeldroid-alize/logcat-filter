package com.logcatfilter.export;

import com.logcatfilter.parser.LogcatEntry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exports filtered log entries to a file in the configured format.
 */
public class LogExporter {

    private final ExportConfig config;

    public LogExporter(ExportConfig config) {
        if (config == null) throw new IllegalArgumentException("ExportConfig must not be null");
        this.config = config;
    }

    public int export(List<LogcatEntry> entries) throws IOException {
        if (entries == null || entries.isEmpty()) return 0;

        List<LogcatEntry> toExport = entries.size() > config.getMaxLines()
                ? entries.subList(0, config.getMaxLines())
                : entries;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(config.getOutputPath()))) {
            if (config.getFormat() == ExportConfig.Format.CSV) {
                writer.write(buildCsvHeader());
                writer.newLine();
            } else if (config.getFormat() == ExportConfig.Format.JSON) {
                writer.write("[");
                writer.newLine();
            }

            for (int i = 0; i < toExport.size(); i++) {
                LogcatEntry entry = toExport.get(i);
                String line = formatEntry(entry, config.getFormat());
                writer.write(line);
                if (config.getFormat() == ExportConfig.Format.JSON && i < toExport.size() - 1) {
                    writer.write(",");
                }
                writer.newLine();
            }

            if (config.getFormat() == ExportConfig.Format.JSON) {
                writer.write("]");
                writer.newLine();
            }
        }
        return toExport.size();
    }

    private String buildCsvHeader() {
        StringBuilder sb = new StringBuilder();
        if (config.isIncludeTimestamp()) sb.append("timestamp,");
        if (config.isIncludePid()) sb.append("pid,");
        if (config.isIncludeTag()) sb.append("tag,");
        sb.append("level,message");
        return sb.toString();
    }

    private String formatEntry(LogcatEntry entry, ExportConfig.Format format) {
        return switch (format) {
            case CSV -> formatCsv(entry);
            case JSON -> formatJson(entry);
            default -> formatPlain(entry);
        };
    }

    private String formatPlain(LogcatEntry entry) {
        StringBuilder sb = new StringBuilder();
        if (config.isIncludeTimestamp()) sb.append(entry.getTimestamp()).append(" ");
        if (config.isIncludePid()) sb.append("[").append(entry.getPid()).append("] ");
        if (config.isIncludeTag()) sb.append(entry.getTag()).append(": ");
        sb.append(entry.getLevel()).append(" ").append(entry.getMessage());
        return sb.toString();
    }

    private String formatCsv(LogcatEntry entry) {
        StringBuilder sb = new StringBuilder();
        if (config.isIncludeTimestamp()) sb.append(csvEscape(entry.getTimestamp())).append(",");
        if (config.isIncludePid()) sb.append(entry.getPid()).append(",");
        if (config.isIncludeTag()) sb.append(csvEscape(entry.getTag())).append(",");
        sb.append(entry.getLevel()).append(",").append(csvEscape(entry.getMessage()));
        return sb.toString();
    }

    private String formatJson(LogcatEntry entry) {
        return String.format(
            "  {\"timestamp\":\"%s\",\"pid\":%s,\"tag\":\"%s\",\"level\":\"%s\",\"message\":\"%s\"}",
            entry.getTimestamp(), entry.getPid(),
            jsonEscape(entry.getTag()), entry.getLevel(), jsonEscape(entry.getMessage()));
    }

    private String csvEscape(String value) {
        if (value == null) return "";
        return value.contains(",") || value.contains("\"") ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }

    private String jsonEscape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
