package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Показывает таймер текущей игровой сессии (с момента входа в мир). */
public class SessionTimerHudModule extends Module {
    private long sessionStart;

    public SessionTimerHudModule() {
        super("SessionTimerHUD", ModuleCategory.HUD, "Показывает таймер текущей игровой сессии.");
    }

    @Override
    protected void onEnable() {
        sessionStart = System.currentTimeMillis();
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (sessionStart == 0) sessionStart = System.currentTimeMillis();
        long elapsed = System.currentTimeMillis() - sessionStart;
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, "Сессия: " + RenderUtil.formatDuration(elapsed), 5, 57, theme.textColorArgb(), theme.backgroundColorArgb());
    }

    public long getSessionMillis() {
        return sessionStart == 0 ? 0 : System.currentTimeMillis() - sessionStart;
    }
}
