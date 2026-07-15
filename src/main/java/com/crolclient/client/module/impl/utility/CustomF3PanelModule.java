package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;

/** Компактная замена части F3-панели, оформленная под выбранную тему клиента. */
public class CustomF3PanelModule extends Module {
    public CustomF3PanelModule() {
        super("CustomF3Panel", ModuleCategory.UTILITY, "Компактная информационная панель (аналог части F3), оформленная под тему.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;
        var theme = ThemeManager.getInstance().getCurrentTheme();
        int x = mc.getWindow().getScaledWidth() - 140;
        int y = 5;
        String[] lines = {
                "CrolClient F3",
                String.format("XYZ: %.1f %.1f %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ()),
                "Измерение: " + mc.world.getRegistryKey().getValue().getPath(),
                "FPS: " + mc.getCurrentFps()
        };
        int i = 0;
        for (String line : lines) {
            RenderUtil.drawTextWithBackground(context, line, x, y + i * 12, theme.textColorArgb(), theme.backgroundColorArgb());
            i++;
        }
    }
}
