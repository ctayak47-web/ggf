package com.crolclient.client;

import com.crolclient.client.auction.AuctionPriceDatabase;
import com.crolclient.client.command.CrolCommand;
import com.crolclient.client.config.ConfigManager;
import com.crolclient.client.gui.ClickGuiScreen;
import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.waypoint.WaypointManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import com.crolclient.client.module.impl.utility.ItemScrollerModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * Точка входа клиентской части CrolClient.
 * <p>
 * Инициализация проходит в следующем порядке:
 *   1. ModuleManager — регистрирует все штатные модули (см. registerDefaultModules).
 *   2. ThemeManager / ConfigManager — читаются лениво при первом обращении,
 *      но конфиг явно загружается здесь (ConfigManager#load), восстанавливая
 *      состояние модулей и выбранную тему из прошлой сессии.
 *   3. Регистрируются обработчики тика, HUD-рендера и клавиатуры.
 *   4. Регистрируется команда /crol.
 * <p>
 * Чтобы добавить новый модуль — см. инструкцию в ModuleManager.java.
 * Чтобы добавить новую тему — см. инструкцию в ThemeManager.java.
 */
public class CrolClient implements ClientModInitializer {

    /** Клавиша открытия ClickGUI — Right Shift, как указано в требованиях. */
    private static final int OPEN_GUI_KEY = GLFW.GLFW_KEY_RIGHT_SHIFT;

    @Override
    public void onInitializeClient() {
        System.out.println("[CrolClient] Инициализация визуального клиента...");

        // 1. Модули регистрируются при первом обращении к ModuleManager (ленивая инициализация синглтона).
        ModuleManager.getInstance();

        // 2. Загружаем сохранённое состояние модулей/темы из прошлой сессии.
        ConfigManager.getInstance().load();

        // 3. Обработка тиков — прогоняем onTick() всех включённых модулей каждый игровой тик.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            ModuleManager.getInstance().tickAll();
            handleOpenGuiKey(client);
            handleModuleHotkeys(client);
        });

        // 4. Рендер HUD — прогоняем onRender() всех включённых модулей каждый кадр интерфейса.
        HudRenderCallback.EVENT.register((context, tickDelta) ->
                ModuleManager.getInstance().renderAll(context, tickDelta.getTickProgress(false)));

        // 4.1. Рендер 3D-мира (после отрисовки сущностей) — прогоняем onRenderWorld() всех
        //      включённых модулей. Используется чисто визуальными эффектами (TargetEsp, Trails,
        //      Halo, HitBubble, ChinaHat, Nimb), которые рисуются в мировых координатах.
        WorldRenderEvents.AFTER_ENTITIES.register(context ->
                ModuleManager.getInstance().renderWorldAll(context));

        // 5. Автосохранение конфигурации и истории цен аукциона при закрытии любого экрана
        //    (в том числе при выходе из игры в главное меню) — подстраховка на случай,
        //    если пользователь не закрывал ClickGUI явно перед выходом.
        // 5.1. Также здесь вешаем на каждый открывающийся экран инвентаря обработчик
        //      прокрутки колёсика мыши над слотом — используется модулем ItemScroller
        //      для быстрого перемещения стака без Shift+ЛКМ.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof net.minecraft.client.gui.screen.ingame.HandledScreen<?> handledScreen) {
                ScreenMouseEvents.allowMouseScroll(handledScreen).register((s, mouseX, mouseY, horizontalAmount, verticalAmount) -> {
                    ItemScrollerModule module = (ItemScrollerModule) ModuleManager.getInstance().getByName("ItemScroller");
                    if (module != null && module.isEnabled()) {
                        return module.handleScroll(handledScreen, mouseX, mouseY, verticalAmount);
                    }
                    return true;
                });

                // 5.2. Превью содержимого шалкер-бокса при наведении (ShulkerPreview).
                ScreenEvents.afterRender(handledScreen).register((s, context, mouseX, mouseY, tickDelta2) -> {
                    com.crolclient.client.module.impl.visual.ShulkerPreviewModule preview =
                            (com.crolclient.client.module.impl.visual.ShulkerPreviewModule)
                                    ModuleManager.getInstance().getByName("ShulkerPreview");
                    if (preview != null && preview.isEnabled()) {
                        preview.renderPreview(handledScreen, context, mouseX, mouseY);
                    }
                });
            }
        });

        // 6. Команда /crol и подкоманды.
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                CrolCommand.register(dispatcher));

        // 7. Сохраняем конфиг и историю цен аукциона при завершении работы клиента (закрытие игры).
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ConfigManager.getInstance().save();
            AuctionPriceDatabase.getInstance().save();
            WaypointManager.getInstance().save();
        }, "CrolClient-ShutdownSave"));

        System.out.println("[CrolClient] Инициализация завершена. Модулей зарегистрировано: "
                + ModuleManager.getInstance().getModules().size());
    }

    private boolean guiKeyWasDown = false;

    private void handleOpenGuiKey(MinecraftClient client) {
        boolean down = InputUtil.isKeyPressed(client.getWindow().getHandle(), OPEN_GUI_KEY);
        if (down && !guiKeyWasDown && client.currentScreen == null) {
            client.setScreen(new ClickGuiScreen());
        }
        guiKeyWasDown = down;
    }

    /** Отслеживает состояние клавиш каждого модуля, чтобы toggle срабатывал один раз на нажатие (без автоповтора). */
    private final java.util.Map<Integer, Boolean> hotkeyWasDown = new java.util.HashMap<>();

    private void handleModuleHotkeys(MinecraftClient client) {
        if (client.currentScreen != null) return; // хоткеи модулей не обрабатываются, пока открыт любой экран (в т.ч. ClickGUI)
        for (var module : ModuleManager.getInstance().getModules()) {
            int keyCode = module.getKeyCode();
            if (keyCode == InputUtil.UNKNOWN_KEY.getCode()) continue;
            boolean down = InputUtil.isKeyPressed(client.getWindow().getHandle(), keyCode);
            boolean wasDown = hotkeyWasDown.getOrDefault(keyCode, false);
            if (down && !wasDown) {
                // Модули с "мгновенным действием по нажатию" (а не переключением enabled)
                // обрабатываются отдельно — их хоткей не должен переключать флаг enabled.
                if (module instanceof com.crolclient.client.module.impl.utility.AutoEatModule autoEat) {
                    autoEat.onHotkeyPressed();
                } else if (module instanceof com.crolclient.client.module.impl.utility.CoordinateClipboardModule clipboard) {
                    clipboard.onHotkeyPressed();
                } else {
                    ModuleManager.getInstance().handleKeyPress(keyCode);
                }
            }
            hotkeyWasDown.put(keyCode, down);
        }
    }
}
