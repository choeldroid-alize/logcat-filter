package com.logcatfilter.search;

import com.logcatfilter.parser.LogcatEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Searches through a list of LogcatEntry objects using a query string.
 * Supports plain-text and regex search modes, with optional case sensitivity.
 */
public class SearchEngine {

    private final SearchOptions options;

    public SearchEngine(SearchOptions options) {
        if (options == null) {
            throw new IllegalArgumentException("SearchOptions must not be null");
        }
        this.options = options;
    }

    /**
     * Search entries for lines matching the given query.
     *
     * @param entries list of log entries to search
     * @param query   the search query
     * @return list of matching SearchResult objects in encounter order
     */
    public List<SearchResult> search(List<LogcatEntry> entries, String query) {
        if (entries == null || entries.isEmpty() || query == null || query.isEmpty()) {
            return Collections.emptyList();
        }

        List<SearchResult> results = new ArrayList<>();
        Pattern pattern = buildPattern(query);

        for (int i = 0; i < entries.size(); i++) {
            LogcatEntry entry = entries.get(i);
            String target = buildTarget(entry);
            boolean matched = options.isRegex()
                    ? pattern.matcher(target).find()
                    : target.contains(options.isCaseSensitive() ? query : query.toLowerCase());
            if (matched) {
                results.add(new SearchResult(i, entry, query));
            }
        }
        return results;
    }

    private Pattern buildPattern(String query) {
        if (!options.isRegex()) {
            return null;
        }
        int flags = options.isCaseSensitive() ? 0 : Pattern.CASE_INSENSITIVE;
        try {
            return Pattern.compile(query, flags);
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Invalid regex pattern: " + query, e);
        }
    }

    private String buildTarget(LogcatEntry entry) {
        String raw = entry.getRawLine();
        return options.isCaseSensitive() ? raw : raw.toLowerCase();
    }

    public SearchOptions getOptions() {
        return options;
    }
}
