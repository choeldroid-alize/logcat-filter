package com.logcatfilter.hotkey;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HotkeyBindingTest {

    @Test
    void constructorNormalizesKeyComboToUpperCase() {
        HotkeyBinding binding = new HotkeyBinding("ctrl+f", "search.open", "Open search");
        assertEquals("CTRL+F", binding.getKeyCombo());
    }

    @Test
    void constructorSetsAllFields() {
        HotkeyBinding binding = new HotkeyBinding("CTRL+B", "bookmark.toggle", "Toggle bookmark", true);
        assertEquals("CTRL+B", binding.getKeyCombo());
        assertEquals("bookmark.toggle", binding.getActionId());
        assertEquals("Toggle bookmark", binding.getDescription());
        assertTrue(binding.isRepeatable());
    }

    @Test
    void defaultRepeatableIsFalse() {
        HotkeyBinding binding = new HotkeyBinding("F1", "help.show", "Help");
        assertFalse(binding.isRepeatable());
    }

    @Test
    void nullDescriptionDefaultsToEmpty() {
        HotkeyBinding binding = new HotkeyBinding("HOME", "scroll.top", null);
        assertEquals("", binding.getDescription());
    }

    @Test
    void blankKeyComboThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new HotkeyBinding("", "action", "desc"));
    }

    @Test
    void blankActionIdThrows() {
        assertThrows(IllegalArgumentException.class,
            () -> new HotkeyBinding("CTRL+X", "", "desc"));
    }

    @Test
    void equalityBasedOnKeyCombo() {
        HotkeyBinding a = new HotkeyBinding("CTRL+F", "search.open", "Search");
        HotkeyBinding b = new HotkeyBinding("ctrl+f", "other.action", "Other");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void toStringContainsKeyAndAction() {
        HotkeyBinding binding = new HotkeyBinding("CTRL+P", "pause.toggle", "Pause");
        String str = binding.toString();
        assertTrue(str.contains("CTRL+P"));
        assertTrue(str.contains("pause.toggle"));
    }
}
