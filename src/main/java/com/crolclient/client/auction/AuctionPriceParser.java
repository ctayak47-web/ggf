package com.crolclient.client.auction;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Извлекает числовую цену лота из lore или названия ItemStack.
 * <p>
 * Поддерживает распространённые на серверах-аукционах форматы записи цены:
 *   "Цена: 1500"
 *   "Price: 1,500"
 *   "Цена: 1.5k" / "1.5K" / "2m" / "2M" (суффиксы тысяч/миллионов)
 *   "§a1000$" / "1000 монет" / "1000 coins" / "1000 руб"
 * <p>
 * Так как на разных серверах формат текста лора отличается, парсер не
 * привязан к конкретному серверу — он ищет ЛЮБУЮ строку, где есть ключевое
 * слово цены (цена/price/cost/стоимость/coins/монет/$/руб) рядом с числом,
 * и если явного ключевого слова нет — использует первое найденное число с
 * суффиксом валюты как запасной вариант.
 */
public final class AuctionPriceParser {

    private AuctionPriceParser() {
    }

    // Ключевые слова, обозначающие цену, в разных вариантах написания серверов.
    private static final Pattern PRICE_KEYWORD_LINE = Pattern.compile(
            "(?i)(цена|price|cost|стоимость|стоит)\\s*[:\\-]?\\s*([0-9.,]+)\\s*([kkKмKmMмлн]*)");

    // Число с суффиксом валюты без явного ключевого слова: "1500$", "2.5k coins", "1000 монет"
    private static final Pattern PRICE_SUFFIX_LINE = Pattern.compile(
            "(?i)([0-9][0-9.,]*)\\s*(k|к|m|м|млн)?\\s*(\\$|монет|coins|coin|руб|₽)");

    /**
     * Пытается извлечь цену из lore предмета. Возвращает null, если ни одна
     * строка лора не похожа на строку с ценой — такие лоты сканер пропускает,
     * не засоряя статистику некорректными нулями.
     */
    public static Double extractPrice(ItemStack stack) {
        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                Double price = parseLine(line.getString());
                if (price != null) return price;
            }
        }
        // запасной путь — иногда цена зашита прямо в название предмета
        Text name = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (name != null) {
            Double price = parseLine(name.getString());
            if (price != null) return price;
        }
        return null;
    }

    private static Double parseLine(String raw) {
        if (raw == null || raw.isBlank()) return null;
        // убираем секции форматирования (§x) на случай, если строка попала как raw-текст
        String cleaned = raw.replaceAll("§.", "");

        Matcher keywordMatcher = PRICE_KEYWORD_LINE.matcher(cleaned);
        if (keywordMatcher.find()) {
            return applyMultiplier(keywordMatcher.group(2), keywordMatcher.group(3));
        }

        Matcher suffixMatcher = PRICE_SUFFIX_LINE.matcher(cleaned);
        if (suffixMatcher.find()) {
            return applyMultiplier(suffixMatcher.group(1), suffixMatcher.group(2));
        }

        return null;
    }

    private static Double applyMultiplier(String numberPart, String suffix) {
        if (numberPart == null) return null;
        String normalized = numberPart.replace(",", "").trim();
        double base;
        try {
            base = Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            return null;
        }
        if (suffix == null) return base;
        String s = suffix.toLowerCase();
        if (s.startsWith("k") || s.startsWith("к")) {
            return base * 1_000;
        }
        if (s.startsWith("m") || s.startsWith("м")) {
            return base * 1_000_000;
        }
        return base;
    }
}
