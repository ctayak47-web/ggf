package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;

import java.util.LinkedList;

/** Рисует график изменения FPS за последние ~60 кадров в углу экрана. */
public class FpsGraphModule extends Module {
    private final LinkedList<Integer> history = new LinkedList<>();
    private static final int MAX_POINTS = 60;

    public FpsGraphModule() {
        super("FpsGraph", ModuleCategory.HUD, "График изменения FPS за последние кадры.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        history.addLast(mc.getCurrentFps());
        while (history.size() > MAX_POINTS) history.removeFirst();

        int x = mc.getWindow().getScaledWidth() - 130;
        int y = mc.getWindow().getScaledHeight() - 50;
        int w = 120, h = 40;
        var theme = ThemeManager.getInstance().getCurrentTheme();
        context.fill(x, y, x + w, y + h, theme.backgroundColorArgb());

        int maxFps = Math.max(1, history.stream().mapToInt(Integer::intValue).max().orElse(60));
        int i = 0;
        int prevX = -1, prevY = -1;
        for (int fps : history) {
            int px = x + (int) ((double) i / MAX_POINTS * w);
            int py = y + h - (int) ((double) fps / maxFps * h);
            if (prevX != -1) {
                context.fill(prevX, prevY, px + 1, py + 1, theme.accentColorArgb());
            }
            prevX = px;
            prevY = py;
            i++;
        }
    }
}
