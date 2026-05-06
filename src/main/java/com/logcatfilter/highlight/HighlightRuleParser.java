package com.logcatfilter.highlight;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses highlight rule definitions from strings.
 * Expected format: "PATTERN:COLOR" or "PATTERN:COLOR:LABEL"
 * Example: "Exception:RED:errors" or "onCreate:CYAN"
 */
public class HighlightRuleParser {

    private static final String DELIMITER = ":";
    private static final int MIN_PARTS = 2;

    /**
     * Parses a single rule definition string into a {@link HighlightRule}.
     *
     * @param definition the rule definition string
     * @return a parsed HighlightRule
     * @throws IllegalArgumentException if the format is invalid
     */
    public HighlightRule parse(String definition) {
        if (definition == null || definition.isBlank()) {
            throw new IllegalArgumentException("Highlight rule definition must not be blank");
        }
        String[] parts = definition.split(DELIMITER, 3);
        if (parts.length < MIN_PARTS) {
            throw new IllegalArgumentException(
                    "Invalid highlight rule format. Expected 'PATTERN:COLOR[:LABEL]', got: '" + definition + "'");
        }
        String pattern = parts[0].trim();
        String colorName = parts[1].trim().toUpperCase();
        String label = parts.length == 3 ? parts[2].trim() : "";

        if (pattern.isEmpty()) {
            throw new IllegalArgumentException("Pattern must not be empty in rule: '" + definition + "'");
        }

        HighlightRule.Color color;
        try {
            color = HighlightRule.Color.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown color '" + colorName + "'. Valid values: RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, WHITE, BOLD");
        }

        return new HighlightRule(pattern, color, label);
    }

    /**
     * Parses multiple rule definitions, one per line, skipping blank lines and comments (#).
     */
    public List<HighlightRule> parseAll(List<String> definitions) {
        List<HighlightRule> rules = new ArrayList<>();
        for (String def : definitions) {
            String trimmed = def.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            rules.add(parse(trimmed));
        }
        return rules;
    }
}
