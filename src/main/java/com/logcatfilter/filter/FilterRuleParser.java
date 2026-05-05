package com.logcatfilter.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses a user-supplied filter expression string into a list of FilterRules.
 *
 * Syntax:  [!]field:pattern  (space-separated for multiple rules)
 * Examples:
 *   tag:MyApp
 *   !tag:System level:E
 *   message:NullPointer pid:1234
 */
public class FilterRuleParser {

    private static final String FIELD_SEPARATOR = ":";

    private FilterRuleParser() {}

    /**
     * Parses the given expression and returns a list of FilterRules.
     * Throws IllegalArgumentException on malformed input.
     */
    public static List<FilterRule> parse(String expression) {
        List<FilterRule> result = new ArrayList<>();
        if (expression == null || expression.isBlank()) {
            return result;
        }

        String[] tokens = expression.trim().split("\\s+");
        for (String token : tokens) {
            result.add(parseToken(token));
        }
        return result;
    }

    private static FilterRule parseToken(String token) {
        boolean negate = token.startsWith("!");
        String effective = negate ? token.substring(1) : token;

        int colonIndex = effective.indexOf(FIELD_SEPARATOR);
        if (colonIndex <= 0 || colonIndex == effective.length() - 1) {
            throw new IllegalArgumentException(
                    "Invalid filter token '" + token + "'. Expected format: [!]field:pattern");
        }

        String fieldStr = effective.substring(0, colonIndex).toUpperCase();
        String pattern = effective.substring(colonIndex + 1);

        FilterRule.Field field;
        try {
            field = FilterRule.Field.valueOf(fieldStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unknown filter field '" + fieldStr + "'. Valid fields: tag, message, level, pid");
        }

        return new FilterRule(field, pattern, negate);
    }
}
