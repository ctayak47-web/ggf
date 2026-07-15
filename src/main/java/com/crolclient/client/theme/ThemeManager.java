package com.crolclient.client.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Загружает цветовые темы оформления ClickGUI из JSON-ресурсов мода
 * (assets/crolclient/themes/*.json) через штатный ResourceManager Minecraft
 * (что позволяет темам переопределяться ресурспаками/датапаками, если это
 * когда-либо понадобится) и предоставляет доступ к текущей выбранной теме.
 * <p>
 * Чтобы добавить новую тему:
 *   1. Создайте файл assets/crolclient/themes/имя.json со структурой Theme.
 *   2. Добавьте имя файла (без расширения) в KNOWN_THEME_IDS ниже.
 * Тема автоматически появится в списке выбора темы в ClickGUI.
 */
public class ThemeManager {

    private static ThemeManager instance;

    /** Список встроенных ID тем — соответствуют именам файлов в assets/crolclient/themes/. */
    private static final String[] KNOWN_THEME_IDS = {"default", "ocean", "sunset"};

    private final Gson gson = new GsonBuilder().setLenient().create();
    private final Map<String, Theme> themes = new LinkedHashMap<>();
    private Theme currentTheme;

    public static ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    private ThemeManager() {
        loadAllThemes();
    }

    private void loadAllThemes() {
        for (String id : KNOWN_THEME_IDS) {
            Theme theme = loadThemeFromResource(id);
            if (theme == null) {
                theme = fallbackTheme(id);
            }
            themes.put(id, theme);
        }
        currentTheme = themes.getOrDefault("default", fallbackTheme("default"));
    }

    private Theme loadThemeFromResource(String id) {
        Identifier resourceId = Identifier.of("crolclient", "themes/" + id + ".json");
        MinecraftClient client = MinecraftClient.getInstance();
        try {
            // На этапе первичной инициализации resourceManager может быть ещё не полностью
            // готов (например, при загрузке до старта клиента) — на этот случай есть
            // резервная загрузка напрямую с диска мода ниже.
            if (client != null && client.getResourceManager() != null) {
                Optional<Resource> resourceOpt = client.getResourceManager().getResource(resourceId);
                if (resourceOpt.isPresent()) {
                    try (Reader reader = new InputStreamReader(resourceOpt.get().getInputStream(), StandardCharsets.UTF_8)) {
                        return gson.fromJson(reader, Theme.class);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            // Резервный путь — читаем напрямую из classpath мода.
        }
        return loadThemeFromClasspath(id);
    }

    private Theme loadThemeFromClasspath(String id) {
        String path = "assets/crolclient/themes/" + id + ".json";
        try (var stream = ThemeManager.class.getClassLoader().getResourceAsStream(path)) {
            if (stream == null) return null;
            try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
                return gson.fromJson(reader, Theme.class);
            }
        } catch (IOException e) {
            return null;
        }
    }

    private Theme fallbackTheme(String id) {
        Theme theme = new Theme();
        theme.name = id;
        return theme;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(String id) {
        Theme theme = themes.get(id);
        if (theme != null) {
            currentTheme = theme;
        }
    }

    public Map<String, Theme> getThemes() {
        return themes;
    }

    public String getCurrentThemeId() {
        for (Map.Entry<String, Theme> entry : themes.entrySet()) {
            if (entry.getValue() == currentTheme) return entry.getKey();
        }
        return "default";
    }
}
