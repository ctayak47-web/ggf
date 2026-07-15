package com.crolclient.client.module;

/**
 * Категории модулей CrolClient.
 * <p>
 * Используются в ClickGUI для группировки модулей по вкладкам, а также
 * при поиске/фильтрации. Чтобы добавить новую категорию — просто добавьте
 * новую константу сюда, и она автоматически появится в ClickGUI, так как
 * список вкладок строится динамически через {@link ModuleCategory#values()}.
 */
public enum ModuleCategory {
    VISUAL("Визуал"),
    HUD("HUD"),
    UTILITY("Утилиты");

    private final String displayName;

    ModuleCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
