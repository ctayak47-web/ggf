package com.crolclient.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Общие утилиты рендера, переиспользуемые HUD- и визуальными модулями. */
public final class RenderUtil {

    private RenderUtil() {
    }

    /** Рисует текст с полупрозрачной подложкой — общий стиль всех HUD-элементов CrolClient. */
    public static void drawTextWithBackground(DrawContext context, String text, int x, int y, int textColor, int bgColor) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int width = mc.textRenderer.getWidth(text);
        context.fill(x - 2, y - 2, x + width + 2, y + mc.textRenderer.fontHeight + 2, bgColor);
        context.drawText(mc.textRenderer, Text.literal(text), x, y, textColor, true);
    }

    /** Рисует прямоугольную рамку заданной толщины. */
    public static void drawBorder(DrawContext context, int x, int y, int w, int h, int thickness, int color) {
        context.fill(x, y, x + w, y + thickness, color); // верх
        context.fill(x, y + h - thickness, x + w, y + h, color); // низ
        context.fill(x, y, x + thickness, y + h, color); // лево
        context.fill(x + w - thickness, y, x + w, y + h, color); // право
    }

    public static String formatDuration(long millis) {
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }
}
