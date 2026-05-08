package com.logcatfilter.column;

import com.logcatfilter.parser.LogcatEntry;

/**
 * Formats a LogcatEntry into a display string according to a ColumnConfig.
 */
public class ColumnFormatter {

    private static final String SEPARATOR = " | ";

    private final ColumnConfig config;

    public ColumnFormatter(ColumnConfig config) {
        if (config == null) throw new IllegalArgumentException("ColumnConfig must not be null");
        this.config = config;
    }

    public String format(LogcatEntry entry) {
        if (entry == null) throw new IllegalArgumentException("LogcatEntry must not be null");
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (ColumnConfig.Column col : config.getVisibleColumns()) {
            if (!first) sb.append(SEPARATOR);
            first = false;
            switch (col) {
                case TIMESTAMP:
                    sb.append(pad(entry.getTimestamp(), config.getTimestampWidth()));
                    break;
                case PID:
                    sb.append(pad(String.valueOf(entry.getPid()), config.getPidWidth()));
                    break;
                case TID:
                    sb.append(pad(String.valueOf(entry.getTid()), config.getPidWidth()));
                    break;
                case LEVEL:
                    sb.append(entry.getLevel() != null ? entry.getLevel().toString().charAt(0) : '?');
                    break;
                case TAG:
                    sb.append(truncate(entry.getTag(), config.getTagWidth()));
                    break;
                case MESSAGE:
                    sb.append(entry.getMessage() != null ? entry.getMessage() : "");
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

    private String pad(String value, int width) {
        if (value == null) value = "";
        if (value.length() >= width) return value.substring(0, width);
        StringBuilder sb = new StringBuilder(value);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }

    private String truncate(String value, int width) {
        if (value == null) return pad("", width);
        if (value.length() > width) return value.substring(0, width - 1) + "…";
        return pad(value, width);
    }

    public ColumnConfig getConfig() {
        return config;
    }
}
