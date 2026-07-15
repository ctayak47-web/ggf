package com.crolclient.client.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Accessor-миксин, открывающий доступ к protected-полям x/y/backgroundWidth/backgroundHeight
 * ванильного HandledScreen — они хранят точные координаты верхнего левого угла
 * панели инвентаря и её размер, необходимые AuctionOverlayRenderer для
 * правильного позиционирования подсветки слотов.
 */
@Mixin(HandledScreen.class)
public interface AccessorHandledScreen {
    @Accessor("x")
    int crolclient$getX();

    @Accessor("y")
    int crolclient$getY();

    @Accessor("backgroundWidth")
    int crolclient$getBackgroundWidth();

    @Accessor("backgroundHeight")
    int crolclient$getBackgroundHeight();
}
