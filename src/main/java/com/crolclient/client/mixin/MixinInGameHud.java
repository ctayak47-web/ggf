package com.crolclient.client.mixin;

import com.crolclient.client.module.ModuleManager;
import com.crolclient.client.module.impl.utility.FunTimeAuctionHelperModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Встраивает рендер оверлея FunTime Auction Helper поверх любого открытого
 * HandledScreen (сундук/шалкер/торговая площадка сервера) сразу после
 * отрисовки фона слотов, но до отрисовки тултипа наведения — благодаря
 * этому подсветка не перекрывает всплывающую подсказку предмета.
 * <p>
 * Точные координаты панели инвентаря берутся через AccessorHandledScreen
 * (accessor-миксин к полям x/y ванильного класса) — это надёжнее, чем
 * вычислять их вручную по формуле центрирования, так как разные типы
 * контейнеров (одиночный сундук/двойной/шалкер) имеют разную высоту.
 */
@Mixin(HandledScreen.class)
public abstract class MixinInGameHud {

    @Inject(method = "drawBackground", at = @At("TAIL"))
    private void crolclient$onDrawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY, CallbackInfo ci) {
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        AccessorHandledScreen accessor = (AccessorHandledScreen) self;

        var module = ModuleManager.getInstance().getByName("FunTimeAuctionHelper");
        if (module instanceof FunTimeAuctionHelperModule auctionModule && auctionModule.isEnabled()) {
            auctionModule.renderOverlay(context, self, accessor.crolclient$getX(), accessor.crolclient$getY());
        }
    }
}
