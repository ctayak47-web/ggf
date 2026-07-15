package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.ModeSetting;
import com.crolclient.client.module.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;

/** Рисует кастомный прицел вместо стандартного крестика — чисто косметическая замена. */
public class CustomCrosshairModule extends Module {

    private final ModeSetting styleSetting = new ModeSetting("Стиль", "Крест", "Крест", "Точка", "Круг");
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFFFFFFFF);
    private final NumberSetting sizeSetting = new NumberSetting("Размер", 5.0, 2.0, 12.0, 1.0);

    public CustomCrosshairModule() {
        super("CustomCrosshair", ModuleCategory.VISUAL, "Заменяет стандартный прицел на кастомный (форма/цвет/размер).");
        addSetting(styleSetting);
        addSetting(colorSetting);
        addSetting(sizeSetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        int cx = mc.getWindow().getScaledWidth() / 2;
        int cy = mc.getWindow().getScaledHeight() / 2;
        int size = (int) sizeSetting.get();
        int color = colorSetting.get();

        switch (styleSetting.getValue()) {
            case "Точка" -> context.fill(cx - 1, cy - 1, cx + 1, cy + 1, color);
            case "Круг" -> drawCircleOutline(context, cx, cy, size, color);
            default -> {
                context.fill(cx - size, cy, cx + size, cy + 1, color);
                context.fill(cx, cy - size, cx + 1, cy + size, color);
            }
        }
    }

    private void drawCircleOutline(DrawContext context, int cx, int cy, int radius, int color) {
        int segments = 24;
        for (int i = 0; i < segments; i++) {
            double angle = (2 * Math.PI * i) / segments;
            int px = cx + (int) (Math.cos(angle) * radius);
            int py = cy + (int) (Math.sin(angle) * radius);
            context.fill(px, py, px + 1, py + 1, color);
        }
    }
}
