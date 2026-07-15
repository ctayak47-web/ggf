package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/**
 * Оценивает приблизительный TPS сервера на основе интервала между
 * игровыми тиками, получаемыми клиентом (информационная эвристика,
 * аналогичная тем, что используют публичные HUD-моды; точность зависит
 * от сервера и не является официальным протокольным значением).
 */
public class TpsDisplayModule extends Module {
    private long lastTickTime = System.currentTimeMillis();
    private double estimatedTps = 20.0;

    public TpsDisplayModule() {
        super("TpsDisplay", ModuleCategory.HUD, "Приблизительная оценка TPS сервера по интервалам тиков.");
    }

    @Override
    public void onTick() {
        long now = System.currentTimeMillis();
        long delta = now - lastTickTime;
        lastTickTime = now;
        if (delta > 0) {
            double instantTps = Math.min(20.0, 1000.0 / delta);
            estimatedTps = estimatedTps * 0.9 + instantTps * 0.1; // сглаживание
        }
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, String.format("TPS: ~%.1f", estimatedTps), 5, 83, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
