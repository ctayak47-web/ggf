package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;

/** Настраивает внешний вид панели Scoreboard (боковая таблица результатов). */
public class CustomScoreboardModule extends Module {
    private final ColorSetting backgroundSetting = new ColorSetting("Цвет фона", 0x70000000);
    private final NumberSetting cornerRadiusSetting = new NumberSetting("Скругление", 4.0, 0.0, 10.0, 1.0);

    public CustomScoreboardModule() {
        super("CustomScoreboard", ModuleCategory.VISUAL, "Кастомный фон и скругление панели Scoreboard.");
        addSetting(backgroundSetting);
        addSetting(cornerRadiusSetting);
    }

    public int getBackgroundColor() { return backgroundSetting.get(); }
    public double getCornerRadius() { return cornerRadiusSetting.get(); }
}
