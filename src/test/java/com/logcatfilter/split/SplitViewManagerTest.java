package com.logcatfilter.split;

import com.logcatfilter.buffer.BufferConfig;
import com.logcatfilter.buffer.LogBuffer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplitViewManagerTest {

    private LogBuffer primary;
    private LogBuffer secondary;
    private SplitViewConfig config;
    private SplitViewManager manager;

    @BeforeEach
    void setUp() {
        BufferConfig bc = new BufferConfig(1000, true);
        primary = new LogBuffer(bc);
        secondary = new LogBuffer(bc);
        config = new SplitViewConfig(true, SplitViewConfig.Orientation.HORIZONTAL, 0.5, false, true);
        manager = new SplitViewManager(config, primary);
    }

    @Test
    void constructorRejectsNullConfig() {
        assertThrows(IllegalArgumentException.class,
                () -> new SplitViewManager(null, primary));
    }

    @Test
    void constructorRejectsNullBuffer() {
        assertThrows(IllegalArgumentException.class,
                () -> new SplitViewManager(config, null));
    }

    @Test
    void splitNotActiveWithoutSecondaryBuffer() {
        assertFalse(manager.isSplitActive());
        assertTrue(manager.getSecondaryBuffer().isEmpty());
    }

    @Test
    void splitActiveAfterAttach() {
        manager.attachSecondaryBuffer(secondary);
        assertTrue(manager.isSplitActive());
        assertTrue(manager.getSecondaryBuffer().isPresent());
    }

    @Test
    void detachRemovesSecondary() {
        manager.attachSecondaryBuffer(secondary);
        manager.detachSecondaryBuffer();
        assertFalse(manager.isSplitActive());
    }

    @Test
    void resizeWithSplitAndDividerAllocatesCorrectly() {
        manager.attachSecondaryBuffer(secondary);
        manager.resize(21);  // 21 total, 1 divider => 20 usable, 50/50 => 10/10
        assertEquals(10, manager.getPrimaryPaneSize());
        assertEquals(10, manager.getSecondaryPaneSize());
    }

    @Test
    void resizeWithoutSplitGivesAllLinesToPrimary() {
        // secondary not attached
        manager.resize(40);
        assertEquals(40, manager.getPrimaryPaneSize());
        assertEquals(0, manager.getSecondaryPaneSize());
    }

    @Test
    void resizeWithNoDivider() {
        SplitViewConfig noDivider = new SplitViewConfig(
                true, SplitViewConfig.Orientation.HORIZONTAL, 0.25, false, false);
        SplitViewManager m = new SplitViewManager(noDivider, primary);
        m.attachSecondaryBuffer(secondary);
        m.resize(20);  // no divider => 20 usable, 25% => 5 primary, 15 secondary
        assertEquals(5, m.getPrimaryPaneSize());
        assertEquals(15, m.getSecondaryPaneSize());
    }

    @Test
    void resizeInvalidTotalThrows() {
        assertThrows(IllegalArgumentException.class, () -> manager.resize(0));
        assertThrows(IllegalArgumentException.class, () -> manager.resize(-5));
    }

    @Test
    void updateConfigReplacesConfig() {
        SplitViewConfig newCfg = new SplitViewConfig(
                false, SplitViewConfig.Orientation.VERTICAL, 0.6, true, false);
        manager.updateConfig(newCfg);
        assertSame(newCfg, manager.getConfig());
    }

    @Test
    void updateConfigRejectsNull() {
        assertThrows(IllegalArgumentException.class, () -> manager.updateConfig(null));
    }
}
