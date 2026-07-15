package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.theme.ThemeManager;
import net.minecraft.client.gui.DrawContext;

/**
 * Полоска-индикатор восстановления силы атаки (attack cooldown) — то же самое
 * значение, что и ванильная полоска над хотбаром (F3+пустой хотбар обычно
 * скрывает её), просто вынесенное отдельным настраиваемым HUD-элементом.
 */
public class CooldownsHudModule extends Module {

    private final NumberSetting xSetting = new NumberSetting("Позиция X", 5, 0, 1000, 1);
    private final NumberSetting ySetting = new NumberSetting("Позиция Y", 150, 0, 1000, 1);
    private final NumberSetting widthSetting = new NumberSetting("Ширина полоски", 80, 20, 200, 1);

    public CooldownsHudModule() {
        super("CooldownsHud", ModuleCategory.HUD, "Полоска восстановления силы атаки.");
        addSetting(xSetting);
        addSetting(ySetting);
        addSetting(widthSetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;

        float progress = mc.player.getAttackCooldownProgress(0.0f);
        var theme = ThemeManager.getInstance().getCurrentTheme();
        int x = (int) xSetting.get();
        int y = (int) ySetting.get();
        int width = (int) widthSetting.get();
        int height = 4;

        context.fill(x, y, x + width, y + height, theme.backgroundColorArgb());
        int filled = (int) (width * progress);
        int barColor = progress >= 1.0f ? theme.accentColorArgb() : 0xFFAAAAAA;
        context.fill(x, y, x + filled, y + height, barColor);
    }
}
