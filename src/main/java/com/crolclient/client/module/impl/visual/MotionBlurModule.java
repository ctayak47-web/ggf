package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Косметический эффект смазывания кадра при быстром движении камеры
 * (постпроцессинг-оверлей поверх кадра). Не влияет на игровую логику.
 */
public class MotionBlurModule extends Module {
    private final NumberSetting intensitySetting = new NumberSetting("Интенсивность", 0.3, 0.0, 1.0, 0.05);

    public MotionBlurModule() {
        super("MotionBlur", ModuleCategory.VISUAL, "Добавляет кинематографичное размытие движения камеры.");
        addSetting(intensitySetting);
    }

    public double getIntensity() {
        return intensitySetting.get();
    }
}
