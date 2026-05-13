package com.logcatfilter.hotkey;

import java.util.Objects;

/**
 * Represents a single hotkey binding mapping a key combination to an action.
 */
public class HotkeyBinding {

    private final String keyCombo;
    private final String actionId;
    private final String description;
    private final boolean repeatable;

    public HotkeyBinding(String keyCombo, String actionId, String description, boolean repeatable) {
        if (keyCombo == null || keyCombo.isBlank()) {
            throw new IllegalArgumentException("Key combo must not be blank");
        }
        if (actionId == null || actionId.isBlank()) {
            throw new IllegalArgumentException("Action ID must not be blank");
        }
        this.keyCombo = keyCombo.toUpperCase();
        this.actionId = actionId;
        this.description = description != null ? description : "";
        this.repeatable = repeatable;
    }

    public HotkeyBinding(String keyCombo, String actionId, String description) {
        this(keyCombo, actionId, description, false);
    }

    public String getKeyCombo() {
        return keyCombo;
    }

    public String getActionId() {
        return actionId;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HotkeyBinding)) return false;
        HotkeyBinding that = (HotkeyBinding) o;
        return Objects.equals(keyCombo, that.keyCombo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyCombo);
    }

    @Override
    public String toString() {
        return "HotkeyBinding{keyCombo='" + keyCombo + "', actionId='" + actionId +
               "', repeatable=" + repeatable + "}";
    }
}
