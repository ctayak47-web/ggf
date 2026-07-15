package com.crolclient.client.auction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Потокобезопасное хранилище истории цен лотов, накопленной сканером
 * FunTime Auction Helper. Обновляется из фонового потока (CompletableFuture),
 * поэтому внутренние коллекции синхронизированы через ConcurrentHashMap и
 * блокировку по списку на каждый предмет.
 * <p>
 * История сохраняется на диск (config/crolclient/auction_history.json),
 * чтобы медиана/среднее оставались осмысленными между игровыми сессиями,
 * а не начинали каждый раз с нуля.
 */
public class AuctionPriceDatabase {

    private static AuctionPriceDatabase instance;

    /** Идентификатор предмета -> список цен за единицу, зафиксированных сканером. */
    private final Map<String, List<Double>> priceHistory = new ConcurrentHashMap<>();

    /** Максимум записей истории на один предмет — старые записи вытесняются, чтобы не разрастаться бесконечно. */
    private static final int MAX_HISTORY_PER_ITEM = 500;

    private final Gson gson = new GsonBuilder().create();
    private final Path file;

    public static AuctionPriceDatabase getInstance() {
        if (instance == null) {
            instance = new AuctionPriceDatabase();
        }
        return instance;
    }

    private AuctionPriceDatabase() {
        this.file = FabricLoader.getInstance().getConfigDir().resolve("crolclient").resolve("auction_history.json");
        load();
    }

    private String keyOf(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        return id.toString();
    }

    /** Добавляет новую точку цены за единицу в историю указанного предмета. Потокобезопасно. */
    public void recordPrice(Item item, double pricePerUnit) {
        if (pricePerUnit <= 0 || Double.isNaN(pricePerUnit) || Double.isInfinite(pricePerUnit)) return;
        String key = keyOf(item);
        List<Double> list = priceHistory.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()));
        synchronized (list) {
            list.add(pricePerUnit);
            while (list.size() > MAX_HISTORY_PER_ITEM) {
                list.remove(0); // вытесняем самые старые записи
            }
        }
    }

    /** Медианная цена за единицу предмета по накопленной истории, либо null, если данных ещё нет. */
    public Double getMedianPrice(Item item) {
        List<Double> list = priceHistory.get(keyOf(item));
        if (list == null || list.isEmpty()) return null;
        List<Double> sorted;
        synchronized (list) {
            sorted = new ArrayList<>(list);
        }
        Collections.sort(sorted);
        int size = sorted.size();
        if (size % 2 == 1) {
            return sorted.get(size / 2);
        } else {
            return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
        }
    }

    /** Средняя (арифметическая) цена за единицу предмета по накопленной истории. */
    public Double getAveragePrice(Item item) {
        List<Double> list = priceHistory.get(keyOf(item));
        if (list == null || list.isEmpty()) return null;
        double sum;
        int count;
        synchronized (list) {
            sum = list.stream().mapToDouble(Double::doubleValue).sum();
            count = list.size();
        }
        return count > 0 ? sum / count : null;
    }

    public int getSampleCount(Item item) {
        List<Double> list = priceHistory.get(keyOf(item));
        return list == null ? 0 : list.size();
    }

    /** Сохраняет всю накопленную историю цен на диск. Вызывается по завершении сканирования и при выходе. */
    public synchronized void save() {
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
                gson.toJson(priceHistory, writer);
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось сохранить историю цен аукциона: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        if (!Files.exists(file)) return;
        try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, List<Double>>>() {}.getType();
            Map<String, List<Double>> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                for (Map.Entry<String, List<Double>> entry : loaded.entrySet()) {
                    priceHistory.put(entry.getKey(), Collections.synchronizedList(new ArrayList<>(entry.getValue())));
                }
            }
        } catch (IOException e) {
            System.err.println("[CrolClient] Не удалось загрузить историю цен аукциона: " + e.getMessage());
        }
    }
}
