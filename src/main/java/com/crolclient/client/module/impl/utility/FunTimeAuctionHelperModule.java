package com.crolclient.client.module.impl.utility;

import com.crolclient.client.auction.AuctionOverlayRenderer;
import com.crolclient.client.auction.AuctionScanner;
import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;
import com.crolclient.client.module.setting.NumberSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

/**
 * FunTime Auction Helper — информационный сканер лотов аукциона.
 * <p>
 * Логика работы:
 *   1. Каждый раз, когда открыт GUI-контейнер (сундук/шалкер/торговая площадка
 *      сервера), и прошло не менее rescanIntervalTicks с последнего скана —
 *      модуль вызывает AuctionScanner.scanOpenContainer(), который асинхронно
 *      парсит лоты и обновляет статистику (см. AuctionScanner).
 *   2. При каждом кадре, пока открыт HandledScreen, модуль вызывает
 *      AuctionOverlayRenderer.render(...) поверх экрана инвентаря, подсвечивая
 *      выгодные лоты цветным индикатором на основе последнего результата скана.
 * <p>
 * Модуль НЕ покупает предметы автоматически и не взаимодействует с сервером
 * от имени игрока — вся автоматизация ограничена анализом уже отображённой
 * информации и визуальной подсказкой.
 */
public class FunTimeAuctionHelperModule extends Module {

    private final BooleanSetting autoRescanSetting = new BooleanSetting("Автосканирование при открытии", true);
    private final NumberSetting rescanIntervalSetting = new NumberSetting("Интервал пересканирования (тики)", 40.0, 10.0, 200.0, 10.0);

    private int ticksSinceLastScan = Integer.MAX_VALUE;
    private boolean containerWasOpen = false;

    public FunTimeAuctionHelperModule() {
        super("FunTimeAuctionHelper", ModuleCategory.UTILITY,
                "Сканирует лоты открытого GUI-аукциона, асинхронно считает медианную цену по истории и подсвечивает выгодные лоты. Покупки не совершает.");
        addSetting(autoRescanSetting);
        addSetting(rescanIntervalSetting);
    }

    @Override
    protected void onDisable() {
        AuctionScanner.getInstance().clearResult();
    }

    @Override
    public void onTick() {
        boolean containerOpen = mc.currentScreen instanceof GenericContainerScreen;
        ticksSinceLastScan++;

        if (containerOpen && !containerWasOpen) {
            // контейнер только что открыт — сканируем сразу
            triggerScan();
        } else if (containerOpen && autoRescanSetting.get() && ticksSinceLastScan >= rescanIntervalSetting.get()) {
            // периодическое пересканирование на случай обновления содержимого (пролистывание страниц лотов)
            triggerScan();
        } else if (!containerOpen && containerWasOpen) {
            AuctionScanner.getInstance().clearResult();
        }

        containerWasOpen = containerOpen;
    }

    private void triggerScan() {
        AuctionScanner.getInstance().scanOpenContainer();
        ticksSinceLastScan = 0;
    }

    /**
     * Вызывается из миксина рендера экрана инвентаря (MixinHandledScreen — при
     * необходимости добавляется отдельно) сразу после отрисовки слотов, чтобы
     * оверлей ложился поверх обычного фона, но под тултипами наведения.
     */
    public void renderOverlay(DrawContext context, HandledScreen<?> screen, int screenX, int screenY) {
        if (!isEnabled()) return;
        AuctionOverlayRenderer.render(context, screen, screenX, screenY);
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        // Небольшой индикатор состояния сканирования в углу экрана, пока открыт контейнер.
        if (!(mc.currentScreen instanceof GenericContainerScreen)) return;
        String status = AuctionScanner.getInstance().isScanInProgress() ? "Сканирование..." : "Готово";
        context.drawText(mc.textRenderer, "CrolClient AuctionHelper: " + status, 4, mc.getWindow().getScaledHeight() - 12, 0xFFAAAAAA, true);
    }
}
