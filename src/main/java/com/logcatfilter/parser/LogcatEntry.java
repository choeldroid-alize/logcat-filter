package com.logcatfilter.parser;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a single parsed logcat log entry.
 */
public class LogcatEntry {

    public enum Level {
        VERBOSE, DEBUG, INFO, WARN, ERROR, FATAL, SILENT;

        public static Level fromChar(char c) {
            return switch (Character.toUpperCase(c)) {
                case 'V' -> VERBOSE;
                case 'D' -> DEBUG;
                case 'I' -> INFO;
                case 'W' -> WARN;
                case 'E' -> ERROR;
                case 'F' -> FATAL;
                case 'S' -> SILENT;
                default -> throw new IllegalArgumentException("Unknown log level: " + c);
            };
        }
    }

    private final LocalDateTime timestamp;
    private final int pid;
    private final int tid;
    private final Level level;
    private final String tag;
    private final String message;
    private final String rawLine;

    public LogcatEntry(LocalDateTime timestamp, int pid, int tid, Level level, String tag, String message, String rawLine) {
        this.timestamp = timestamp;
        this.pid = pid;
        this.tid = tid;
        this.level = level;
        this.tag = tag;
        this.message = message;
        this.rawLine = rawLine;
    }

    public LocalDateTime getTimestamp() { return timestamp; }
    public int getPid() { return pid; }
    public int getTid() { return tid; }
    public Level getLevel() { return level; }
    public String getTag() { return tag; }
    public String getMessage() { return message; }
    public String getRawLine() { return rawLine; }

    @Override
    public String toString() {
        return String.format("[%s] %s/%s(%d): %s", level, tag, timestamp, pid, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LogcatEntry)) return false;
        LogcatEntry that = (LogcatEntry) o;
        return pid == that.pid && tid == that.tid &&
                Objects.equals(rawLine, that.rawLine);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pid, tid, rawLine);
    }
}
