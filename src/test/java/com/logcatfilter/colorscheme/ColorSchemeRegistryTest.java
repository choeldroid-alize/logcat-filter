package com.logcatfilter.colorscheme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ColorSchemeRegistryTest {

    private ColorSchemeRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ColorSchemeRegistry();
    }

    @Test
    void defaultRegistryHasTwoBuiltInSchemes() {
        assertEquals(2, registry.size());
        assertTrue(registry.contains("dark"));
        assertTrue(registry.contains("plain"));
    }

    @Test
    void defaultActiveSchemeIsDark() {
        assertEquals("dark", registry.getActive().getName());
    }

    @Test
    void setActiveSwitchesScheme() {
        registry.setActive("plain");
        assertEquals("plain", registry.getActive().getName());
    }

    @Test
    void setActiveUnknownSchemeThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.setActive("nonexistent"));
    }

    @Test
    void registerCustomSchemeAndActivate() {
        Map<ColorScheme.Element, String> colors = new HashMap<>();
        colors.put(ColorScheme.Element.LEVEL_ERROR, "\u001B[31m");
        ColorScheme custom = new ColorScheme("solarized", colors);

        registry.register(custom);
        assertTrue(registry.contains("solarized"));
        assertEquals(3, registry.size());

        registry.setActive("solarized");
        assertEquals("solarized", registry.getActive().getName());
    }

    @Test
    void getReturnsEmptyForUnknownScheme() {
        assertTrue(registry.get("unknown").isEmpty());
    }

    @Test
    void removeNonActiveSchemeSucceeds() {
        registry.setActive("dark");
        registry.remove("plain");
        assertFalse(registry.contains("plain"));
        assertEquals(1, registry.size());
    }

    @Test
    void removeActiveSchemeThrows() {
        assertThrows(IllegalStateException.class, () -> registry.remove("dark"));
    }

    @Test
    void darkSchemeHasColorForAllLevels() {
        ColorScheme dark = registry.getActive();
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_ERROR).isEmpty());
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_WARN).isEmpty());
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_INFO).isEmpty());
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_DEBUG).isEmpty());
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_VERBOSE).isEmpty());
        assertFalse(dark.getColor(ColorScheme.Element.LEVEL_FATAL).isEmpty());
    }

    @Test
    void plainSchemeReturnsEmptyStringForAnyElement() {
        ColorScheme plain = registry.get("plain").orElseThrow();
        assertEquals("", plain.getColor(ColorScheme.Element.LEVEL_ERROR));
        assertFalse(plain.hasColor(ColorScheme.Element.TIMESTAMP));
    }

    @Test
    void registerNullSchemeThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.register(null));
    }
}
