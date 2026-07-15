package com.crolclient.client.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Точка расширения для модулей, которым требуется реагировать на события
 * локального игрока (например, ItemPhysics при дропе предметов, или
 * AutoEat при использовании предмета). Основная логика этих модулей
 * реализована через ModuleManager#tickAll() на каждый тик — этот миксин
 * зарезервирован для более точечных хуков (конкретных методов сущности
 * игрока), если понадобится расширить функциональность без polling'а в тике.
 */
@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {
    // Пусто намеренно — см. комментарий класса.
}
