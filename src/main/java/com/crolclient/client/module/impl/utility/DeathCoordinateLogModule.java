package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Записывает координаты игрока в текстовый лог-файл при каждой смерти. */
public class DeathCoordinateLogModule extends Module {
    private final Path logFile = FabricLoader.getInstance().getConfigDir().resolve("crolclient").resolve("death_log.txt");
    private double lastHealth = 20;

    public DeathCoordinateLogModule() {
        super("DeathCoordinateLog", ModuleCategory.UTILITY, "Записывает координаты игрока в файл при каждой смерти.");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        double health = mc.player.getHealth();
        if (lastHealth > 0 && health <= 0) {
            logDeath();
        }
        lastHealth = health;
    }

    private void logDeath() {
        if (mc.player == null) return;
        String line = String.format("[%s] X=%.1f Y=%.1f Z=%.1f%n",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                mc.player.getX(), mc.player.getY(), mc.player.getZ());
        try {
            Files.createDirectories(logFile.getParent());
            Files.writeString(logFile, line, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            ToastManager.getInstance().push("DeathLog", "Координаты смерти сохранены", Toast.Type.INFO);
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось записать death_log: " + e.getMessage());
        }
    }
}
