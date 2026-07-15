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
 * Светящийся пульсирующий диск-пьедестал под ногами игрока ("нимбус") — ещё
 * один чисто декоративный эффект, отдельный от Halo (тот рисуется над головой).
 * Реализован как несколько концентрических колец с разным радиусом/прозрачностью,
 * пульсирующих со временем — имитация "свечения" без текстур/шейдеров.
 */
public class NimbModule extends Module {

    private final ModeSetting targetModeSetting = new ModeSetting("Кому рисовать", "Все игроки", "Все игроки", "Только себе");
    private final NumberSetting radiusSetting = new NumberSetting("Радиус диска", 0.6, 0.2, 1.5, 0.05);
    private final NumberSetting pulseSpeedSetting = new NumberSetting("Скорость пульсации", 1.0, 0.0, 4.0, 0.1);
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFFA855F7);

    private float phase = 0f;

    public NimbModule() {
        super("Nimb", ModuleCategory.VISUAL, "Пульсирующий светящийся диск под ногами игрока.");
        addSetting(targetModeSetting);
        addSetting(radiusSetting);
        addSetting(pulseSpeedSetting);
        addSetting(colorSetting);
    }

    @Override
    public void onTick() {
        phase += pulseSpeedSetting.get() * 0.1f;
        if (phase > Math.PI * 2) phase -= (float) (Math.PI * 2);
    }

    @Override
    public void onRenderWorld(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) return;
        boolean onlySelf = "Только себе".equals(targetModeSetting.getValue());

        int argb = colorSetting.get();
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float baseAlpha = ((argb >> 24) & 0xFF) / 255f;
        float radius = (float) radiusSetting.get();
        Vec3d camPos = context.camera().getPos();

        // множитель пульсации 0.7..1.0, чтобы диск "дышал"
        float pulse = 0.85f + 0.15f * (float) Math.sin(phase);

        WorldRenderUtil.beginLines(false);
        context.matrixStack().push();
        context.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z);

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof PlayerEntity player)) continue;
            if (onlySelf && player != mc.player) continue;

            double cx = player.getX();
            double cy = player.getY() + 0.02;
            double cz = player.getZ();

            // три концентрических кольца с затуханием к краю
            for (int ring = 0; ring < 3; ring++) {
                float ringRadius = radius * pulse * (0.5f + ring * 0.25f);
                float ringAlpha = baseAlpha * (1f - ring * 0.3f);
                WorldRenderUtil.drawRing(context.matrixStack(),
                        context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                        cx, cy, cz, ringRadius, 20, r, g, b, ringAlpha);
            }
        }

        context.matrixStack().pop();
        WorldRenderUtil.endLines();
    }
}
