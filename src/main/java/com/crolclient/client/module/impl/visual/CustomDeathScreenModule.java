package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;

/** Заменяет стандартный экран смерти на кастомный дизайн со статистикой сессии. */
public class CustomDeathScreenModule extends Module {
    private final BooleanSetting showStatsSetting = new BooleanSetting("Показывать статистику", true);

    public CustomDeathScreenModule() {
        super("CustomDeathScreen", ModuleCategory.VISUAL, "Кастомный экран смерти с дополнительной статистикой сессии.");
        addSetting(showStatsSetting);
    }

    public boolean showStats() {
        return showStatsSetting.get();
    }
}
