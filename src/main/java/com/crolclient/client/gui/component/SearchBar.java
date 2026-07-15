package com.crolclient.client.gui.component;

import com.crolclient.client.theme.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

/** Простое текстовое поле поиска модулей вверху ClickGUI. */
public class SearchBar {

    public int x;
    public int y;
    public int width;
    public int height = 16;

    private final StringBuilder query = new StringBuilder();
    private boolean focused = false;

    public SearchBar(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
    }

    public void render(DrawContext context, Theme theme) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int borderColor = focused ? theme.accentColorArgb() : 0xFF555555;
        context.fill(x, y, x + width, y + height, 0xFF1A1A1A);
        com.crolclient.client.util.RenderUtil.drawBorder(context, x, y, width, height, 1, borderColor);

        String display = query.isEmpty() ? "Поиск модулей..." : query.toString();
        int color = query.isEmpty() ? 0xFF777777 : 0xFFFFFFFF;
        context.drawText(mc.textRenderer, Text.literal(display), x + 4, y + 4, color, false);
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }

    public void appendChar(char c) {
        if (query.length() < 32) {
            query.append(c);
        }
    }

    public void backspace() {
        if (!query.isEmpty()) {
            query.deleteCharAt(query.length() - 1);
        }
    }

    public String getQuery() {
        return query.toString();
    }

    public void clear() {
        query.setLength(0);
    }
}
