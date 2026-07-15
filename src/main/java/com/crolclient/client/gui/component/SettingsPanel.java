package com.crolclient.client.gui.component;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.setting.*;
import com.crolclient.client.theme.Theme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Панель настроек выбранного модуля: рендерит чекбоксы, слайдеры, палитры
 * цветов и переключатели режимов, автоматически подбирая тип виджета по
 * классу Setting&lt;?&gt;. Клики обрабатываются через handleClick/handleDrag,
 * вызываемые из ClickGuiScreen.
 */
public class SettingsPanel {

    public int x;
    public int y;
    public int width = 160;
    private static final int ROW_HEIGHT = 20;

    private Module targetModule;

    public SettingsPanel(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setTargetModule(Module module) {
        this.targetModule = module;
    }

    public Module getTargetModule() {
        return targetModule;
    }

    public int getHeight() {
        if (targetModule == null) return 0;
        return 20 + targetModule.getSettings().size() * ROW_HEIGHT + 6;
    }

    public void render(DrawContext context, Theme theme, int mouseX, int mouseY) {
        if (targetModule == null) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        int height = getHeight();

        context.fill(x, y, x + width, y + height, theme.backgroundColorArgb());
        context.drawText(mc.textRenderer, Text.literal(targetModule.getName() + " — настройки"), x + 4, y + 4, theme.accentColorArgb(), true);

        int rowY = y + 18;
        for (Setting<?> setting : targetModule.getSettings()) {
            renderSetting(context, mc, setting, rowY, theme);
            rowY += ROW_HEIGHT;
        }
    }

    private void renderSetting(DrawContext context, MinecraftClient mc, Setting<?> setting, int rowY, Theme theme) {
        context.drawText(mc.textRenderer, Text.literal(setting.getName()), x + 4, rowY, 0xFFCCCCCC, false);

        if (setting instanceof BooleanSetting bs) {
            int boxColor = bs.get() ? theme.accentColorArgb() : 0xFF444444;
            context.fill(x + width - 18, rowY - 2, x + width - 4, rowY + 10, boxColor);
        } else if (setting instanceof NumberSetting ns) {
            int barX = x + 4;
            int barY = rowY + 10;
            int barW = width - 8;
            context.fill(barX, barY, barX + barW, barY + 3, 0xFF444444);
            double ratio = (ns.get() - ns.getMin()) / (ns.getMax() - ns.getMin());
            int filled = (int) (barW * Math.max(0, Math.min(1, ratio)));
            context.fill(barX, barY, barX + filled, barY + 3, theme.accentColorArgb());
            context.drawText(mc.textRenderer, Text.literal(String.format("%.2f", ns.get())), x + width - 40, rowY, 0xFFFFFFFF, false);
        } else if (setting instanceof ColorSetting cs) {
            context.fill(x + width - 20, rowY - 2, x + width - 4, rowY + 10, cs.get());
        } else if (setting instanceof ModeSetting ms) {
            context.drawText(mc.textRenderer, Text.literal(ms.getValue()), x + width - 60, rowY, theme.accentColorArgb(), false);
        }
    }

    /** Обрабатывает клик по настройке (чекбоксы, циклический переход режима). Слайдеры — через handleDrag. */
    public void handleClick(int mouseX, int mouseY) {
        if (targetModule == null) return;
        int rowY = y + 18;
        for (Setting<?> setting : targetModule.getSettings()) {
            boolean inRow = mouseY >= rowY - 2 && mouseY <= rowY + 12 && mouseX >= x && mouseX <= x + width;
            if (inRow) {
                if (setting instanceof BooleanSetting bs) {
                    bs.setValue(!bs.get());
                } else if (setting instanceof ModeSetting ms) {
                    ms.cycleNext();
                } else if (setting instanceof NumberSetting ns) {
                    applyDrag(ns, mouseX);
                }
                return;
            }
            rowY += ROW_HEIGHT;
        }
    }

    /** Обрабатывает перетаскивание слайдера (вызывается из onMouseDrag ClickGuiScreen). */
    public void handleDrag(int mouseX, int mouseY) {
        if (targetModule == null) return;
        int rowY = y + 18;
        for (Setting<?> setting : targetModule.getSettings()) {
            boolean inRow = mouseY >= rowY + 8 && mouseY <= rowY + 16;
            if (inRow && setting instanceof NumberSetting ns) {
                applyDrag(ns, mouseX);
                return;
            }
            rowY += ROW_HEIGHT;
        }
    }

    private void applyDrag(NumberSetting ns, int mouseX) {
        int barX = x + 4;
        int barW = width - 8;
        double ratio = (mouseX - barX) / (double) barW;
        ratio = Math.max(0, Math.min(1, ratio));
        double newValue = ns.getMin() + ratio * (ns.getMax() - ns.getMin());
        ns.setClamped(newValue);
    }
}
