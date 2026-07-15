package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;

/** Кастомизация внешнего вида хотбара: цвет обводки выбранного слота, скругление, масштаб. */
public class CustomHotbarModule extends Module {
    private final ColorSetting selectionColorSetting = new ColorSetting("Цвет выделения", 0xFFFFFFFF);
    private final NumberSetting scaleSetting = new NumberSetting("Масштаб", 1.0, 0.5, 1.5, 0.05);

    public CustomHotbarModule() {
        super("CustomHotbar", ModuleCategory.VISUAL, "Кастомный цвет выделения слота и масштаб хотбара.");
        addSetting(selectionColorSetting);
        addSetting(scaleSetting);
    }

    public int getSelectionColor() { return selectionColorSetting.get(); }
    public double getScale() { return scaleSetting.get(); }
}
