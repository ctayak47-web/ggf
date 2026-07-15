package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;

/**
 * Косметический след частиц за плащом/элитрами при полёте — чисто визуальный
 * эффект (аналог декоративных крыльев/следов в лаунчерах), не влияет на
 * скорость полёта, урон от падения или коллизии.
 */
public class CapeTrailModule extends Module {
    private final ColorSetting trailColorSetting = new ColorSetting("Цвет следа", 0xFF60A5FA);

    public CapeTrailModule() {
        super("CapeTrail", ModuleCategory.VISUAL, "Декоративный след частиц за плащом при полёте на элитрах.");
        addSetting(trailColorSetting);
    }

    public int getTrailColor() {
        return trailColorSetting.get();
    }
}
