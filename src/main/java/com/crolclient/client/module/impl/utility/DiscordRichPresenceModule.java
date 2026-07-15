package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

/**
 * Заглушка-интеграция с Discord Rich Presence: хранит и предоставляет
 * текущий статус (например, "Играет на <сервер>") для внешней библиотеки
 * IPC-соединения с Discord, которую пользователь может подключить отдельно
 * (полноценный IPC-клиент требует нативной библиотеки, не входящей в
 * стандартный набор Fabric-зависимостей).
 */
public class DiscordRichPresenceModule extends Module {
    private String currentStatus = "В главном меню";

    public DiscordRichPresenceModule() {
        super("DiscordRichPresence", ModuleCategory.UTILITY, "Показывает статус игры в Discord (требует внешнего IPC-моста).");
    }

    @Override
    public void onTick() {
        if (mc.player != null && mc.world != null) {
            currentStatus = "Играет: X=" + (int) mc.player.getX() + " Z=" + (int) mc.player.getZ();
        } else {
            currentStatus = "В главном меню";
        }
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}
