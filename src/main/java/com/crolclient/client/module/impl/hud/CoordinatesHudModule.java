package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Отображает текущие координаты игрока (X/Y/Z) в углу экрана. */
public class CoordinatesHudModule extends Module {
    public CoordinatesHudModule() {
        super("CoordinatesHUD", ModuleCategory.HUD, "Показывает текущие координаты игрока на экране.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        String text = String.format("X: %.1f  Y: %.1f  Z: %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, text, 5, 5, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
