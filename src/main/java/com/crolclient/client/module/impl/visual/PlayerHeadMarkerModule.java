package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ModeSetting;

/**
 * Декоративный 3D-маркер/значок над головой СВОЕГО персонажа, настраиваемый
 * текущей темой оформления (например, корона в цвет акцента темы). Видим
 * только локально клиенту владельца — чистая косметика уровня рендера.
 */
public class PlayerHeadMarkerModule extends Module {
    private final ModeSetting styleSetting = new ModeSetting("Стиль маркера", "Корона", "Корона", "Звезда", "Кольцо");

    public PlayerHeadMarkerModule() {
        super("PlayerHeadMarker", ModuleCategory.VISUAL, "Декоративный маркер над головой своего персонажа, цвет берётся из темы.");
        addSetting(styleSetting);
    }

    public String getStyle() {
        return styleSetting.getValue();
    }
}
