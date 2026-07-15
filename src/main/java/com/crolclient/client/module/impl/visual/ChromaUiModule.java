package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;

/** Плавная перецветовка (RGB-хрома) акцентных элементов ClickGUI со временем. */
public class ChromaUiModule extends Module {
    private final NumberSetting speedSetting = new NumberSetting("Скорость", 1.0, 0.1, 5.0, 0.1);

    public ChromaUiModule() {
        super("ChromaUI", ModuleCategory.VISUAL, "Циклическая радужная подсветка акцентных элементов интерфейса.");
        addSetting(speedSetting);
    }

    /** Возвращает текущий ARGB-цвет хромы на основе времени — используется рендером ClickGUI. */
    public int getCurrentColor() {
        float hue = (float) ((System.currentTimeMillis() / 1000.0 * speedSetting.get()) % 1.0);
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.8f, 1.0f) & 0xFFFFFF;
        return 0xFF000000 | rgb;
    }
}
