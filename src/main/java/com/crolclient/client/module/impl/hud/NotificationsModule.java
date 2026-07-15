package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

/** Рендерит очередь Toast-уведомлений (система оповещений CrolClient) в правом верхнем углу. */
public class NotificationsModule extends Module {
    public NotificationsModule() {
        super("Notifications", ModuleCategory.HUD, "Отображает всплывающие уведомления клиента (Toast).");
    }

    @Override
    public void onTick() {
        ToastManager.getInstance().tick();
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        List<Toast> toasts = ToastManager.getInstance().getActiveToasts();
        int width = mc.getWindow().getScaledWidth();
        int y = 30;
        for (Toast toast : toasts) {
            int x = width - 155;
            float alpha = 1.0f - Math.max(0, toast.progress() - 0.8f) * 5f;
            int alphaInt = (int) (alpha * 255) & 0xFF;
            int bg = (alphaInt << 24) | 0x1A1A1A;
            int titleColor = (0xFF << 24) | (toast.type.color >> 8);

            context.fill(x, y, x + 150, y + 28, bg);
            context.fill(x, y, x + 3, y + 28, toast.type.color);
            context.drawText(mc.textRenderer, Text.literal(toast.title), x + 8, y + 4, titleColor, false);
            context.drawText(mc.textRenderer, Text.literal(toast.message), x + 8, y + 15, 0xFFAAAAAA, false);
            y += 32;
        }
    }
}
