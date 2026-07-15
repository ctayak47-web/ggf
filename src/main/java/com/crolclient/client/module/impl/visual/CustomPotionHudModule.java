package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ModeSetting;

/** Заменяет расположение/стиль иконок активных эффектов зелий на экране. */
public class CustomPotionHudModule extends Module {
    private final ModeSetting positionSetting = new ModeSetting("Позиция", "Справа сверху", "Справа сверху", "Слева сверху", "Компактно снизу");

    public CustomPotionHudModule() {
        super("CustomPotionHud", ModuleCategory.VISUAL, "Настраивает позицию и стиль отображения активных эффектов зелий.");
        addSetting(positionSetting);
    }

    public String getPosition() {
        return positionSetting.getValue();
    }
}
