package com.logcatfilter.split;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplitViewConfigTest {

    @Test
    void defaultConstructorSetsExpectedDefaults() {
        SplitViewConfig cfg = new SplitViewConfig();
        assertFalse(cfg.isEnabled());
        assertEquals(SplitViewConfig.Orientation.HORIZONTAL, cfg.getOrientation());
        assertEquals(0.5, cfg.getSplitRatio(), 1e-9);
        assertFalse(cfg.isSyncScroll());
        assertTrue(cfg.isShowDivider());
    }

    @Test
    void parameterisedConstructorStoresValues() {
        SplitViewConfig cfg = new SplitViewConfig(
                true, SplitViewConfig.Orientation.VERTICAL, 0.3, true, false);
        assertTrue(cfg.isEnabled());
        assertEquals(SplitViewConfig.Orientation.VERTICAL, cfg.getOrientation());
        assertEquals(0.3, cfg.getSplitRatio(), 1e-9);
        assertTrue(cfg.isSyncScroll());
        assertFalse(cfg.isShowDivider());
    }

    @Test
    void invalidSplitRatioThrowsOnConstruction() {
        assertThrows(IllegalArgumentException.class,
                () -> new SplitViewConfig(true, SplitViewConfig.Orientation.HORIZONTAL, 0.0, false, true));
        assertThrows(IllegalArgumentException.class,
                () -> new SplitViewConfig(true, SplitViewConfig.Orientation.HORIZONTAL, 1.0, false, true));
        assertThrows(IllegalArgumentException.class,
                () -> new SplitViewConfig(true, SplitViewConfig.Orientation.HORIZONTAL, 1.5, false, true));
    }

    @Test
    void setterUpdatesRatio() {
        SplitViewConfig cfg = new SplitViewConfig();
        cfg.setSplitRatio(0.7);
        assertEquals(0.7, cfg.getSplitRatio(), 1e-9);
    }

    @Test
    void setterRejectsInvalidRatio() {
        SplitViewConfig cfg = new SplitViewConfig();
        assertThrows(IllegalArgumentException.class, () -> cfg.setSplitRatio(0.0));
        assertThrows(IllegalArgumentException.class, () -> cfg.setSplitRatio(1.0));
    }

    @Test
    void nullOrientationFallsBackToHorizontal() {
        SplitViewConfig cfg = new SplitViewConfig();
        cfg.setOrientation(null);
        assertEquals(SplitViewConfig.Orientation.HORIZONTAL, cfg.getOrientation());
    }

    @Test
    void toStringContainsKeyFields() {
        SplitViewConfig cfg = new SplitViewConfig(
                true, SplitViewConfig.Orientation.VERTICAL, 0.4, true, true);
        String s = cfg.toString();
        assertTrue(s.contains("VERTICAL"));
        assertTrue(s.contains("0.4"));
        assertTrue(s.contains("true"));
    }
}
