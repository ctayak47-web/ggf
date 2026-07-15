package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Меняет визуальную интенсивность дождя/снега на клиенте (плотность частиц
 * осадков). Погода на сервере не меняется — это чисто локальный рендер-фильтр.
 */
public class WeatherCustomizerModule extends Module {
    private final NumberSetting intensitySetting = new NumberSetting("Интенсивность осадков", 1.0, 0.0, 2.0, 0.1);

    public WeatherCustomizerModule() {
        super("WeatherCustomizer", ModuleCategory.VISUAL, "Меняет визуальную интенсивность дождя/снега на клиенте.");
        addSetting(intensitySetting);
    }

    public double getIntensity() {
        return intensitySetting.get();
    }
}
