package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.BooleanSetting;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.ModeSetting;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.util.WorldRenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;

/**
 * Обводит контуром ближайшие сущности в пределах радиуса — визуальная подсветка
 * целей, аналог "target ESP" в клиентах-утилитах. Только отрисовка контура,
 * никакого влияния на попадания/урон/хитбоксы — чистая визуализация того, что
 * и так рендерится игрой в кадре камеры (кроме режима "сквозь стены", который
 * явно завязан на отдельный тумблер и по умолчанию выключен).
 */
public class TargetEspModule extends Module {

    private final ModeSetting targetModeSetting = new ModeSetting("Цели", "Игроки", "Игроки", "Мобы", "Все");
    private final NumberSetting rangeSetting = new NumberSetting("Радиус", 24, 4, 64, 1);
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFFEF4444);
    private final BooleanSetting seeThroughWallsSetting = new BooleanSetting("Сквозь стены", false);
    private final BooleanSetting colorByHealthSetting = new BooleanSetting("Цвет по HP", true);

    public TargetEspModule() {
        super("TargetEsp", ModuleCategory.VISUAL, "Обводит контуром ближайшие цели (игроков/мобов) в радиусе.");
        addSetting(targetModeSetting);
        addSetting(rangeSetting);
        addSetting(colorSetting);
        addSetting(seeThroughWallsSetting);
        addSetting(colorByHealthSetting);
    }

    @Override
    public void onRenderWorld(WorldRenderContext context) {
        if (mc.world == null || mc.player == null) return;

        double range = rangeSetting.get();
        String mode = targetModeSetting.getValue();

        WorldRenderUtil.beginLines(seeThroughWallsSetting.get());
        for (Entity entity : mc.world.getEntities()) {
            if (entity == mc.player || !(entity instanceof LivingEntity living) || living.isDead()) continue;
            if (entity.squaredDistanceTo(mc.player) > range * range) continue;

            boolean isPlayer = entity instanceof PlayerEntity;
            boolean isHostileMob = entity instanceof HostileEntity;
            boolean matches = switch (mode) {
                case "Игроки" -> isPlayer;
                case "Мобы" -> isHostileMob;
                default -> true;
            };
            if (!matches) continue;

            float[] rgba = resolveColor(living);
            Box box = entity.getBoundingBox().expand(0.05);
            context.matrixStack().push();
            context.matrixStack().translate(
                    -context.camera().getPos().x,
                    -context.camera().getPos().y,
                    -context.camera().getPos().z);
            WorldRenderUtil.drawBoxOutline(context.matrixStack(), context.consumers() != null
                            ? context.consumers()
                            : mc.getBufferBuilders().getEntityVertexConsumers(),
                    box, rgba[0], rgba[1], rgba[2], rgba[3]);
            context.matrixStack().pop();
        }
        WorldRenderUtil.endLines();
    }

    private float[] resolveColor(LivingEntity entity) {
        int argb = colorSetting.get();
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float a = ((argb >> 24) & 0xFF) / 255f;

        if (colorByHealthSetting.get()) {
            float pct = entity.getMaxHealth() > 0 ? entity.getHealth() / entity.getMaxHealth() : 1f;
            pct = Math.max(0f, Math.min(1f, pct));
            // зелёный при полном HP -> красный при низком, вместо статичного цвета
            r = 1f - pct;
            g = pct;
            b = 0.15f;
        }
        return new float[]{r, g, b, a};
    }
}
