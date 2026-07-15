package com.crolclient.client.module;

import com.crolclient.client.module.setting.Setting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Базовый абстрактный класс для всех модулей CrolClient.
 * <p>
 * Чтобы добавить новый модуль:
 * 1. Создайте класс в com.crolclient.client.module.impl.<категория>,
 *    унаследованный от Module.
 * 2. В конструкторе вызовите super(имя, категория, описание, keyCode).
 * 3. Переопределите onEnable()/onDisable() для логики включения/выключения.
 * 4. При необходимости переопределите onRender()/onTick() для покадровой
 *    или потиковой логики.
 * 5. Зарегистрируйте модуль в ModuleManager (метод registerDefaultModules).
 * <p>
 * Модуль сам не занимается рендером напрямую в игровой мир для функций,
 * дающих нечестное преимущество (например, ESP/hitbox) — такие модули
 * в CrolClient отсутствуют по архитектурному ограничению проекта.
 */
public abstract class Module {

    private final String name;
    private final ModuleCategory category;
    private final String description;
    private int keyCode;
    private boolean enabled;

    /** Настройки модуля (слайдеры, чекбоксы, цвета) — отображаются в ClickGUI. */
    protected final List<Setting<?>> settings = new ArrayList<>();

    protected final MinecraftClient mc = MinecraftClient.getInstance();

    protected Module(String name, ModuleCategory category, String description, int keyCode) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.keyCode = keyCode;
        this.enabled = false;
    }

    protected Module(String name, ModuleCategory category, String description) {
        this(name, category, description, InputUtil.UNKNOWN_KEY.getCode());
    }

    /** Переключает состояние модуля и вызывает соответствующий колбэк. */
    public final void toggle() {
        setEnabled(!enabled);
    }

    public final void setEnabled(boolean value) {
        if (this.enabled == value) return;
        this.enabled = value;
        if (value) {
            onEnable();
        } else {
            onDisable();
        }
    }

    /** Вызывается один раз при включении модуля. Переопределяется по необходимости. */
    protected void onEnable() {
    }

    /** Вызывается один раз при выключении модуля. Переопределяется по необходимости. */
    protected void onDisable() {
    }

    /** Вызывается каждый игровой тик, пока модуль включён. */
    public void onTick() {
    }

    /** Вызывается каждый кадр рендера HUD, пока модуль включён. */
    public void onRender(net.minecraft.client.gui.DrawContext context, float tickDelta) {
    }

    /**
     * Вызывается каждый кадр рендера 3D-мира (после отрисовки сущностей), пока модуль
     * включён. Используется чисто визуальными эффектами, которые рисуются "в мире"
     * (обводки, следы, декоративные модели над головой и т.п.), а не поверх экрана.
     * Матрица {@code context.matrixStack()} уже готова для мировых координат —
     * см. {@link com.crolclient.client.util.WorldRenderUtil} для готовых примитивов.
     */
    public void onRenderWorld(net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext context) {
    }

    public String getName() {
        return name;
    }

    public ModuleCategory getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

    /** Отправляет клиентское уведомление игроку через систему Toast/чат. Утилита для наследников. */
    protected void chat(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(net.minecraft.text.Text.literal("§b[CrolClient] §f" + message), false);
        }
    }
}
