package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Настраивает цвет и толщину обводки блока при наведении курсора
 * (стандартная ванильная функция подсветки выбранного блока — этот модуль
 * только меняет её внешний вид, не добавляет ESP сквозь стены и не
 * подсвечивает блоки за пределами обычной дальности взаимодействия).
 */
public class BlockOutlineCustomizerModule extends Module {
    private final ColorSetting colorSetting = new ColorSetting("Цвет обводки", 0xFF000000);
    private final NumberSetting thicknessSetting = new NumberSetting("Толщина", 2.0, 1.0, 5.0, 0.5);

    public BlockOutlineCustomizerModule() {
        super("BlockOutline", ModuleCategory.VISUAL, "Меняет цвет и толщину обводки выбранного блока (только визуал, дальность не меняется).");
        addSetting(colorSetting);
        addSetting(thicknessSetting);
    }

    public int getColor() { return colorSetting.get(); }
    public double getThickness() { return thicknessSetting.get(); }
}
