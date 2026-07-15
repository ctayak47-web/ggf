package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;

/** Настраивает фон, прозрачность и масштаб шрифта окна чата. */
public class ChatCustomizerModule extends Module {
    private final ColorSetting backgroundSetting = new ColorSetting("Цвет фона", 0x60000000);
    private final NumberSetting opacitySetting = new NumberSetting("Прозрачность", 0.6, 0.0, 1.0, 0.05);
    private final NumberSetting scaleSetting = new NumberSetting("Масштаб шрифта", 1.0, 0.5, 2.0, 0.1);

    public ChatCustomizerModule() {
        super("ChatCustomizer", ModuleCategory.VISUAL, "Настраивает фон, прозрачность и масштаб шрифта чата.");
        addSetting(backgroundSetting);
        addSetting(opacitySetting);
        addSetting(scaleSetting);
    }

    public int getBackgroundColor() { return backgroundSetting.get(); }
    public double getOpacity() { return opacitySetting.get(); }
    public double getScale() { return scaleSetting.get(); }
}
