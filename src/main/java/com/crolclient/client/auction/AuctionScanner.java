package com.crolclient.client.auction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Основной сканер лотов аукциона. Работает в паре с открытым GenericContainerScreenHandler
 * (или любым другим ScreenHandler с индексируемыми слотами контейнера).
 * <p>
 * Архитектура:
 *   1. captureSnapshot() — синхронно (в рендер/тик потоке) снимает лёгкий
 *      снимок текущих ItemStack всех слотов контейнера (сам ItemStack.copy()
 *      дешёвый, тяжёлая логика — сравнение/парсинг/статистика — не выполняется
 *      на этом шаге).
 *   2. scanAsync() — передаёт снимок в пул фоновых потоков через
 *      CompletableFuture.supplyAsync(...), где выполняется:
 *        - извлечение цены из lore (AuctionPriceParser)
 *        - запись цены за единицу в AuctionPriceDatabase (историческая статистика)
 *        - сравнение цены лота с уже известной медианой по этому предмету
 *      Рендер-поток НЕ блокируется — результат прилетает через колбэк
 *      thenAcceptAsync(..., mc.execute(...)), который безопасно применяет
 *      результат обратно в основном потоке игры (Minecraft не терпит
 *      обращений к игровым объектам из чужого потока).
 *   3. Результат сканирования — список подсвечиваемых слотов с их
 *      "выгодностью" — используется AuctionOverlayRenderer для отрисовки
 *      цветных рамок поверх слотов инвентаря.
 */
public class AuctionScanner {

    private static AuctionScanner instance;

    /** Отдельный пул потоков для сканирования — не мешает общему ForkJoinPool игры/других модов. */
    private final Executor scannerExecutor = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "CrolClient-AuctionScanner");
        t.setDaemon(true);
        return t;
    });

    /** Последний вычисленный результат сканирования: слот -> оценка выгодности. */
    private final Map<Integer, LotEvaluation> lastResult = new ConcurrentHashMap<>();

    private volatile boolean scanInProgress = false;

    public static AuctionScanner getInstance() {
        if (instance == null) {
            instance = new AuctionScanner();
        }
        return instance;
    }

    /** Оценка одного лота после сканирования — используется рендером оверлея. */
    public static class LotEvaluation {
        public final AuctionLot lot;
        public final Double medianForItem;
        /** Насколько лот дешевле медианы, в долях (0.3 = на 30% дешевле среднего). Может быть отрицательным (дороже медианы). */
        public final double discountRatio;

        LotEvaluation(AuctionLot lot, Double medianForItem, double discountRatio) {
            this.lot = lot;
            this.medianForItem = medianForItem;
            this.discountRatio = discountRatio;
        }

        /** Лот считается выгодным, если он минимум на 25% дешевле медианной цены и есть достаточно данных истории. */
        public boolean isGoodDeal() {
            return medianForItem != null && discountRatio >= 0.25;
        }
    }

    /**
     * Снимает лёгкий снимок содержимого открытого контейнера (вызывается
     * из основного потока — обращение к слотам ScreenHandler безопасно
     * только там) и запускает асинхронную обработку.
     */
    public void scanOpenContainer() {
        if (scanInProgress) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.currentScreenHandler == null) return;

        List<SlotSnapshot> snapshot = captureSnapshot(mc);
        if (snapshot.isEmpty()) return;

        scanInProgress = true;
        scanAsync(snapshot).whenComplete((result, throwable) -> {
            scanInProgress = false;
            if (throwable != null) {
                System.err.println("[CrolClient] Ошибка асинхронного сканирования аукциона: " + throwable.getMessage());
            }
        });
    }

    /** Простая неизменяемая копия данных слота, безопасная для передачи в другой поток. */
    private record SlotSnapshot(int slotIndex, ItemStack stackCopy) {
    }

    private List<SlotSnapshot> captureSnapshot(MinecraftClient mc) {
        List<SlotSnapshot> result = new ArrayList<>();
        // Сканируем только слоты самого контейнера (не инвентарь игрока, который
        // обычно идёт следом в том же ScreenHandler) — используем isPlayerSlot как отсечку.
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            if (isPlayerInventorySlot(slot)) continue;
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            result.add(new SlotSnapshot(slot.id, stack.copy()));
        }
        return result;
    }

    /**
     * Эвристика отделения слотов инвентаря игрока от слотов самого контейнера
     * аукциона: инвентарь игрока — это Slot с inventory-классом PlayerInventory.
     */
    private boolean isPlayerInventorySlot(Slot slot) {
        return slot.inventory instanceof net.minecraft.entity.player.PlayerInventory;
    }

    /**
     * Асинхронная обработка снимка: парсинг цены каждого лота, запись в
     * историю базы цен и расчёт отклонения от медианы. Выполняется целиком
     * в фоновом потоке (scannerExecutor), не трогая рендер/тик поток игры,
     * кроме финального безопасного применения результата через mc.execute().
     */
    private CompletableFuture<Void> scanAsync(List<SlotSnapshot> snapshot) {
        return CompletableFuture.supplyAsync(() -> {
            List<LotEvaluation> evaluations = new ArrayList<>();
            AuctionPriceDatabase db = AuctionPriceDatabase.getInstance();

            for (SlotSnapshot s : snapshot) {
                ItemStack stack = s.stackCopy();
                Double price = AuctionPriceParser.extractPrice(stack);
                if (price == null) continue;

                String displayName = stack.getName().getString();
                AuctionLot lot = new AuctionLot(s.slotIndex(), stack.getItem(), displayName, stack.getCount(), price);

                // Считаем медиану ДО записи текущего лота, чтобы не искажать
                // сравнение собственным значением (сравниваем с историей ДО этого лота).
                Double medianBefore = db.getMedianPrice(lot.item);

                double discount = 0.0;
                if (medianBefore != null && medianBefore > 0) {
                    double perUnit = lot.pricePerUnit();
                    discount = (medianBefore - perUnit) / medianBefore;
                }

                evaluations.add(new LotEvaluation(lot, medianBefore, discount));

                // Пополняем историю уже после расчёта — следующий лот того же типа
                // в этом же скане увидит обновлённую медиану (реалистичное накопление).
                db.recordPrice(lot.item, lot.pricePerUnit());
            }

            return evaluations;
        }, scannerExecutor).thenAcceptAsync(evaluations -> {
            // Применяем результат в основном потоке игры — безопасно для рендера.
            lastResult.clear();
            for (LotEvaluation eval : evaluations) {
                lastResult.put(eval.lot.slotIndex, eval);
            }
            AuctionPriceDatabase.getInstance().save();
        }, MinecraftClient.getInstance()::execute);
    }

    public Map<Integer, LotEvaluation> getLastResult() {
        return Collections.unmodifiableMap(lastResult);
    }

    public boolean isScanInProgress() {
        return scanInProgress;
    }

    public void clearResult() {
        lastResult.clear();
    }
}
