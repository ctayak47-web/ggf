package com.crolclient.client.module;

import com.crolclient.client.module.impl.hud.*;
import com.crolclient.client.module.impl.utility.*;
import com.crolclient.client.module.impl.visual.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Центральный реестр всех модулей CrolClient.
 * <p>
 * Чтобы добавить новый модуль в клиент:
 *   1. Реализуйте класс модуля (см. Module.java — инструкция там же).
 *   2. Добавьте одну строку "register(new ВашМодуль());" в registerDefaultModules().
 * Больше никаких изменений не требуется — модуль автоматически появится
 * в ClickGUI (в своей категории), будет сохраняться в конфиг и обрабатывать
 * свой хоткей.
 */
public class ModuleManager {

    private static ModuleManager instance;

    private final List<Module> modules = new ArrayList<>();
    private final Map<String, Module> byName = new LinkedHashMap<>();

    public static ModuleManager getInstance() {
        if (instance == null) {
            instance = new ModuleManager();
        }
        return instance;
    }

    private ModuleManager() {
        registerDefaultModules();
    }

    private void register(Module module) {
        modules.add(module);
        byName.put(module.getName().toLowerCase(), module);
    }

    /**
     * Регистрация всех штатных модулей клиента.
     * Список сгруппирован по категориям для читаемости.
     */
    private void registerDefaultModules() {
        // ===================== VISUAL =====================
        register(new FullBrightModule());
        register(new ZoomModule());
        register(new FreeLookModule());
        register(new CustomNametagsModule());
        register(new CustomCrosshairModule());
        register(new AmbientOcclusionToggleModule());
        register(new CustomSkyModule());
        register(new MotionBlurModule());
        register(new ChromaUiModule());
        register(new CustomHandViewModule());
        register(new DurabilityViewerModule());
        register(new BetterFoliageModule());
        register(new CustomParticlesDensityModule());
        register(new CinematicCameraModule());
        register(new CustomDeathScreenModule());
        register(new CustomLoadingScreenModule());
        register(new BlockOutlineCustomizerModule());
        register(new CustomPotionHudModule());
        register(new CustomBossBarModule());
        register(new CustomXpBarModule());
        register(new ChatCustomizerModule());
        register(new TabListCustomizerModule());
        register(new CustomScoreboardModule());
        register(new ItemPhysicsModule());
        register(new WeatherCustomizerModule());
        register(new CustomHotbarModule());
        register(new CustomArmorHudIconsModule());
        register(new CapeTrailModule());
        register(new PlayerHeadMarkerModule());
        register(new JumpParticleCircleModule());
        register(new TargetEspModule());
        register(new TrailsModule());
        register(new HaloModule());
        register(new HitBubbleModule());
        register(new ChinaHatModule());
        register(new NimbModule());
        register(new ShulkerPreviewModule());

        // ===================== HUD =====================
        register(new CoordinatesHudModule());
        register(new FpsHudModule());
        register(new BiomeHudModule());
        register(new ClockHudModule());
        register(new SessionTimerHudModule());
        register(new CompassHudModule());
        register(new WaypointHudModule());
        register(new NotificationsModule());
        register(new PingDisplayModule());
        register(new TpsDisplayModule());
        register(new FpsGraphModule());
        register(new SessionStatsOverlayModule());
        register(new LightLevelOverlayModule());
        register(new ChunkBoundaryModule());
        register(new TargetHudModule());
        register(new CooldownsHudModule());
        register(new ArmorHudModule());
        register(new WatermarkModule());

        // ===================== UTILITY =====================
        register(new AutoEatModule());
        register(new AntiCactusModule());
        register(new ItemNotifyModule());
        register(new DeathCoordinateLogModule());
        register(new AutoReconnectModule());
        register(new ChatFilterModule());
        register(new ScreenshotOrganizerModule());
        register(new DiscordRichPresenceModule());
        register(new InventoryValueEstimatorModule());
        register(new CoordinateClipboardModule());
        register(new CustomF3PanelModule());
        register(new ChatMacrosModule());
        register(new ServerPingCheckerModule());
        register(new ConfigProfilesModule());
        register(new FunTimeAuctionHelperModule());
        register(new ItemScrollerModule());
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(ModuleCategory category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .sorted(Comparator.comparing(Module::getName))
                .collect(Collectors.toList());
    }

    /** Поиск модулей по подстроке в имени/описании — используется в ClickGUI и /crol search. */
    public List<Module> search(String query) {
        String q = query.toLowerCase().trim();
        if (q.isEmpty()) return getModules();
        return modules.stream()
                .filter(m -> m.getName().toLowerCase().contains(q)
                        || m.getDescription().toLowerCase().contains(q)
                        || m.getCategory().getDisplayName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public Module getByName(String name) {
        return byName.get(name.toLowerCase());
    }

    /** Обрабатывает нажатие клавиши — переключает модуль, привязанный к данному keyCode. */
    public void handleKeyPress(int keyCode) {
        if (keyCode == net.minecraft.client.util.InputUtil.UNKNOWN_KEY.getCode()) return;
        for (Module module : modules) {
            if (module.getKeyCode() == keyCode) {
                module.toggle();
            }
        }
    }

    /** Вызывается каждый тик — прогоняет onTick() всех включённых модулей. */
    public void tickAll() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onTick();
            }
        }
    }

    /** Вызывается каждый кадр рендера HUD — прогоняет onRender() всех включённых модулей. */
    public void renderAll(net.minecraft.client.gui.DrawContext context, float tickDelta) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender(context, tickDelta);
            }
        }
    }

    /** Вызывается каждый кадр рендера 3D-мира — прогоняет onRenderWorld() всех включённых модулей. */
    public void renderWorldAll(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRenderWorld(context);
            }
        }
    }
}
