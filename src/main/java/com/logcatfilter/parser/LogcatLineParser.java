package com.logcatfilter.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses raw logcat lines (threadtime format) into {@link LogcatEntry} objects.
 * Expected format: MM-DD HH:mm:ss.mmm  PID   TID LEVEL TAG: MESSAGE
 */
public class LogcatLineParser {

    // Example: 01-15 12:34:56.789  1234  5678 D MyTag: This is a message
    private static final Pattern LOGCAT_PATTERN = Pattern.compile(
            "^(\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3})" +
            "\\s+(\\d+)\\s+(\\d+)" +
            "\\s+([VDIWEFS])" +
            "\\s+([^:]+):\\s*(.*)$"
    );

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("MM-dd HH:mm:ss.SSS");

    private final int currentYear;

    public LogcatLineParser() {
        this.currentYear = LocalDateTime.now().getYear();
    }

    public LogcatLineParser(int year) {
        this.currentYear = year;
    }

    /**
     * Attempts to parse a raw logcat line.
     *
     * @param rawLine the raw line from logcat output
     * @return an Optional containing the parsed entry, or empty if the line doesn't match
     */
    public Optional<LogcatEntry> parse(String rawLine) {
        if (rawLine == null || rawLine.isBlank()) {
            return Optional.empty();
        }

        Matcher matcher = LOGCAT_PATTERN.matcher(rawLine.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        try {
            String timestampStr = matcher.group(1);
            LocalDateTime timestamp = LocalDateTime.parse(timestampStr, FORMATTER)
                    .withYear(currentYear);

            int pid = Integer.parseInt(matcher.group(2));
            int tid = Integer.parseInt(matcher.group(3));
            LogcatEntry.Level level = LogcatEntry.Level.fromChar(matcher.group(4).charAt(0));
            String tag = matcher.group(5).trim();
            String message = matcher.group(6);

            return Optional.of(new LogcatEntry(timestamp, pid, tid, level, tag, message, rawLine));
        } catch (DateTimeParseException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
