package com.crolclient.client.module.setting;

/** Настройка типа "цвет" — хранится как ARGB int. */
public class ColorSetting extends Setting<Integer> {
    public ColorSetting(String name, int defaultArgb) {
        super(name, defaultArgb);
    }

    public int get() {
        return value;
    }

    public int getRed() {
        return (value >> 16) & 0xFF;
    }

    public int getGreen() {
        return (value >> 8) & 0xFF;
    }

    public int getBlue() {
        return value & 0xFF;
    }

    public int getAlpha() {
        return (value >> 24) & 0xFF;
    }
}
