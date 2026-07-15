package com.crolclient.client.gui.component;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Панель одной категории модулей в ClickGUI: заголовок + список кликабельных
 * строк-модулей внутри неё. Компонент чисто визуальный и делегирует обработку
 * клика обратно в ClickGuiScreen через возвращаемый Module по координатам.
 */
public class CategoryPanel {

    public final ModuleCategory category;
    public int x;
    public int y;
    public int width = 140;
    private static final int HEADER_HEIGHT = 16;
    private static final int ROW_HEIGHT = 14;

    private List<Module> modules;

    public CategoryPanel(ModuleCategory category, int x, int y) {
        this.category = category;
        this.x = x;
        this.y = y;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public int getHeight() {
        return HEADER_HEIGHT + (modules == null ? 0 : modules.size() * ROW_HEIGHT) + 4;
    }

    public void render(DrawContext context, Theme theme, int mouseX, int mouseY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        int height = getHeight();

        // фон панели
        context.fill(x, y, x + width, y + height, theme.backgroundColorArgb());
        // заголовок категории — акцентный цвет темы
        context.fill(x, y, x + width, y + HEADER_HEIGHT, blend(theme.accentColorArgb(), 0x66000000));
        context.drawText(mc.textRenderer, Text.literal(category.getDisplayName()), x + 4, y + 4, 0xFFFFFFFF, true);

        if (modules == null) return;

        int rowY = y + HEADER_HEIGHT + 2;
        for (Module module : modules) {
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT;
            int rowColor = module.isEnabled() ? theme.accentColorArgb() : (hovered ? 0x40FFFFFF : 0x00000000);
            context.fill(x + 2, rowY, x + width - 2, rowY + ROW_HEIGHT - 1, rowColor);

            int textColor = module.isEnabled() ? 0xFFFFFFFF : 0xFFBBBBBB;
            context.drawText(mc.textRenderer, Text.literal(module.getName()), x + 6, rowY + 3, textColor, false);
            rowY += ROW_HEIGHT;
        }
    }

    /** Возвращает модуль, по строке которого кликнули, либо null, если клик мимо. */
    public Module getModuleAt(int mouseX, int mouseY) {
        if (modules == null) return null;
        if (mouseX < x || mouseX > x + width) return null;
        int rowY = y + HEADER_HEIGHT + 2;
        for (Module module : modules) {
            if (mouseY >= rowY && mouseY <= rowY + ROW_HEIGHT) {
                return module;
            }
            rowY += ROW_HEIGHT;
        }
        return null;
    }

    private static int blend(int argb1, int argb2) {
        int a = ((argb1 >> 24) & 0xFF);
        int r = ((argb1 >> 16) & 0xFF);
        int g = ((argb1 >> 8) & 0xFF);
        int b = (argb1 & 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
