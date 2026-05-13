package com.logcatfilter.hotkey;

import java.util.*;

/**
 * Manages registration and lookup of hotkey bindings.
 * Provides default bindings for common logcat-filter actions.
 */
public class HotkeyRegistry {

    private final Map<String, HotkeyBinding> bindingsByKey = new LinkedHashMap<>();
    private final Map<String, HotkeyBinding> bindingsByAction = new LinkedHashMap<>();

    public HotkeyRegistry() {
        registerDefaults();
    }

    private void registerDefaults() {
        register(new HotkeyBinding("CTRL+F", "search.open", "Open search"));
        register(new HotkeyBinding("CTRL+B", "bookmark.toggle", "Toggle bookmark"));
        register(new HotkeyBinding("CTRL+P", "pause.toggle", "Pause/resume stream"));
        register(new HotkeyBinding("CTRL+E", "export.start", "Export logs"));
        register(new HotkeyBinding("CTRL+C", "app.quit", "Quit application"));
        register(new HotkeyBinding("PAGE_UP", "scroll.pageUp", "Scroll page up", true));
        register(new HotkeyBinding("PAGE_DOWN", "scroll.pageDown", "Scroll page down", true));
        register(new HotkeyBinding("HOME", "scroll.top", "Scroll to top"));
        register(new HotkeyBinding("END", "scroll.bottom", "Scroll to bottom"));
        register(new HotkeyBinding("CTRL+T", "tag.manage", "Manage tags"));
        register(new HotkeyBinding("CTRL+H", "highlight.manage", "Manage highlights"));
        register(new HotkeyBinding("F1", "help.show", "Show help"));
    }

    public void register(HotkeyBinding binding) {
        bindingsByKey.put(binding.getKeyCombo(), binding);
        bindingsByAction.put(binding.getActionId(), binding);
    }

    public Optional<HotkeyBinding> findByKey(String keyCombo) {
        if (keyCombo == null) return Optional.empty();
        return Optional.ofNullable(bindingsByKey.get(keyCombo.toUpperCase()));
    }

    public Optional<HotkeyBinding> findByAction(String actionId) {
        if (actionId == null) return Optional.empty();
        return Optional.ofNullable(bindingsByAction.get(actionId));
    }

    public boolean unregister(String keyCombo) {
        if (keyCombo == null) return false;
        HotkeyBinding removed = bindingsByKey.remove(keyCombo.toUpperCase());
        if (removed != null) {
            bindingsByAction.remove(removed.getActionId());
            return true;
        }
        return false;
    }

    public List<HotkeyBinding> getAllBindings() {
        return Collections.unmodifiableList(new ArrayList<>(bindingsByKey.values()));
    }

    public int size() {
        return bindingsByKey.size();
    }
}
