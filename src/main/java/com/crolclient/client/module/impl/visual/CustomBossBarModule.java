package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;

/** Перекрашивает и стилизует полосу здоровья босса. */
public class CustomBossBarModule extends Module {
    private final ColorSetting barColorSetting = new ColorSetting("Цвет полосы", 0xFFDC2626);

    public CustomBossBarModule() {
        super("CustomBossBar", ModuleCategory.VISUAL, "Кастомный цвет и стиль полосы здоровья босса.");
        addSetting(barColorSetting);
    }

    public int getBarColor() {
        return barColorSetting.get();
    }
}
