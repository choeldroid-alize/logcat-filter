package com.logcatfilter.notify;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationManagerTest {

    private NotificationManager manager;

    private LogcatEntry entry(String tag, String level, String message) {
        return new LogcatEntry("01-01 00:00:00.000", "1234", "5678", level, tag, message);
    }

    @BeforeEach
    void setUp() {
        manager = new NotificationManager();
    }

    @Test
    void addAndRetrieveRule() {
        NotificationRule rule = new NotificationRule("r1", "ERROR",
                NotificationRule.MatchField.LEVEL, "Error Alert", false);
        manager.addRule(rule);
        assertEquals(1, manager.ruleCount());
        assertSame(rule, manager.getRule("r1"));
    }

    @Test
    void removeRuleReturnsTrueWhenPresent() {
        manager.addRule(new NotificationRule("r2", "crash",
                NotificationRule.MatchField.MESSAGE, "Crash", true));
        assertTrue(manager.removeRule("r2"));
        assertEquals(0, manager.ruleCount());
    }

    @Test
    void removeRuleReturnsFalseWhenAbsent() {
        assertFalse(manager.removeRule("nonexistent"));
    }

    @Test
    void evaluateFiresEventOnMatch() {
        manager.addRule(new NotificationRule("r3", "NullPointer",
                NotificationRule.MatchField.MESSAGE, "NPE", false));
        List<NotificationEvent> events = manager.evaluate(
                entry("MyTag", "E", "NullPointerException occurred"));
        assertEquals(1, events.size());
        assertEquals("NPE", events.get(0).getLabel());
    }

    @Test
    void evaluateDoesNotFireOnMismatch() {
        manager.addRule(new NotificationRule("r4", "FATAL",
                NotificationRule.MatchField.LEVEL, "Fatal", false));
        List<NotificationEvent> events = manager.evaluate(
                entry("SomeTag", "D", "debug message"));
        assertTrue(events.isEmpty());
    }

    @Test
    void listenerReceivesEvent() {
        List<NotificationEvent> received = new ArrayList<>();
        manager.addListener(received::add);
        manager.addRule(new NotificationRule("r5", "MyTag",
                NotificationRule.MatchField.TAG, "Tag Match", false));
        manager.evaluate(entry("MyTag", "I", "hello"));
        assertEquals(1, received.size());
        assertEquals("r5", received.get(0).getRule().getId());
    }

    @Test
    void evaluateNullEntryReturnsEmpty() {
        manager.addRule(new NotificationRule("r6", ".*",
                NotificationRule.MatchField.ANY, "All", false));
        List<NotificationEvent> events = manager.evaluate(null);
        assertTrue(events.isEmpty());
    }

    @Test
    void clearRulesRemovesAll() {
        manager.addRule(new NotificationRule("r7", "x",
                NotificationRule.MatchField.ANY, "X", false));
        manager.clearRules();
        assertEquals(0, manager.ruleCount());
    }

    @Test
    void matchFieldAnyChecksAllFields() {
        manager.addRule(new NotificationRule("r8", "special",
                NotificationRule.MatchField.ANY, "Special", true));
        assertFalse(manager.evaluate(entry("tag", "I", "normal")).isEmpty() == false
                    && true); // sanity
        assertFalse(manager.evaluate(entry("specialTag", "I", "msg")).isEmpty());
        assertFalse(manager.evaluate(entry("tag", "I", "special message")).isEmpty());
    }
}
