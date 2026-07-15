package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Отображает текущий FPS в углу экрана. */
public class FpsHudModule extends Module {
    public FpsHudModule() {
        super("FpsHUD", ModuleCategory.HUD, "Показывает текущий FPS.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        int fps = mc.getCurrentFps();
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, "FPS: " + fps, 5, 18, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
