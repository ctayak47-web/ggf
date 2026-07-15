package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;

/** Показывает текущий пинг до сервера. */
public class PingDisplayModule extends Module {
    public PingDisplayModule() {
        super("PingDisplay", ModuleCategory.HUD, "Показывает текущий пинг до сервера.");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        int ping = entry != null ? entry.getLatency() : -1;
        var theme = ThemeManager.getInstance().getCurrentTheme();
        RenderUtil.drawTextWithBackground(context, "Пинг: " + (ping >= 0 ? ping + "ms" : "-"), 5, 70, theme.textColorArgb(), theme.backgroundColorArgb());
    }
}
