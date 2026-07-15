package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

/**
 * Переподключается к серверу при обрыве сетевого соединения (дисконнект по
 * таймауту/потере пакетов). НЕ обходит бан и не переподключается, если
 * игрок был явно кикнут администрацией или забанен — переподключение
 * срабатывает только на разрывы уровня сети (см. onDisconnect в CrolClient).
 */
public class AutoReconnectModule extends Module {
    private final NumberSetting delaySeconds = new NumberSetting("Задержка (сек)", 5.0, 1.0, 30.0, 1.0);
    private ServerInfo lastServer;

    public AutoReconnectModule() {
        super("AutoReconnect", ModuleCategory.UTILITY, "Переподключается при разрыве сети (не для обхода банов/киков).");
        addSetting(delaySeconds);
    }

    public void rememberServer(ServerInfo serverInfo) {
        this.lastServer = serverInfo;
    }

    /** Вызывается из обработчика дисконнекта только для сетевых разрывов, не для кика/бана. */
    public void attemptReconnect() {
        if (!isEnabled() || lastServer == null) return;
        ToastManager.getInstance().push("AutoReconnect", "Переподключение через " + (int) delaySeconds.get() + " сек...", Toast.Type.INFO);

        new Thread(() -> {
            try {
                Thread.sleep((long) (delaySeconds.get() * 1000));
            } catch (InterruptedException ignored) {
                return;
            }
            mc.execute(() -> {
                if (mc.currentScreen == null) return;
                ServerAddress address = ServerAddress.parse(lastServer.address);
                ConnectScreen.connect(new MultiplayerScreen(net.minecraft.client.gui.screen.Screen.EMPTY), mc, address, lastServer, false, null);
            });
        }, "CrolClient-AutoReconnect").start();
    }
}
