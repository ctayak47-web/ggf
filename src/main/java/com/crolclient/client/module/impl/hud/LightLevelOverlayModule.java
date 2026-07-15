package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.BlockPos;

/**
 * Информационный индикатор уровня освещения блока под ногами игрока
 * (справочная информация о возможности спавна мобов — идентична данным,
 * доступным через F3, просто выводится компактнее).
 */
public class LightLevelOverlayModule extends Module {
    public LightLevelOverlayModule() {
        super("LightLevelOverlay", ModuleCategory.HUD, "Показывает уровень освещения под ногами (справочно, для контроля спавна мобов).");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;
        BlockPos pos = mc.player.getBlockPos();
        int light = mc.world.getLightLevel(pos);
        int color = light <= 7 ? 0xFFEF4444 : 0xFF22C55E;
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, "Освещение: " + light, 5, 109, color, theme.backgroundColorArgb());
    }
}
