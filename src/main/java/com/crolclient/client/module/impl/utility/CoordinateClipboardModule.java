package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import org.lwjgl.glfw.GLFW;

/** По хоткею копирует текущие координаты игрока в системный буфер обмена. */
public class CoordinateClipboardModule extends Module {
    public CoordinateClipboardModule() {
        super("CoordinateClipboard", ModuleCategory.UTILITY, "Копирует текущие координаты в буфер обмена по хоткею.", GLFW.GLFW_KEY_K);
    }

    public void onHotkeyPressed() {
        if (mc.player == null) return;
        String coords = String.format("%.1f, %.1f, %.1f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        mc.keyboard.setClipboard(coords);
        ToastManager.getInstance().push("Coordinates", "Скопировано: " + coords, Toast.Type.SUCCESS);
    }
}
