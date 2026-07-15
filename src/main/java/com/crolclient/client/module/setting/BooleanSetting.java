package com.crolclient.client.module.setting;

/** Настройка типа "чекбокс" (вкл/выкл). */
public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public boolean get() {
        return value;
    }
}
