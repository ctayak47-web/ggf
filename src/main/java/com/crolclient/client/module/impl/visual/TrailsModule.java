package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.ModeSetting;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.util.WorldRenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Оставляет декоративный след из отрезков позади движущихся игроков —
 * чисто косметический эффект, не влияет на движение/коллизии.
 */
public class TrailsModule extends Module {

    private final ModeSetting targetModeSetting = new ModeSetting("Кому рисовать", "Все игроки", "Все игроки", "Только себе");
    private final NumberSetting lengthSetting = new NumberSetting("Длина следа", 40, 5, 200, 1);
    private final ColorSetting colorSetting = new ColorSetting("Цвет следа", 0xFF60A5FA);

    private final Map<UUID, Deque<Vec3d>> trails = new HashMap<>();

    public TrailsModule() {
        super("Trails", ModuleCategory.VISUAL, "Декоративный след из линий позади движущихся игроков.");
        addSetting(targetModeSetting);
        addSetting(lengthSetting);
        addSetting(colorSetting);
    }

    @Override
    protected void onDisable() {
        trails.clear();
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;
        int maxLen = (int) lengthSetting.get();
        boolean onlySelf = "Только себе".equals(targetModeSetting.getValue());

        java.util.List<Entity> tracked = new java.util.ArrayList<>();
        if (onlySelf) {
            tracked.add(mc.player);
        } else {
            for (Entity entity : mc.world.getEntities()) {
                if (entity instanceof PlayerEntity) tracked.add(entity);
            }
        }

        for (Entity entity : tracked) {
            Deque<Vec3d> points = trails.computeIfAbsent(entity.getUuid(), id -> new ArrayDeque<>());
            points.addLast(entity.getPos());
            while (points.size() > maxLen) {
                points.removeFirst();
            }
        }

        // чистим следы сущностей, которые больше не отслеживаются (вышли из радиуса видимости и т.п.)
        trails.keySet().removeIf(id -> tracked.stream().noneMatch(e -> e.getUuid().equals(id)));
    }

    @Override
    public void onRenderWorld(WorldRenderContext context) {
        if (trails.isEmpty()) return;
        Vec3d camPos = context.camera().getPos();
        int argb = colorSetting.get();
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float baseAlpha = ((argb >> 24) & 0xFF) / 255f;

        WorldRenderUtil.beginLines(false);
        context.matrixStack().push();
        context.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z);

        for (Deque<Vec3d> points : trails.values()) {
            Vec3d[] arr = points.toArray(new Vec3d[0]);
            for (int i = 0; i < arr.length - 1; i++) {
                // след затухает от хвоста к голове — старые точки более прозрачные
                float fade = (float) i / Math.max(1, arr.length - 1);
                WorldRenderUtil.drawLine(context.matrixStack(),
                        context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                        arr[i], arr[i + 1], r, g, b, baseAlpha * fade);
            }
        }

        context.matrixStack().pop();
        WorldRenderUtil.endLines();
    }
}
