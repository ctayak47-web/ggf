package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import net.minecraft.client.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

/**
 * Декоративное кольцо частиц под ногами игрока в момент прыжка.
 * ВАЖНО: модуль реагирует только на факт прыжка (изменение onGround у
 * ЛОКАЛЬНОГО игрока) и не содержит какой-либо информации о хитбоксах,
 * дистанции атаки, таймингах или состоянии других сущностей — это чистая
 * косметика по аналогии с частицами двойного прыжка в лаунчерах.
 */
public class JumpParticleCircleModule extends Module {
    private final ColorSetting colorSetting = new ColorSetting("Цвет кольца", 0xFFFFFFFF);
    private boolean wasOnGround = true;

    public JumpParticleCircleModule() {
        super("JumpParticleCircle", ModuleCategory.VISUAL, "Декоративное кольцо частиц под ногами в момент прыжка.");
        addSetting(colorSetting);
    }

    @Override
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        boolean onGround = mc.player.isOnGround();
        if (wasOnGround && !onGround && mc.player.getVelocity().y > 0.1) {
            spawnRing();
        }
        wasOnGround = onGround;
    }

    private void spawnRing() {
        Vec3d pos = mc.player.getPos();
        int points = 16;
        for (int i = 0; i < points; i++) {
            double angle = (2 * Math.PI * i) / points;
            double px = pos.x + Math.cos(angle) * 0.4;
            double pz = pos.z + Math.sin(angle) * 0.4;
            mc.world.addParticle(ParticleTypes.CLOUD, px, pos.y + 0.05, pz, 0, 0.01, 0);
        }
    }
}
