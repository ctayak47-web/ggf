package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ModeSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Показывает мини-иконки надетой брони в углу экрана (аналог F3+инвентаря, но всегда на виду). */
public class CustomArmorHudIconsModule extends Module {
    private final ModeSetting positionSetting = new ModeSetting("Позиция", "Слева снизу", "Слева снизу", "Справа снизу", "Слева сверху");

    public CustomArmorHudIconsModule() {
        super("CustomArmorHudIcons", ModuleCategory.VISUAL, "Показывает иконки надетой брони в углу экрана.");
        addSetting(positionSetting);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null) return;
        int x = 5;
        int y = mc.getWindow().getScaledHeight() - 40;
        int i = 0;
        for (ItemStack stack : mc.player.getArmorItems()) {
            if (stack.isEmpty()) continue;
            context.drawItem(stack, x, y - i * 20);
            i++;
        }
    }
}
