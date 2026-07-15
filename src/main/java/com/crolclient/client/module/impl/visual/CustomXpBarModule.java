package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;

/** Перекрашивает полосу опыта и меняет её стиль. */
public class CustomXpBarModule extends Module {
    private final ColorSetting colorSetting = new ColorSetting("Цвет полосы опыта", 0xFF22C55E);

    public CustomXpBarModule() {
        super("CustomXpBar", ModuleCategory.VISUAL, "Меняет цвет и стиль полосы опыта.");
        addSetting(colorSetting);
    }

    public int getColor() {
        return colorSetting.get();
    }
}
