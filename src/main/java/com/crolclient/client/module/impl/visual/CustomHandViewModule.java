package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;

/**
 * Настраивает позицию/масштаб отображения предмета в руке (view model) —
 * аналог опции "FOV руки" в некоторых лаунчерах. Только визуальное смещение
 * модели от первого лица, не меняет hitbox или скорость атаки/использования.
 */
public class CustomHandViewModule extends Module {
    private final NumberSetting scaleSetting = new NumberSetting("Масштаб руки", 1.0, 0.5, 1.5, 0.05);
    private final NumberSetting offsetXSetting = new NumberSetting("Смещение X", 0.0, -1.0, 1.0, 0.05);
    private final NumberSetting offsetYSetting = new NumberSetting("Смещение Y", 0.0, -1.0, 1.0, 0.05);

    public CustomHandViewModule() {
        super("CustomHandView", ModuleCategory.VISUAL, "Меняет масштаб и позицию предмета/руки от первого лица.");
        addSetting(scaleSetting);
        addSetting(offsetXSetting);
        addSetting(offsetYSetting);
    }

    public double getScale() { return scaleSetting.get(); }
    public double getOffsetX() { return offsetXSetting.get(); }
    public double getOffsetY() { return offsetYSetting.get(); }
}
