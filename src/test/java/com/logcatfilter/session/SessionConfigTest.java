package com.logcatfilter.session;

import com.logcatfilter.filter.FilterRule;
import com.logcatfilter.tag.TagDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionConfigTest {

    private SessionConfig config;

    @BeforeEach
    void setUp() {
        config = new SessionConfig();
    }

    @Test
    void defaultStateIsCorrect() {
        assertFalse(config.isPaused());
        assertEquals(SessionConfig.DEFAULT_MAX_BUFFER_LINES, config.getMaxBufferLines());
        assertTrue(config.getFilterRules().isEmpty());
        assertTrue(config.getTagDefinitions().isEmpty());
    }

    @Test
    void addAndRetrieveFilterRule() {
        FilterRule rule = new FilterRule("level", "ERROR", FilterRule.MatchType.EQUALS, false);
        config.addFilterRule(rule);
        assertEquals(1, config.getFilterRules().size());
        assertSame(rule, config.getFilterRules().get(0));
    }

    @Test
    void removeFilterRule() {
        FilterRule rule = new FilterRule("tag", "MyApp", FilterRule.MatchType.CONTAINS, false);
        config.addFilterRule(rule);
        config.removeFilterRule(rule);
        assertTrue(config.getFilterRules().isEmpty());
    }

    @Test
    void filterRulesListIsUnmodifiable() {
        assertThrows(UnsupportedOperationException.class,
                () -> config.getFilterRules().add(new FilterRule("tag", "X", FilterRule.MatchType.CONTAINS, false)));
    }

    @Test
    void addNullFilterRuleThrows() {
        assertThrows(IllegalArgumentException.class, () -> config.addFilterRule(null));
    }

    @Test
    void addAndRetrieveTagDefinition() {
        TagDefinition tag = new TagDefinition("ERROR", "\u001B[31m", true);
        config.addTagDefinition(tag);
        assertEquals(1, config.getTagDefinitions().size());
        assertSame(tag, config.getTagDefinitions().get(0));
    }

    @Test
    void addNullTagDefinitionThrows() {
        assertThrows(IllegalArgumentException.class, () -> config.addTagDefinition(null));
    }

    @Test
    void pausedToggle() {
        config.setPaused(true);
        assertTrue(config.isPaused());
        config.setPaused(false);
        assertFalse(config.isPaused());
    }

    @Test
    void setMaxBufferLines() {
        config.setMaxBufferLines(1000);
        assertEquals(1000, config.getMaxBufferLines());
    }

    @Test
    void setMaxBufferLinesZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> config.setMaxBufferLines(0));
    }

    @Test
    void clearResetsAllState() {
        config.addFilterRule(new FilterRule("tag", "X", FilterRule.MatchType.CONTAINS, false));
        config.addTagDefinition(new TagDefinition("WARN", "\u001B[33m", false));
        config.setPaused(true);
        config.setMaxBufferLines(100);

        config.clear();

        assertTrue(config.getFilterRules().isEmpty());
        assertTrue(config.getTagDefinitions().isEmpty());
        assertFalse(config.isPaused());
        assertEquals(SessionConfig.DEFAULT_MAX_BUFFER_LINES, config.getMaxBufferLines());
    }
}
