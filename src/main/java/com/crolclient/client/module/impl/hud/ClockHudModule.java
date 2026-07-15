package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/** Отображает реальное (системное) время часов в углу экрана. */
public class ClockHudModule extends Module {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public ClockHudModule() {
        super("ClockHUD", ModuleCategory.HUD, "Показывает текущее реальное время.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        String text = LocalTime.now().format(FORMAT);
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, text, 5, 44, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
