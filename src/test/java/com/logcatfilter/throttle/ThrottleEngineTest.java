package com.logcatfilter.throttle;

import com.logcatfilter.parser.LogcatEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThrottleEngineTest {

    private LogcatEntry entry;

    @BeforeEach
    void setUp() {
        entry = new LogcatEntry("2024-01-01 12:00:00.000", 1000, 1001, "D", "TestTag", "message");
    }

    @Test
    void allowsEntriesWithinLimit() {
        ThrottleConfig config = new ThrottleConfig(3, 1000L, true);
        ThrottleEngine engine = new ThrottleEngine(config);

        assertEquals(ThrottleResult.Action.ALLOW, engine.evaluate(entry).getAction());
        assertEquals(ThrottleResult.Action.ALLOW, engine.evaluate(entry).getAction());
        assertEquals(ThrottleResult.Action.ALLOW, engine.evaluate(entry).getAction());
    }

    @Test
    void dropsExcessWhenConfiguredToDrop() {
        ThrottleConfig config = new ThrottleConfig(2, 1000L, true);
        ThrottleEngine engine = new ThrottleEngine(config);

        engine.evaluate(entry); // 1 – ALLOW
        engine.evaluate(entry); // 2 – ALLOW
        ThrottleResult result = engine.evaluate(entry); // 3 – DROP

        assertEquals(ThrottleResult.Action.DROP, result.getAction());
        assertTrue(result.isDropped());
    }

    @Test
    void buffersExcessWhenConfiguredToBuffer() {
        ThrottleConfig config = new ThrottleConfig(2, 2000L, false);
        ThrottleEngine engine = new ThrottleEngine(config);

        engine.evaluate(entry); // ALLOW
        engine.evaluate(entry); // ALLOW
        ThrottleResult result = engine.evaluate(entry); // BUFFER

        assertEquals(ThrottleResult.Action.BUFFER, result.getAction());
        assertEquals(1, engine.getBufferedCount());
    }

    @Test
    void resetClearsCounterAndBuffer() {
        ThrottleConfig config = new ThrottleConfig(1, 2000L, false);
        ThrottleEngine engine = new ThrottleEngine(config);

        engine.evaluate(entry); // ALLOW
        engine.evaluate(entry); // BUFFER
        assertEquals(1, engine.getBufferedCount());

        engine.reset();
        assertEquals(0, engine.getBufferedCount());
        assertEquals(ThrottleResult.Action.ALLOW, engine.evaluate(entry).getAction());
    }

    @Test
    void resultExposesCountAndLimit() {
        ThrottleConfig config = new ThrottleConfig(5, 1000L, true);
        ThrottleEngine engine = new ThrottleEngine(config);

        ThrottleResult result = engine.evaluate(entry);
        assertEquals(1, result.getCurrentCount());
        assertEquals(5, result.getLimit());
        assertTrue(result.isAllowed());
    }

    @Test
    void throwsOnInvalidConfig() {
        assertThrows(IllegalArgumentException.class, () -> new ThrottleConfig(0, 1000L, false));
        assertThrows(IllegalArgumentException.class, () -> new ThrottleConfig(10, 0L, false));
    }

    @Test
    void defaultConfigHasExpectedValues() {
        ThrottleConfig config = ThrottleConfig.defaults();
        assertEquals(ThrottleConfig.DEFAULT_MAX_LINES_PER_SECOND, config.getMaxLinesPerWindow());
        assertEquals(ThrottleConfig.DEFAULT_WINDOW_MS, config.getWindowMillis());
        assertFalse(config.isDropExcess());
    }
}
