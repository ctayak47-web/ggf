package com.crolclient.client.module.setting;

/**
 * Базовый класс настройки модуля (обобщённый по типу значения T).
 * Используется ClickGUI для автоматической генерации виджетов настроек:
 * BooleanSetting -> чекбокс, NumberSetting -> слайдер, ColorSetting -> палитра,
 * ModeSetting -> выпадающий список / переключатель режимов.
 */
public abstract class Setting<T> {

    private final String name;
    protected T value;

    protected Setting(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
