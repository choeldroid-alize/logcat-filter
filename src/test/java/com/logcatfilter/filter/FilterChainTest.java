package com.logcatfilter.filter;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterChainTest {

    private LogcatEntry entry;

    @BeforeEach
    void setUp() {
        entry = new LogcatEntry(
                LocalDateTime.now(), 1234, 5678, 'E', "MyApp", "NullPointerException occurred"
        );
    }

    @Test
    void emptyChainAcceptsEverything() {
        FilterChain chain = new FilterChain();
        assertTrue(chain.accepts(entry));
    }

    @Test
    void singleTagRuleMatches() {
        FilterChain chain = new FilterChain();
        chain.addRule(new FilterRule(FilterRule.Field.TAG, "MyApp"));
        assertTrue(chain.accepts(entry));
    }

    @Test
    void singleTagRuleDoesNotMatch() {
        FilterChain chain = new FilterChain();
        chain.addRule(new FilterRule(FilterRule.Field.TAG, "OtherTag"));
        assertFalse(chain.accepts(entry));
    }

    @Test
    void negatedRuleExcludesMatch() {
        FilterChain chain = new FilterChain();
        chain.addRule(new FilterRule(FilterRule.Field.TAG, "MyApp", true));
        assertFalse(chain.accepts(entry));
    }

    @Test
    void multipleRulesAllMustPass() {
        FilterChain chain = new FilterChain();
        chain.addRule(new FilterRule(FilterRule.Field.TAG, "MyApp"));
        chain.addRule(new FilterRule(FilterRule.Field.LEVEL, "E"));
        chain.addRule(new FilterRule(FilterRule.Field.MESSAGE, "NullPointer"));
        assertTrue(chain.accepts(entry));
    }

    @Test
    void parsedRulesIntegrateWithChain() {
        List<FilterRule> rules = FilterRuleParser.parse("tag:MyApp level:E !message:success");
        FilterChain chain = new FilterChain();
        rules.forEach(chain::addRule);
        assertEquals(3, chain.size());
        assertTrue(chain.accepts(entry));
    }

    @Test
    void parseInvalidTokenThrows() {
        assertThrows(IllegalArgumentException.class, () -> FilterRuleParser.parse("invalidtoken"));
    }

    @Test
    void parseUnknownFieldThrows() {
        assertThrows(IllegalArgumentException.class, () -> FilterRuleParser.parse("unknown:value"));
    }

    @Test
    void nullEntryReturnsFalse() {
        FilterChain chain = new FilterChain();
        assertFalse(chain.accepts(null));
    }
}
