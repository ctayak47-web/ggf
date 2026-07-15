package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Горизонтальный компас с направлениями света вверху экрана. */
public class CompassHudModule extends Module {
    public CompassHudModule() {
        super("CompassHUD", ModuleCategory.HUD, "Горизонтальная полоса компаса вверху экрана.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        int centerX = mc.getWindow().getScaledWidth() / 2;
        int y = 4;
        float yaw = mc.player.getYaw(tickDelta) % 360;
        if (yaw < 0) yaw += 360;

        String[] directions = {"Ю", "ЮЗ", "З", "СЗ", "С", "СВ", "В", "ЮВ"};
        var theme = ThemeManager.getInstance().getCurrentTheme();

        context.fill(centerX - 60, y, centerX + 60, y + 10, theme.backgroundColorArgb());
        int index = Math.round(yaw / 45f) % 8;
        String currentDir = directions[index];
        int width = mc.textRenderer.getWidth(currentDir);
        context.drawText(mc.textRenderer, Text.literal(currentDir), centerX - width / 2, y + 1, theme.accentColorArgb(), true);
    }
}
