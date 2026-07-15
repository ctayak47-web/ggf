package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

/**
 * Панель с информацией о сущности, на которую сейчас наведён прицел
 * (имя, здоровье, дистанция). Источник данных — {@code mc.targetedEntity},
 * то же самое поле, которое игра использует для взаимодействия (ПКМ/атака),
 * то есть модуль не даёт информацию о том, что и так не видно на экране.
 */
public class TargetHudModule extends Module {

    private final NumberSetting xSetting = new NumberSetting("Позиция X", 5, 0, 1000, 1);
    private final NumberSetting ySetting = new NumberSetting("Позиция Y", 100, 0, 1000, 1);

    public TargetHudModule() {
        super("TargetHud", ModuleCategory.HUD, "Показывает имя/HP/дистанцию до цели под прицелом.");
        addSetting(xSetting);
        addSetting(ySetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        Entity target = mc.targetedEntity;
        if (!(target instanceof LivingEntity living)) return;

        var theme = ThemeManager.getInstance().getCurrentTheme();
        int x = (int) xSetting.get();
        int y = (int) ySetting.get();

        String name = target instanceof PlayerEntity ? target.getName().getString() : target.getType().getName().getString();
        double distance = mc.player.distanceTo(target);
        String healthLine = String.format("HP: %.1f / %.1f", living.getHealth(), living.getMaxHealth());
        String distanceLine = String.format("Дистанция: %.1fм", distance);

        RenderUtil.drawTextWithBackground(context, "Цель: " + name, x, y, theme.textColorArgb(), theme.backgroundColorArgb());
        RenderUtil.drawTextWithBackground(context, healthLine, x, y + 12, theme.textColorArgb(), theme.backgroundColorArgb());
        RenderUtil.drawTextWithBackground(context, distanceLine, x, y + 24, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
