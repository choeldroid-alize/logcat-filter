package com.logcatfilter.throttle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ThrottleConfigTest {

    @Test
    void constructorStoresValues() {
        ThrottleConfig config = new ThrottleConfig(100, 500L, true);
        assertEquals(100, config.getMaxLinesPerWindow());
        assertEquals(500L, config.getWindowMillis());
        assertTrue(config.isDropExcess());
    }

    @Test
    void defaultsFactoryMatchesConstants() {
        ThrottleConfig config = ThrottleConfig.defaults();
        assertEquals(ThrottleConfig.DEFAULT_MAX_LINES_PER_SECOND, config.getMaxLinesPerWindow());
        assertEquals(ThrottleConfig.DEFAULT_WINDOW_MS, config.getWindowMillis());
        assertEquals(ThrottleConfig.DEFAULT_DROP_EXCESS, config.isDropExcess());
    }

    @Test
    void toStringContainsKeyFields() {
        ThrottleConfig config = new ThrottleConfig(200, 1000L, false);
        String str = config.toString();
        assertTrue(str.contains("200"));
        assertTrue(str.contains("1000"));
        assertTrue(str.contains("false"));
    }

    @Test
    void negativeMaxLinesThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ThrottleConfig(-1, 1000L, false));
    }

    @Test
    void zeroWindowThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ThrottleConfig(10, 0L, true));
    }

    @Test
    void throttleResultToStringContainsAction() {
        ThrottleResult result = new ThrottleResult(ThrottleResult.Action.ALLOW, 1, 10, 800L);
        assertTrue(result.toString().contains("ALLOW"));
        assertTrue(result.toString().contains("1/10"));
    }
}
