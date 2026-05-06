package com.logcatfilter.highlight;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighlightEngineTest {

    private HighlightEngine engine;
    private HighlightRuleParser parser;

    @BeforeEach
    void setUp() {
        engine = new HighlightEngine();
        parser = new HighlightRuleParser();
    }

    @Test
    void highlightRaw_noRules_returnsOriginalText() {
        String result = engine.highlightRaw("some log message");
        assertEquals("some log message", result);
    }

    @Test
    void highlightRaw_matchingRule_appliesAnsiColor() {
        engine.addRule(parser.parse("Exception:RED"));
        String result = engine.highlightRaw("NullPointerException occurred");
        assertTrue(result.contains("\033[31m"), "Should contain RED ANSI code");
        assertTrue(result.contains("Exception"), "Should still contain matched text");
        assertTrue(result.contains("\033[0m"), "Should contain ANSI reset");
    }

    @Test
    void highlightRaw_noMatch_returnsOriginalText() {
        engine.addRule(parser.parse("Exception:RED"));
        String result = engine.highlightRaw("normal log line");
        assertEquals("normal log line", result);
    }

    @Test
    void highlight_nullEntry_returnsEmptyString() {
        String result = engine.highlight(null);
        assertEquals("", result);
    }

    @Test
    void highlight_appliesRulesToEntryMessage() {
        engine.addRule(parser.parse("onCreate:CYAN"));
        LogcatEntry entry = new LogcatEntry("01-01", "12:00:00.000", 100, 100,
                "D", "ActivityManager", "Activity onCreate called");
        String result = engine.highlight(entry);
        assertTrue(result.contains("\033[36m"), "Should contain CYAN ANSI code");
    }

    @Test
    void multipleRules_appliedInOrder() {
        engine.addRule(parser.parse("Error:RED"));
        engine.addRule(parser.parse("Warning:YELLOW"));
        assertEquals(2, engine.getRules().size());
        String result = engine.highlightRaw("Error and Warning in one line");
        assertTrue(result.contains("\033[31m"));
        assertTrue(result.contains("\033[33m"));
    }

    @Test
    void clearRules_removesAllRules() {
        engine.addRule(parser.parse("test:BLUE"));
        engine.clearRules();
        assertFalse(engine.hasRules());
    }

    @Test
    void parseAll_skipsBlankLinesAndComments() {
        List<String> defs = List.of(
                "# this is a comment",
                "",
                "Exception:RED:errors",
                "onCreate:GREEN"
        );
        List<HighlightRule> rules = parser.parseAll(defs);
        assertEquals(2, rules.size());
        assertEquals("errors", rules.get(0).getLabel());
        assertEquals(HighlightRule.Color.GREEN, rules.get(1).getColor());
    }

    @Test
    void parse_invalidFormat_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> parser.parse("PATTERN_ONLY"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse("pattern:INVALIDCOLOR"));
        assertThrows(IllegalArgumentException.class, () -> parser.parse(""));
    }
}
