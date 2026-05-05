package com.logcatfilter.tag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TagRegistryTest {

    private TagRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TagRegistry();
    }

    @Test
    void registerAndLookupTag() {
        registry.register("MyApp", "My Application", "\033[32m");

        Optional<TagDefinition> result = registry.lookup("MyApp");

        assertTrue(result.isPresent());
        assertEquals("MyApp", result.get().getTag());
        assertEquals("My Application", result.get().getLabel());
        assertEquals("\033[32m", result.get().getAnsiColor());
    }

    @Test
    void lookupUnknownTagReturnsEmpty() {
        Optional<TagDefinition> result = registry.lookup("Unknown");
        assertFalse(result.isPresent());
    }

    @Test
    void registerOverwritesExistingTag() {
        registry.register("MyApp", "Old Label", "\033[31m");
        registry.register("MyApp", "New Label", "\033[32m");

        TagDefinition def = registry.lookup("MyApp").orElseThrow();
        assertEquals("New Label", def.getLabel());
        assertEquals("\033[32m", def.getAnsiColor());
        assertEquals(1, registry.size());
    }

    @Test
    void unregisterRemovesTag() {
        registry.register("MyApp", "My Application", null);
        assertTrue(registry.unregister("MyApp"));
        assertFalse(registry.lookup("MyApp").isPresent());
    }

    @Test
    void unregisterNonExistentTagReturnsFalse() {
        assertFalse(registry.unregister("Ghost"));
    }

    @Test
    void registerBlankTagThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.register("  ", "label", null));
        assertThrows(IllegalArgumentException.class, () -> registry.register(null, "label", null));
    }

    @Test
    void colorizeWrapsTextWithAnsiCodes() {
        registry.register("MyApp", "My Application", "\033[33m");
        TagDefinition def = registry.lookup("MyApp").orElseThrow();

        String colorized = def.colorize("hello");
        assertEquals("\033[33m" + "hello" + TagDefinition.ANSI_RESET, colorized);
    }

    @Test
    void colorizeWithNoColorReturnsTextUnchanged() {
        registry.register("MyApp", "My Application", null);
        TagDefinition def = registry.lookup("MyApp").orElseThrow();

        assertEquals("hello", def.colorize("hello"));
    }

    @Test
    void labelDefaultsToTagWhenNull() {
        registry.register("MyApp", null, null);
        TagDefinition def = registry.lookup("MyApp").orElseThrow();
        assertEquals("MyApp", def.getLabel());
    }
}
