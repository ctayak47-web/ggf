package com.crolclient.client.module.impl.hud;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.theme.ThemeManager;
import com.crolclient.client.util.RenderUtil;
import com.crolclient.client.waypoint.Waypoint;
import com.crolclient.client.waypoint.WaypointManager;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

/** Отображает список сохранённых точек (waypoints) текущего измерения с расстоянием и направлением. */
public class WaypointHudModule extends Module {
    public WaypointHudModule() {
        super("WaypointHUD", ModuleCategory.HUD, "Показывает расстояние и направление до сохранённых точек (waypoints).");
    }

    @Override
    public void onRender(DrawContext context, float tickDelta) {
        List<Waypoint> waypoints = WaypointManager.getInstance().getWaypointsForCurrentDimension();
        if (waypoints.isEmpty()) return;

        var theme = ThemeManager.getInstance().getCurrentTheme();
        int y = mc.getWindow().getScaledHeight() - 60;
        int x = mc.getWindow().getScaledWidth() - 150;

        for (Waypoint wp : waypoints) {
            double distance = WaypointManager.distanceFromPlayer(wp);
            String text = String.format("%s: %.0fm", wp.name, distance);
            int color = com.crolclient.client.theme.Theme.parseHexColor(wp.color);
            RenderUtil.drawTextWithBackground(context, text, x, y, color, theme.backgroundColorArgb());
            y -= 13;
        }
    }
}
