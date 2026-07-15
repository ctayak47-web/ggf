package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;

/** Проверяет доступность сервера (TCP-пинг) асинхронно, не блокируя игровой поток. */
public class ServerPingCheckerModule extends Module {
    public ServerPingCheckerModule() {
        super("ServerPingChecker", ModuleCategory.UTILITY, "Асинхронно проверяет доступность сервера по адресу.");
    }

    public CompletableFuture<Long> checkPing(String host, int port) {
        return CompletableFuture.supplyAsync(() -> {
            long start = System.currentTimeMillis();
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 3000);
                return System.currentTimeMillis() - start;
            } catch (IOException e) {
                return -1L;
            }
        });
    }
}
