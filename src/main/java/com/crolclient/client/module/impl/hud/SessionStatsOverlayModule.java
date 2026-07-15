package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;

/** Показывает пройденное расстояние (в блоках) и время в игре за сессию. */
public class SessionStatsOverlayModule extends Module {
    private double blocksTraveled = 0;
    private Vec3d lastPos;
    private long sessionStart;

    public SessionStatsOverlayModule() {
        super("SessionStats", ModuleCategory.HUD, "Показывает пройденное расстояние и время в игре за сессию.");
    }

    @Override
    protected void onEnable() {
        sessionStart = System.currentTimeMillis();
        blocksTraveled = 0;
        lastPos = mc.player != null ? mc.player.getPos() : null;
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        Vec3d current = mc.player.getPos();
        if (lastPos != null) {
            blocksTraveled += lastPos.distanceTo(current);
        }
        lastPos = current;
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        var theme = ThemeManager.getInstance().getCurrentTheme();
        long elapsed = System.currentTimeMillis() - sessionStart;
        String text = String.format("Пройдено: %.0f блоков | %s", blocksTraveled, RenderUtil.formatDuration(elapsed));
        RenderUtil.drawTextWithBackground(context, text, 5, 96, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
