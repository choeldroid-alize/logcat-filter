package com.logcatfilter.hotkey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class HotkeyRegistryTest {

    private HotkeyRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new HotkeyRegistry();
    }

    @Test
    void defaultBindingsAreRegistered() {
        assertTrue(registry.size() > 0);
        assertTrue(registry.findByKey("CTRL+F").isPresent());
        assertTrue(registry.findByKey("CTRL+P").isPresent());
    }

    @Test
    void findByKeyIsCaseInsensitive() {
        Optional<HotkeyBinding> result = registry.findByKey("ctrl+f");
        assertTrue(result.isPresent());
        assertEquals("search.open", result.get().getActionId());
    }

    @Test
    void findByActionReturnsCorrectBinding() {
        Optional<HotkeyBinding> result = registry.findByAction("pause.toggle");
        assertTrue(result.isPresent());
        assertEquals("CTRL+P", result.get().getKeyCombo());
    }

    @Test
    void registerOverridesExistingBinding() {
        HotkeyBinding custom = new HotkeyBinding("CTRL+F", "custom.action", "Custom");
        registry.register(custom);
        Optional<HotkeyBinding> result = registry.findByKey("CTRL+F");
        assertTrue(result.isPresent());
        assertEquals("custom.action", result.get().getActionId());
    }

    @Test
    void unregisterRemovesBinding() {
        assertTrue(registry.findByKey("CTRL+B").isPresent());
        boolean removed = registry.unregister("CTRL+B");
        assertTrue(removed);
        assertFalse(registry.findByKey("CTRL+B").isPresent());
        assertFalse(registry.findByAction("bookmark.toggle").isPresent());
    }

    @Test
    void unregisterNonExistentReturnsFalse() {
        assertFalse(registry.unregister("CTRL+Z"));
    }

    @Test
    void findByKeyReturnsEmptyForNull() {
        assertFalse(registry.findByKey(null).isPresent());
    }

    @Test
    void getAllBindingsIsUnmodifiable() {
        List<HotkeyBinding> bindings = registry.getAllBindings();
        assertNotNull(bindings);
        assertThrows(UnsupportedOperationException.class,
            () -> bindings.add(new HotkeyBinding("F2", "test", "Test")));
    }

    @Test
    void pageDownBindingIsRepeatable() {
        Optional<HotkeyBinding> result = registry.findByKey("PAGE_DOWN");
        assertTrue(result.isPresent());
        assertTrue(result.get().isRepeatable());
    }
}
