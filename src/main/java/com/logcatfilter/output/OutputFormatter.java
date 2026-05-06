package com.logcatfilter.output;

import com.logcatfilter.parser.LogcatEntry;
import com.logcatfilter.tag.TagDefinition;

import java.util.List;

/**
 * Formats a LogcatEntry for terminal output, applying ANSI color codes
 * based on log level and any matched tag definitions.
 */
public class OutputFormatter {

    private static final String RESET  = "\u001B[0m";
    private static final String BOLD   = "\u001B[1m";

    // Level colours
    private static final String COLOR_VERBOSE = "\u001B[37m";  // white
    private static final String COLOR_DEBUG   = "\u001B[36m";  // cyan
    private static final String COLOR_INFO    = "\u001B[32m";  // green
    private static final String COLOR_WARNING = "\u001B[33m";  // yellow
    private static final String COLOR_ERROR   = "\u001B[31m";  // red
    private static final String COLOR_FATAL   = "\u001B[35m";  // magenta

    private final boolean useColor;

    public OutputFormatter(boolean useColor) {
        this.useColor = useColor;
    }

    /**
     * Formats a single log entry, optionally annotating it with matched tag labels.
     *
     * @param entry    the parsed logcat entry
     * @param tags     list of tags that matched this entry (may be empty)
     * @return         the formatted string ready for printing
     */
    public String format(LogcatEntry entry, List<TagDefinition> tags) {
        StringBuilder sb = new StringBuilder();

        if (useColor) {
            sb.append(levelColor(entry.getLevel()));
        }

        // Timestamp + PID/TID
        sb.append(entry.getTimestamp())
          .append(" ")
          .append(entry.getPid())
          .append("/")
          .append(entry.getTid())
          .append(" ")
          .append(entry.getLevel())
          .append("/")
          .append(entry.getTag())
          .append(": ")
          .append(entry.getMessage());

        // Append tag labels if any
        if (!tags.isEmpty()) {
            sb.append("  [");
            for (int i = 0; i < tags.size(); i++) {
                if (i > 0) sb.append(", ");
                if (useColor) sb.append(BOLD);
                sb.append(tags.get(i).getLabel());
                if (useColor) sb.append(RESET).append(levelColor(entry.getLevel()));
            }
            sb.append("]");
        }

        if (useColor) {
            sb.append(RESET);
        }

        return sb.toString();
    }

    private String levelColor(String level) {
        if (level == null) return "";
        switch (level.toUpperCase()) {
            case "V": return COLOR_VERBOSE;
            case "D": return COLOR_DEBUG;
            case "I": return COLOR_INFO;
            case "W": return COLOR_WARNING;
            case "E": return COLOR_ERROR;
            case "F": return COLOR_FATAL;
            default:  return "";
        }
    }
}
