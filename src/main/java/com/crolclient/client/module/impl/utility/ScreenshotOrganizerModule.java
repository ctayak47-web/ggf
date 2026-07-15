package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** Перемещает новые скриншоты в подпапки по дате и по имени текущего сервера. */
public class ScreenshotOrganizerModule extends Module {
    public ScreenshotOrganizerModule() {
        super("ScreenshotOrganizer", ModuleCategory.UTILITY, "Раскладывает скриншоты по папкам: дата / сервер.");
    }

    /** Вызывается после сохранения скриншота игрой (см. hook в CrolClient или ScreenshotRecorder callback). */
    public void organize(Path screenshotFile) {
        if (!isEnabled()) return;
        try {
            String serverName = currentServerFolderName();
            String dateFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            Path targetDir = screenshotFile.getParent().resolve(serverName).resolve(dateFolder);
            Files.createDirectories(targetDir);
            Path target = targetDir.resolve(screenshotFile.getFileName());
            Files.move(screenshotFile, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось организовать скриншот: " + e.getMessage());
        }
    }

    private String currentServerFolderName() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getCurrentServerEntry() != null) {
            return mc.getCurrentServerEntry().address.replaceAll("[^a-zA-Z0-9.-]", "_");
        }
        return "singleplayer";
    }
}
