package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

/** Отображает текущий биом игрока. */
public class BiomeHudModule extends Module {
    public BiomeHudModule() {
        super("BiomeHUD", ModuleCategory.HUD, "Показывает биом, в котором сейчас находится игрок.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.world == null) return;
        BlockPos pos = mc.player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = mc.world.getBiome(pos);
        String biomeName = biomeEntry.getKey()
                .map(RegistryKey::getValue)
                .map(id -> id.getPath().replace('_', ' '))
                .orElse("неизвестно");
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, "Биом: " + biomeName, 5, 31, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
