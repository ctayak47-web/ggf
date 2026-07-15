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
 * Декоративная коническая "шляпа" (бамбуковая шляпа), парящая над головой
 * игроков — популярный косметический эффект в некоторых лаунчерах. Чистая
 * визуализация из линий, без текстур/моделей — не влияет на хитбокс/обзор.
 */
public class ChinaHatModule extends Module {

    private final ModeSetting targetModeSetting = new ModeSetting("Кому рисовать", "Все игроки", "Все игроки", "Только себе");
    private final NumberSetting baseRadiusSetting = new NumberSetting("Радиус основания", 0.5, 0.2, 1.0, 0.05);
    private final NumberSetting heightSetting = new NumberSetting("Высота конуса", 0.35, 0.1, 1.0, 0.05);
    private final NumberSetting hoverSetting = new NumberSetting("Парение над головой", 0.15, 0.0, 0.6, 0.05);
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFFE2B04A);

    public ChinaHatModule() {
        super("ChinaHat", ModuleCategory.VISUAL, "Декоративная коническая шляпа над головой игроков.");
        addSetting(targetModeSetting);
        addSetting(baseRadiusSetting);
        addSetting(heightSetting);
        addSetting(hoverSetting);
        addSetting(colorSetting);
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
        float baseRadius = (float) baseRadiusSetting.get();
        float height = (float) heightSetting.get();
        float hover = (float) hoverSetting.get();
        Vec3d camPos = context.camera().getPos();

        WorldRenderUtil.beginLines(false);
        context.matrixStack().push();
        context.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z);

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (onlySelf && player != mc.player) continue;

            double cx = player.getX();
            double baseY = player.getY() + player.getHeight() + hover;
            double cz = player.getZ();

            WorldRenderUtil.drawConeOutline(context.matrixStack(),
                    context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                    cx, baseY, cz, baseRadius, height, 16, r, g, b, a);
        }

        context.matrixStack().pop();
        WorldRenderUtil.endLines();
    }
}
