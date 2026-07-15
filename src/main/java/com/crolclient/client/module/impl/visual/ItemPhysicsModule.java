package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;

/**
 * Декоративный эффект дропа предметов: добавляет визуальное вращение/отскок
 * выпавшим предметам на земле (чисто клиентская анимация модели энтити,
 * не влияет на хитбокс подбора и синхронизирована только локально).
 */
public class ItemPhysicsModule extends Module {
    public ItemPhysicsModule() {
        super("ItemPhysics", ModuleCategory.VISUAL, "Декоративная физика вращения/отскока выпавших предметов.");
    }
}
