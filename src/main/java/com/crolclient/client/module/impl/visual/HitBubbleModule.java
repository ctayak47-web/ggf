package com.crolclient.client.module.impl.visual;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.module.setting.NumberSetting;
import com.crolclient.client.util.WorldRenderUtil;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Рисует декоративные "пузырьки", разлетающиеся с места сущности при уменьшении
 * её здоровья (по сути — визуальный индикатор попадания). Определяется чисто
 * по изменению {@link LivingEntity#getHealth()} между тиками — никаких пакетов
 * урона не перехватывается и не подделывается.
 */
public class HitBubbleModule extends Module {

    private final NumberSetting rangeSetting = new NumberSetting("Радиус отслеживания", 16, 4, 48, 1);
    private final NumberSetting particleCountSetting = new NumberSetting("Кол-во частиц", 6, 1, 20, 1);
    private final NumberSetting lifetimeSetting = new NumberSetting("Время жизни (тиков)", 12, 4, 40, 1);
    private final ColorSetting colorSetting = new ColorSetting("Цвет", 0xFF38BDF8);

    private final Map<UUID, Float> lastHealth = new HashMap<>();
    private final List<Bubble> bubbles = new ArrayList<>();

    public HitBubbleModule() {
        super("HitBubble", ModuleCategory.VISUAL, "Партиклы-пузырьки при получении сущностью урона.");
        addSetting(rangeSetting);
        addSetting(particleCountSetting);
        addSetting(lifetimeSetting);
        addSetting(colorSetting);
    }

    @Override
    protected void onDisable() {
        lastHealth.clear();
        bubbles.clear();
    }

    @Override
    public void onTick() {
        if (mc.world == null || mc.player == null) return;
        double range = rangeSetting.get();

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof LivingEntity living) || living.isDead()) continue;
            if (entity.squaredDistanceTo(mc.player) > range * range) continue;

            float health = living.getHealth();
            Float prev = lastHealth.put(entity.getUuid(), health);
            if (prev != null && health < prev - 0.01f) {
                spawnBubbles(living);
            }
        }

        // обновляем/удаляем частицы
        Iterator<Bubble> it = bubbles.iterator();
        while (it.hasNext()) {
            Bubble bubble = it.next();
            bubble.pos = bubble.pos.add(bubble.velocity);
            bubble.velocity = bubble.velocity.multiply(0.92).add(0, -0.01, 0);
            bubble.age++;
            if (bubble.age > lifetimeSetting.get()) it.remove();
        }
    }

    private void spawnBubbles(LivingEntity entity) {
        int count = (int) particleCountSetting.get();
        Vec3d origin = entity.getPos().add(0, entity.getHeight() * 0.6, 0);
        for (int i = 0; i < count; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = 0.05 + Math.random() * 0.08;
            Vec3d velocity = new Vec3d(Math.cos(angle) * speed, 0.08 + Math.random() * 0.05, Math.sin(angle) * speed);
            bubbles.add(new Bubble(origin, velocity));
        }
    }

    @Override
    public void onRenderWorld(WorldRenderContext context) {
        if (bubbles.isEmpty()) return;
        Vec3d camPos = context.camera().getPos();
        int argb = colorSetting.get();
        float r = ((argb >> 16) & 0xFF) / 255f;
        float g = ((argb >> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;
        float baseAlpha = ((argb >> 24) & 0xFF) / 255f;
        float maxAge = (float) lifetimeSetting.get();

        WorldRenderUtil.beginLines(false);
        context.matrixStack().push();
        context.matrixStack().translate(-camPos.x, -camPos.y, -camPos.z);

        for (Bubble bubble : bubbles) {
            float fade = 1f - (bubble.age / maxAge);
            float size = 0.05f * fade + 0.02f;
            // маленький крестик-искра вместо полноценного спрайта — не требует текстур
            Vec3d p = bubble.pos;
            WorldRenderUtil.drawLine(context.matrixStack(),
                    context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                    p.add(-size, 0, 0), p.add(size, 0, 0), r, g, b, baseAlpha * fade);
            WorldRenderUtil.drawLine(context.matrixStack(),
                    context.consumers() != null ? context.consumers() : mc.getBufferBuilders().getEntityVertexConsumers(),
                    p.add(0, -size, 0), p.add(0, size, 0), r, g, b, baseAlpha * fade);
        }

        context.matrixStack().pop();
        WorldRenderUtil.endLines();
    }

    private static class Bubble {
        Vec3d pos;
        Vec3d velocity;
        int age = 0;

        Bubble(Vec3d pos, Vec3d velocity) {
            this.pos = pos;
            this.velocity = velocity;
        }
    }
}
