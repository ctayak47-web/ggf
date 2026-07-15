package com.crolclient.client.auction;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.Map;

/**
 * Рисует цветной оверлей (рамку + подсказку цены) поверх слотов открытого
 * GUI-контейнера аукциона на основе последнего результата AuctionScanner.
 * Вызывается из миксина/колбэка отрисовки экрана инвентаря
 * (см. AuctionHelperModule#onScreenDrawn).
 * <p>
 * Чисто информационный слой поверх уже открытого игроком экрана —
 * не совершает покупок и не взаимодействует с сервером самостоятельно.
 */
public final class AuctionOverlayRenderer {

    private AuctionOverlayRenderer() {
    }

    private static final int GOOD_DEAL_COLOR = 0x8022C55E; // полупрозрачный зелёный
    private static final int MILD_DEAL_COLOR = 0x60EAB308;  // полупрозрачный жёлтый

    public static void render(DrawContext context, HandledScreen<?> screen, int screenX, int screenY) {
        MinecraftClient mc = MinecraftClient.getInstance();
        Map<Integer, AuctionScanner.LotEvaluation> results = AuctionScanner.getInstance().getLastResult();
        if (results.isEmpty()) return;

        for (Slot slot : screen.getScreenHandler().slots) {
            AuctionScanner.LotEvaluation eval = results.get(slot.id);
            if (eval == null) continue;

            int x = screenX + slot.x;
            int y = screenY + slot.y;

            if (eval.isGoodDeal()) {
                context.fill(x, y, x + 16, y + 16, GOOD_DEAL_COLOR);
                drawTooltipHint(context, mc, x, y, eval, true);
            } else if (eval.discountRatio > 0.05) {
                context.fill(x, y, x + 16, y + 16, MILD_DEAL_COLOR);
            }
        }
    }

    private static void drawTooltipHint(DrawContext context, MinecraftClient mc, int x, int y, AuctionScanner.LotEvaluation eval, boolean goodDeal) {
        if (mc.currentScreen == null) return;
        // Небольшая зелёная точка-индикатор в углу слота — компактная подсказка
        // без перекрытия иконки предмета; полная информация — в hoveredTooltip (см. модуль).
        context.fill(x + 12, y, x + 16, y + 4, 0xFF22C55E);
    }

    /** Формирует текст подсказки (для добавления в тултип наведения) с деталями сравнения цены. */
    public static Text buildTooltipLine(AuctionScanner.LotEvaluation eval) {
        if (eval.medianForItem == null) {
            return Text.literal("§7CrolClient: нет истории цен для сравнения");
        }
        String percent = String.format("%.0f%%", eval.discountRatio * 100);
        if (eval.isGoodDeal()) {
            return Text.literal("§a✔ Выгодно: на " + percent + " дешевле медианы (§7мед. " + String.format("%.0f", eval.medianForItem) + "§a)");
        } else if (eval.discountRatio > 0) {
            return Text.literal("§eНемного дешевле медианы (" + percent + ")");
        } else {
            return Text.literal("§7Цена не ниже медианы (§7мед. " + String.format("%.0f", eval.medianForItem) + "§7)");
        }
    }
}
