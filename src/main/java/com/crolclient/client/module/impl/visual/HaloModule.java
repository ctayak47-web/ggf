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

/**
 * Декоративное кольцо ("нимб"), парящее над головой игроков — чистая косметика.
 */
public class HaloModule extends Module {

    private final ModeSetting targetModeSetting = new ModeSetting("Кому рисовать", "Все игроки", "Все игроки", "Только себе");
    private final NumberSetting radiusSetting = new NumberSetting("Радиус кольца", 0.35, 0.15, 0.8, 0.05);
    private final NumberSetting heightSetting = new NumberSetting("Высота над головой", 0.4, 0.0, 1.2, 0.05);
    private final NumberSetting spinSpeedSetting = new NumberSetting("Скорость вращения", 1.0, 0.0, 4.0, 0.1);
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFFFACC15);

    private float rotation = 0f;

    public HaloModule() {
        super("Halo", ModuleCategory.VISUAL, "Декоративное кольцо над головой игроков.");
        addSetting(targetModeSetting);
        addSetting(radiusSetting);
        addSetting(heightSetting);
        addSetting(spinSpeedSetting);
        addSetting(colorSetting);
    }

    @Override
    public void onTick() {
        rotation += spinSpeedSetting.get() * 4f;
        if (rotation > 360f) rotation -= 360f;
    }

    @Override
    public void onRenderWorld(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) return;
        boolean onlySelf = "Только себе".equals(targetModeSetting.getValue());

        int argb = colorSetting.get();
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float a = ((argb >> 24) & 0xFF) / 255f;
        float radius = (float) radiusSetting.get();
        float height = (float) heightSetting.get();
        Vec3d camPos = context.camera().getPos();

        WorldRenderUtil.beginLines(false);
        context.matrixStack().push();
        context.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z);

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (onlySelf && player != mc.player) continue;

            double cx = player.getX();
            double cy = player.getY() + player.getHeight() + height;
            double cz = player.getZ();

            WorldRenderUtil.drawRing(context.matrixStack(),
                    context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                    cx, cy, cz, radius, 24, (float) Math.toRadians(rotation), r, g, b, a);
        }

        context.matrixStack().pop();
        WorldRenderUtil.endLines();
    }
}
