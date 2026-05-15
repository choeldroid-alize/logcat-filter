package com.logcatfilter.levelmap;

import com.logcatfilter.levelmap.LevelColorMap.LogLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LevelColorMapFactoryTest {

    @Test
    void defaultMapIsNotEmpty() {
        LevelColorMap map = LevelColorMapFactory.createDefault();
        assertEquals(LogLevel.values().length, map.size());
    }

    @Test
    void defaultMapVerboseIsWhite() {
        LevelColorMap map = LevelColorMapFactory.createDefault();
        assertEquals(LevelColorMapFactory.ANSI_WHITE, map.getColor(LogLevel.VERBOSE).orElseThrow());
    }

    @Test
    void defaultMapFatalIsMagenta() {
        LevelColorMap map = LevelColorMapFactory.createDefault();
        assertEquals(LevelColorMapFactory.ANSI_MAGENTA, map.getColor(LogLevel.FATAL).orElseThrow());
    }

    @Test
    void emptyMapHasSizeZero() {
        LevelColorMap map = LevelColorMapFactory.createEmpty();
        assertEquals(0, map.size());
    }

    @Test
    void monochromeMapHasAllLevels() {
        LevelColorMap map = LevelColorMapFactory.createMonochrome();
        assertEquals(LogLevel.values().length, map.size());
    }

    @Test
    void defaultMapsAreIndependent() {
        LevelColorMap a = LevelColorMapFactory.createDefault();
        LevelColorMap b = LevelColorMapFactory.createDefault();
        a.setColor(LogLevel.DEBUG, LevelColorMapFactory.ANSI_RED);
        // Modifying 'a' must not affect 'b'
        assertEquals(LevelColorMapFactory.ANSI_CYAN, b.getColor(LogLevel.DEBUG).orElseThrow());
    }
}
