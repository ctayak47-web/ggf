package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

/**
 * Флаг-модуль "свободный обзор камеры" — служит переключателем состояния,
 * которое читает MixinGameRenderer при рендере камеры, отделяя направление
 * взгляда камеры от направления модели персонажа (косметическая функция
 * камеры, не меняет реальное направление игрока/удар/коллизии).
 */
public class FreeLookModule extends Module {
    public FreeLookModule() {
        super("FreeLook", ModuleCategory.VISUAL, "Позволяет вращать камеру независимо от направления персонажа (только визуально).");
    }

    /** Читается миксином рендера камеры для решения — применять ли независимый обзор. */
    public static boolean isActive() {
        Module m = com.crolclient.client.module.ModuleManager.getInstance().getByName("FreeLook");
        return m != null && m.isEnabled();
    }
}
