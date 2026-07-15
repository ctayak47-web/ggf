package com.crolclient.client.theme;

/**
 * Модель цветовой темы оформления ClickGUI, загружаемая из JSON
 * (assets/crolclient/themes/*.json). Поля соответствуют структуре JSON один в один.
 */
public class Theme {

    public String name = "default";
    /** Акцентный цвет (ARGB hex-строка, например "#3B82F6"). */
    public String accentColor = "#3B82F6";
    /** Цвет фона панелей (ARGB hex-строка). */
    public String backgroundColor = "#1A1A1ACC";
    /** Радиус скругления углов панелей, в пикселях. */
    public float cornerRadius = 6.0f;
    /** Общая прозрачность UI, от 0.0 (прозрачно) до 1.0 (непрозрачно). */
    public float opacity = 0.85f;
    /** Цвет текста (ARGB hex-строка). */
    public String textColor = "#FFFFFFFF";

    public int accentColorArgb() {
        return parseHexColor(accentColor);
    }

    public int backgroundColorArgb() {
        return parseHexColor(backgroundColor);
    }

    public int textColorArgb() {
        return parseHexColor(textColor);
    }

    /**
     * Парсит hex-строку цвета формата "#RRGGBB" или "#RRGGBBAA" в ARGB int,
     * пригодный для передачи в DrawContext.fill()/drawText().
     */
    public static int parseHexColor(String hex) {
        if (hex == null || hex.isEmpty()) return 0xFFFFFFFF;
        String cleaned = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            if (cleaned.length() == 6) {
                // RRGGBB -> добавляем полную непрозрачность
                long rgb = Long.parseLong(cleaned, 16);
                return (int) (0xFF000000L | rgb);
            } else if (cleaned.length() == 8) {
                // RRGGBBAA -> переставляем в ARGB
                long rgba = Long.parseLong(cleaned, 16);
                long r = (rgba >> 24) & 0xFF;
                long g = (rgba >> 16) & 0xFF;
                long b = (rgba >> 8) & 0xFF;
                long a = rgba & 0xFF;
                return (int) ((a << 24) | (r << 16) | (g << 8) | b);
            }
        } catch (NumberFormatException e) {
            // при некорректном формате отдаём безопасный дефолт
        }
        return 0xFFFFFFFF;
    }
}
