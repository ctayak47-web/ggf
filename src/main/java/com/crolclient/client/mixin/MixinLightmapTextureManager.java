package com.crolclient.client.mixin;

import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.module.impl.visual.FullBrightModule;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Точка расширения для FullBright на случай, если потребуется более
 * агрессивная коррекция карты освещения помимо стандартного gamma-override
 * (см. FullBrightModule, который на данный момент использует штатный
 * GameOptions#getGamma — это осознанный выбор в пользу "честного" метода,
 * идентичного игровому ползунку яркости, без прямого патчинга текстуры
 * карты освещения).
 */
@Mixin(LightmapTextureManager.class)
public abstract class MixinLightmapTextureManager {
    // Пусто намеренно: FullBright реализован через GameOptions (см. класс модуля),
    // а не через патчинг текстуры lightmap, чтобы избежать искажения теней/цвета
    // сверх стандартного игрового поведения максимальной яркости.
}
