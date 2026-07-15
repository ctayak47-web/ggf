package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Сглаживает движение камеры (плавная интерполяция вместо мгновенного
 * поворота) для съёмки кинематографичных роликов. Не влияет на реальное
 * направление взгляда персонажа, используемое сервером.
 */
public class CinematicCameraModule extends Module {
    private final NumberSetting smoothnessSetting = new NumberSetting("Плавность", 0.5, 0.0, 0.95, 0.05);

    public CinematicCameraModule() {
        super("CinematicCamera", ModuleCategory.VISUAL, "Плавное сглаживание движения камеры для съёмки роликов.");
        addSetting(smoothnessSetting);
    }

    public double getSmoothness() {
        return smoothnessSetting.get();
    }
}
