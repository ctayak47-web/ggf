package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/**
 * Отображает точный процент/число прочности предметов брони и хотбара
 * текстом поверх иконки — информация, уже доступная через тултип предмета,
 * просто выводится сразу на экран без наведения.
 */
public class DurabilityViewerModule extends Module {
    private final BooleanSetting showPercentSetting = new BooleanSetting("Показывать проценты", true);

    public DurabilityViewerModule() {
        super("DurabilityViewer", ModuleCategory.VISUAL, "Показывает числовую прочность брони и предметов хотбара текстом.");
        addSetting(showPercentSetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        int slotSize = 20;
        int startX = mc.getWindow().getScaledWidth() / 2 - (9 * slotSize) / 2;
        int y = mc.getWindow().getScaledHeight() - 19;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !stack.isDamageable()) continue;
            int maxDamage = stack.getMaxDamage();
            int damage = stack.getDamage();
            int remaining = maxDamage - damage;
            String text = showPercentSetting.get()
                    ? (int) ((remaining / (double) maxDamage) * 100) + "%"
                    : remaining + "/" + maxDamage;
            int x = startX + i * slotSize + 2;
            context.drawText(mc.textRenderer, Text.literal(text), x, y - 10, 0xFFFFFFFF, true);
        }
    }
}
