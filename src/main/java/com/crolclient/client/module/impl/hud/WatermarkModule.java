package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;
import com.crolclient.client.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/**
 * Классический "watermark" в углу экрана: название клиента (+ опционально FPS).
 */
public class WatermarkModule extends Module {

    private final BooleanSetting showFpsSetting = new BooleanSetting("Показывать FPS", true);

    public WatermarkModule() {
        super("Watermark", ModuleCategory.HUD, "Название клиента в углу экрана.");
        addSetting(showFpsSetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        var theme = ThemeManager.getInstance().getCurrentTheme();
        String text = "CrolClient";
        if (showFpsSetting.get()) {
            text += " | " + mc.getCurrentFps() + " fps";
        }
        context.drawText(mc.textRenderer, Text.literal(text), 5, 5, theme.accentColorArgb(), true);
    }
}
