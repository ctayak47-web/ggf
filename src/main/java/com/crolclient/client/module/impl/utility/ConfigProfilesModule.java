package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Позволяет переключаться между несколькими сохранёнными наборами
 * настроек (профилями), копируя файл конфигурации под именем профиля.
 */
public class ConfigProfilesModule extends Module {
    private final Path profilesDir = FabricLoader.getInstance().getConfigDir().resolve("crolclient").resolve("profiles");

    public ConfigProfilesModule() {
        super("ConfigProfiles", ModuleCategory.UTILITY, "Переключение между несколькими сохранёнными наборами настроек.");
    }

    public void saveProfile(String name) {
        try {
            Files.createDirectories(profilesDir);
            Path source = FabricLoader.getInstance().getConfigDir().resolve("crolclient").resolve("config.json");
            if (Files.exists(source)) {
                Files.copy(source, profilesDir.resolve(name + ".json"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось сохранить профиль: " + e.getMessage());
        }
    }

    public void loadProfile(String name) {
        try {
            Path profile = profilesDir.resolve(name + ".json");
            Path target = FabricLoader.getInstance().getConfigDir().resolve("crolclient").resolve("config.json");
            if (Files.exists(profile)) {
                Files.copy(profile, target, StandardCopyOption.REPLACE_EXISTING);
                com.crolclient.client.config.ConfigManager.getInstance().load();
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось загрузить профиль: " + e.getMessage());
        }
    }
}
