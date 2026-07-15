package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;

/** Стилизует список игроков (Tab-меню): фон, разделители, порядок сортировки. */
public class TabListCustomizerModule extends Module {
    private final BooleanSetting sortByPingSetting = new BooleanSetting("Сортировать по пингу", false);

    public TabListCustomizerModule() {
        super("TabListCustomizer", ModuleCategory.VISUAL, "Стилизует список игроков по Tab (фон, сортировка).");
        addSetting(sortByPingSetting);
    }

    public boolean sortByPing() {
        return sortByPingSetting.get();
    }
}
