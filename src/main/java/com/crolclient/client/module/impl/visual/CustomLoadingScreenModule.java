package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ModeSetting;

/** Заменяет стандартный экран загрузки мира на кастомный (тема оформления учитывается). */
public class CustomLoadingScreenModule extends Module {
    private final ModeSetting styleSetting = new ModeSetting("Стиль", "Минимал", "Минимал", "Классика", "Тема клиента");

    public CustomLoadingScreenModule() {
        super("CustomLoadingScreen", ModuleCategory.VISUAL, "Кастомный экран загрузки мира.");
        addSetting(styleSetting);
    }

    public String getStyle() {
        return styleSetting.getValue();
    }
}
