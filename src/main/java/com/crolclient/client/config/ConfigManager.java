package com.crolclient.client.config;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.module.setting.*;
import com.crolclient.client.theme.ThemeManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Сохраняет и загружает конфигурацию CrolClient (состояние модулей,
 * значения настроек, выбранную тему, хоткеи) в JSON-файл через Gson.
 * <p>
 * Файл конфигурации: .minecraft/config/crolclient/config.json
 * Автозагрузка происходит один раз при старте клиента (см. CrolClient#onInitializeClient).
 * Автосохранение вызывается при закрытии ClickGUI и при выходе из игры.
 */
public class ConfigManager {

    private static ConfigManager instance;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Path configDir;
    private final Path configFile;

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    private ConfigManager() {
        this.configDir = FabricLoader.getInstance().getConfigDir().resolve("crolclient");
        this.configFile = configDir.resolve("config.json");
    }

    /** Структура, которая сериализуется в JSON. */
    private static class ConfigData {
        String theme = "default";
        Map<String, ModuleData> modules = new LinkedHashMap<>();
    }

    private static class ModuleData {
        boolean enabled;
        int keyCode = -1;
        Map<String, Object> settings = new HashMap<>();
    }

    /** Сохраняет текущее состояние всех модулей и выбранной темы в JSON. */
    public void save() {
        try {
            Files.createDirectories(configDir);

            ConfigData data = new ConfigData();
            data.theme = ThemeManager.getInstance().getCurrentThemeId();

            for (Module module : ModuleManager.getInstance().getModules()) {
                ModuleData md = new ModuleData();
                md.enabled = module.isEnabled();
                md.keyCode = module.getKeyCode();
                for (Setting<?> setting : module.getSettings()) {
                    md.settings.put(setting.getName(), setting.getValue());
                }
                data.modules.put(module.getName(), md);
            }

            try (Writer writer = Files.newBufferedWriter(configFile, StandardCharsets.UTF_8)) {
                gson.toJson(data, writer);
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось сохранить конфиг: " + e.getMessage());
        }
    }

    /** Загружает состояние модулей и тему из JSON. Если файла нет — использует значения по умолчанию. */
    @SuppressWarnings("unchecked")
    public void load() {
        if (!Files.exists(configFile)) {
            return; // первый запуск — все модули со значениями по умолчанию из конструкторов
        }
        try (Reader reader = Files.newBufferedReader(configFile, StandardCharsets.UTF_8)) {
            ConfigData data = gson.fromJson(reader, ConfigData.class);
            if (data == null) return;

            if (data.theme != null) {
                ThemeManager.getInstance().setCurrentTheme(data.theme);
            }

            if (data.modules != null) {
                for (Map.Entry<String, ModuleData> entry : data.modules.entrySet()) {
                    Module module = ModuleManager.getInstance().getByName(entry.getKey());
                    if (module == null) continue;
                    ModuleData md = entry.getValue();

                    if (md.keyCode != -1) {
                        module.setKeyCode(md.keyCode);
                    }

                    // Восстанавливаем значения настроек по имени, приводя типы вручную,
                    // так как Gson десериализует числа как Double по умолчанию.
                    for (Setting<?> setting : module.getSettings()) {
                        Object raw = md.settings.get(setting.getName());
                        if (raw == null) continue;
                        applyRawValue(setting, raw);
                    }

                    if (md.enabled) {
                        module.setEnabled(true);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось загрузить конфиг: " + e.getMessage());
        }
    }

    private void applyRawValue(Setting<?> setting, Object raw) {
        if (setting instanceof BooleanSetting bs && raw instanceof Boolean b) {
            bs.setValue(b);
        } else if (setting instanceof NumberSetting ns && raw instanceof Double d) {
            ns.setClamped(d);
        } else if (setting instanceof ColorSetting cs && raw instanceof Double d) {
            cs.setValue(d.intValue());
        } else if (setting instanceof ModeSetting ms && raw instanceof String s) {
            ms.setValue(s);
        }
    }
}
