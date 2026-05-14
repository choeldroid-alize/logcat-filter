package com.logcatfilter.alias;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AliasRegistryTest {

    private AliasRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new AliasRegistry();
    }

    @Test
    void testRegisterAndResolve() {
        registry.register("err", "level:ERROR");
        Optional<String> result = registry.resolve("err");
        assertTrue(result.isPresent());
        assertEquals("level:ERROR", result.get());
    }

    @Test
    void testResolveUnknownAlias() {
        Optional<String> result = registry.resolve("unknown");
        assertFalse(result.isPresent());
    }

    @Test
    void testResolveNullReturnsEmpty() {
        Optional<String> result = registry.resolve(null);
        assertFalse(result.isPresent());
    }

    @Test
    void testRegisterTrimsWhitespace() {
        registry.register("  warn  ", "  level:WARN  ");
        Optional<String> result = registry.resolve("warn");
        assertTrue(result.isPresent());
        assertEquals("level:WARN", result.get());
    }

    @Test
    void testRegisterBlankNameThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.register("", "level:ERROR"));
        assertThrows(IllegalArgumentException.class, () -> registry.register(null, "level:ERROR"));
    }

    @Test
    void testRegisterBlankExpansionThrows() {
        assertThrows(IllegalArgumentException.class, () -> registry.register("err", ""));
        assertThrows(IllegalArgumentException.class, () -> registry.register("err", null));
    }

    @Test
    void testRemoveAlias() {
        registry.register("err", "level:ERROR");
        boolean removed = registry.remove("err");
        assertTrue(removed);
        assertFalse(registry.resolve("err").isPresent());
    }

    @Test
    void testRemoveNonExistentReturnsFalse() {
        assertFalse(registry.remove("nonexistent"));
        assertFalse(registry.remove(null));
    }

    @Test
    void testExpand() {
        registry.register("err", "level:ERROR");
        registry.register("myapp", "tag:MyApp");
        String expanded = registry.expand("err AND myapp");
        assertEquals("level:ERROR AND tag:MyApp", expanded);
    }

    @Test
    void testExpandNullOrBlank() {
        assertNull(registry.expand(null));
        assertEquals("", registry.expand(""));
        assertEquals("   ", registry.expand("   "));
    }

    @Test
    void testGetAll() {
        registry.register("err", "level:ERROR");
        registry.register("warn", "level:WARN");
        Map<String, String> all = registry.getAll();
        assertEquals(2, all.size());
        assertEquals("level:ERROR", all.get("err"));
    }

    @Test
    void testGetAllIsUnmodifiable() {
        registry.register("err", "level:ERROR");
        Map<String, String> all = registry.getAll();
        assertThrows(UnsupportedOperationException.class, () -> all.put("x", "y"));
    }

    @Test
    void testClear() {
        registry.register("err", "level:ERROR");
        registry.clear();
        assertEquals(0, registry.size());
        assertFalse(registry.resolve("err").isPresent());
    }
}
