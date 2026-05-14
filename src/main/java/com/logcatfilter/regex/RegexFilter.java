package com.logcatfilter.regex;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Applies a list of compiled regex patterns to LogcatEntry fields,
 * returning only entries that match at least one pattern (include mode)
 * or none of the patterns (exclude mode).
 */
public class RegexFilter {

    public enum Mode { INCLUDE, EXCLUDE }

    private final List<Pattern> patterns;
    private final Mode mode;

    public RegexFilter(List<String> rawPatterns, Mode mode) {
        if (rawPatterns == null || rawPatterns.isEmpty()) {
            throw new IllegalArgumentException("Pattern list must not be null or empty");
        }
        this.mode = mode;
        List<Pattern> compiled = new ArrayList<>();
        for (String raw : rawPatterns) {
            try {
                compiled.add(Pattern.compile(raw));
            } catch (PatternSyntaxException e) {
                throw new IllegalArgumentException("Invalid regex pattern: " + raw, e);
            }
        }
        this.patterns = Collections.unmodifiableList(compiled);
    }

    /**
     * Tests whether the entry passes this filter.
     */
    public boolean accepts(LogcatEntry entry) {
        if (entry == null) return false;
        boolean anyMatch = matchesAny(entry);
        return mode == Mode.INCLUDE ? anyMatch : !anyMatch;
    }

    private boolean matchesAny(LogcatEntry entry) {
        String composite = buildComposite(entry);
        for (Pattern p : patterns) {
            if (p.matcher(composite).find()) {
                return true;
            }
        }
        return false;
    }

    private String buildComposite(LogcatEntry entry) {
        StringBuilder sb = new StringBuilder();
        if (entry.getTag() != null)     sb.append(entry.getTag()).append(' ');
        if (entry.getMessage() != null) sb.append(entry.getMessage()).append(' ');
        if (entry.getPid() != null)     sb.append(entry.getPid());
        return sb.toString();
    }

    public List<Pattern> getPatterns() { return patterns; }
    public Mode getMode()              { return mode; }
}
