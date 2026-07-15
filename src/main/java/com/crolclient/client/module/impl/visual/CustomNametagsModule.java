package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Кастомизация внешнего вида ников над игроками: фон, прозрачность, цвет.
 * Настройки читаются рендер-миксином ника (стандартный ник виден всем игрокам
 * одинаково через ванильные пакеты — этот модуль лишь меняет то, КАК локальный
 * клиент рисует уже полученную информацию, а не то, ЧТО он видит).
 */
public class CustomNametagsModule extends Module {

    private final BooleanSetting backgroundSetting = new BooleanSetting("Фон", true);
    private final ColorSetting backgroundColorSetting = new ColorSetting("Цвет фона", 0x80000000);
    private final NumberSetting scaleSetting = new NumberSetting("Масштаб", 1.0, 0.5, 2.0, 0.1);

    public CustomNametagsModule() {
        super("CustomNametags", ModuleCategory.VISUAL, "Настраивает стиль, фон и прозрачность ников над игроками.");
        addSetting(backgroundSetting);
        addSetting(backgroundColorSetting);
        addSetting(scaleSetting);
    }

    public boolean hasBackground() {
        return backgroundSetting.get();
    }

    public int getBackgroundColor() {
        return backgroundColorSetting.get();
    }

    public double getScale() {
        return scaleSetting.get();
    }
}
