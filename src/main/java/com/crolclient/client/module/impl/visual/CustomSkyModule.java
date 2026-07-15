package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;

/**
 * Заменяет цвет неба на кастомный. Значение читается рендер-миксином неба
 * (WorldRenderer hook) — чисто визуальный оверлей, не влияет на видимость
 * мобов/игроков и не даёт информации о погоде/времени сверх обычного.
 */
public class CustomSkyModule extends Module {
    private final ColorSetting skyColorSetting = new ColorSetting("Цвет неба", 0xFF87CEEB);

    public CustomSkyModule() {
        super("CustomSky", ModuleCategory.VISUAL, "Заменяет цвет неба на выбранный.");
        addSetting(skyColorSetting);
    }

    public int getSkyColor() {
        return skyColorSetting.get();
    }
}
