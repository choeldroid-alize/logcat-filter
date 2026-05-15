package com.logcatfilter.levelmap;

import com.logcatfilter.levelmap.LevelColorMap.LogLevel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class LevelColorMapTest {

    private LevelColorMap map;

    @BeforeEach
    void setUp() {
        map = LevelColorMapFactory.createDefault();
    }

    @Test
    void defaultMapContainsAllLevels() {
        for (LogLevel level : LogLevel.values()) {
            assertTrue(map.hasColor(level), "Missing color for level: " + level);
        }
        assertEquals(6, map.size());
    }

    @Test
    void getColorReturnsExpectedAnsiCode() {
        Optional<String> color = map.getColor(LogLevel.ERROR);
        assertTrue(color.isPresent());
        assertEquals(LevelColorMapFactory.ANSI_RED, color.get());
    }

    @Test
    void setColorOverridesExistingEntry() {
        map.setColor(LogLevel.INFO, LevelColorMapFactory.ANSI_MAGENTA);
        assertEquals(LevelColorMapFactory.ANSI_MAGENTA, map.getColor(LogLevel.INFO).orElseThrow());
    }

    @Test
    void removeColorMakesLevelAbsent() {
        map.removeColor(LogLevel.DEBUG);
        assertFalse(map.hasColor(LogLevel.DEBUG));
        assertTrue(map.getColor(LogLevel.DEBUG).isEmpty());
    }

    @Test
    void fromCharParsesAllValidChars() {
        assertEquals(LogLevel.VERBOSE, LevelColorMap.fromChar('V'));
        assertEquals(LogLevel.DEBUG,   LevelColorMap.fromChar('d'));
        assertEquals(LogLevel.INFO,    LevelColorMap.fromChar('I'));
        assertEquals(LogLevel.WARN,    LevelColorMap.fromChar('W'));
        assertEquals(LogLevel.ERROR,   LevelColorMap.fromChar('E'));
        assertEquals(LogLevel.FATAL,   LevelColorMap.fromChar('F'));
    }

    @Test
    void fromCharThrowsOnUnknown() {
        assertThrows(IllegalArgumentException.class, () -> LevelColorMap.fromChar('X'));
    }

    @Test
    void setColorThrowsOnNullLevel() {
        assertThrows(IllegalArgumentException.class, () -> map.setColor(null, "\033[32m"));
    }

    @Test
    void setColorThrowsOnBlankColor() {
        assertThrows(IllegalArgumentException.class, () -> map.setColor(LogLevel.INFO, "  "));
    }

    @Test
    void monochromeMapMapsAllToReset() {
        LevelColorMap mono = LevelColorMapFactory.createMonochrome();
        for (LogLevel level : LogLevel.values()) {
            assertEquals(LevelColorMapFactory.ANSI_RESET, mono.getColor(level).orElseThrow());
        }
    }

    @Test
    void emptyMapHasNoEntries() {
        LevelColorMap empty = LevelColorMapFactory.createEmpty();
        assertEquals(0, empty.size());
        assertTrue(empty.getColor(LogLevel.WARN).isEmpty());
    }

    @Test
    void asUnmodifiableMapThrowsOnMutation() {
        assertThrows(UnsupportedOperationException.class,
                () -> map.asUnmodifiableMap().put(LogLevel.INFO, "\033[32m"));
    }
}
