package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

/**
 * Флаг-переключатель, читаемый рендер-миксином листвы: включает более
 * плотную/приятную визуальную отрисовку листвы (косметика уровня клиента,
 * аналог настроек графики травы/листвы в ванильных опциях).
 */
public class BetterFoliageModule extends Module {
    public BetterFoliageModule() {
        super("BetterFoliage", ModuleCategory.VISUAL, "Более насыщенная визуальная отрисовка листвы деревьев.");
    }
}
