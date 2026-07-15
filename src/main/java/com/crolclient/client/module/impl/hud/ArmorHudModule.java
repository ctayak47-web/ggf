package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Показывает надетую броню (4 слота) вертикальным столбиком иконок с
 * числовым остатком прочности рядом с каждой — быстрая проверка "не пора
 * ли чинить броню", без открытия инвентаря.
 */
public class ArmorHudModule extends Module {

    private final NumberSetting xSetting = new NumberSetting("Позиция X", 5, 0, 1000, 1);
    private final NumberSetting ySetting = new NumberSetting("Позиция Y", 200, 0, 1000, 1);

    private static final EquipmentSlot[] SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    public ArmorHudModule() {
        super("ArmorHud", ModuleCategory.HUD, "Иконки надетой брони с остатком прочности.");
        addSetting(xSetting);
        addSetting(ySetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        int x = (int) xSetting.get();
        int y = (int) ySetting.get();
        int rowHeight = 18;

        for (int i = 0; i < SLOTS.length; i++) {
            ItemStack stack = mc.player.getEquippedStack(SLOTS[i]);
            if (stack.isEmpty()) continue;

            int slotY = y + i * rowHeight;
            context.drawItem(stack, x, slotY);
            context.drawItemInSlot(mc.textRenderer, stack, x, slotY);

            if (stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int remaining = max - stack.getDamage();
                float pct = max > 0 ? (float) remaining / max : 1f;
                int color = pct > 0.5f ? 0xFF4ADE80 : (pct > 0.2f ? 0xFFFACC15 : 0xFFF87171);
                context.drawText(mc.textRenderer, Text.literal(remaining + "/" + max), x + 20, slotY + 4, color, true);
            }
        }
    }
}
