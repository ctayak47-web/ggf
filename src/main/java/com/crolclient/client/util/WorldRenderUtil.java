package com.crolclient.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/**
 * Общие утилиты 3D-рендера в игровом мире (в отличие от {@link RenderUtil},
 * который рисует поверх экрана в HUD-координатах).
 * <p>
 * Все методы ожидают, что {@link MatrixStack} уже смещён на позицию камеры
 * (см. использование в {@code WorldRenderEvents.AFTER_ENTITIES} — контекст
 * Fabric API передаёт матрицу, уже готовую для мировых координат минус
 * позиция камеры, поэтому вызывающий код обычно просто транслирует matrices
 * на позицию отрисовываемого объекта минус {@code context.camera().getPos()}).
 * <p>
 * Никакие функции здесь не читают и не изменяют состояние игры (хитбоксы,
 * коллизии, урон) — только отрисовка примитивов.
 */
public final class WorldRenderUtil {

    private WorldRenderUtil() {
    }

    /** Рисует рамку-бокс (контур) заданного размера цветом rgba (0..1). */
    public static void drawBoxOutline(MatrixStack matrices, VertexConsumerProvider consumers, Box box,
                                       float r, float g, float b, float a) {
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry = matrices.peek();

        // Нижняя грань
        line(buffer, entry, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.minY, box.minZ, box.maxX, box.minY, box.maxZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, r, g, b, a);
        line(buffer, entry, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, r, g, b, a);
        // Верхняя грань
        line(buffer, entry, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
        line(buffer, entry, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, r, g, b, a);
        // Вертикальные рёбра
        line(buffer, entry, box.minX, box.minY, box.minZ, box.minX, box.maxY, box.minZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.minY, box.minZ, box.maxX, box.maxY, box.minZ, r, g, b, a);
        line(buffer, entry, box.maxX, box.minY, box.maxZ, box.maxX, box.maxY, box.maxZ, r, g, b, a);
        line(buffer, entry, box.minX, box.minY, box.maxZ, box.minX, box.maxY, box.maxZ, r, g, b, a);
    }

    private static void line(VertexConsumer buffer, MatrixStack.Entry entry,
                              double x1, double y1, double z1, double x2, double y2, double z2,
                              float r, float g, float b, float a) {
        float nx = (float) (x2 - x1);
        float ny = (float) (y2 - y1);
        float nz = (float) (z2 - z1);
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        buffer.vertex(entry.getPositionMatrix(), (float) x1, (float) y1, (float) z1)
                .color(r, g, b, a).normal(entry, nx, ny, nz);
        buffer.vertex(entry.getPositionMatrix(), (float) x2, (float) y2, (float) z2)
                .color(r, g, b, a).normal(entry, nx, ny, nz);
    }

    /**
     * Рисует горизонтальное кольцо (диск-контур) радиуса {@code radius} на высоте {@code y}
     * относительно центра (cx, cz). Используется для Halo/Nimb.
     */
    public static void drawRing(MatrixStack matrices, VertexConsumerProvider consumers,
                                 double cx, double cy, double cz, float radius, int segments,
                                 float r, float g, float b, float a) {
        drawRing(matrices, consumers, cx, cy, cz, radius, segments, 0f, r, g, b, a);
    }

    /** Перегрузка с фазовым сдвигом (в радианах) — для анимированного вращения кольца. */
    public static void drawRing(MatrixStack matrices, VertexConsumerProvider consumers,
                                 double cx, double cy, double cz, float radius, int segments, float phase,
                                 float r, float g, float b, float a) {
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry = matrices.peek();
        for (int i = 0; i < segments; i++) {
            double a1 = phase + (Math.PI * 2 * i) / segments;
            double a2 = phase + (Math.PI * 2 * (i + 1)) / segments;
            double x1 = cx + Math.cos(a1) * radius;
            double z1 = cz + Math.sin(a1) * radius;
            double x2 = cx + Math.cos(a2) * radius;
            double z2 = cz + Math.sin(a2) * radius;
            line(buffer, entry, x1, cy, z1, x2, cy, z2, r, g, b, a);
        }
    }

    /**
     * Рисует конус (полый, из линий) вершиной вверх — используется для ChinaHat.
     * Основание — окружность радиуса baseRadius на высоте baseY, вершина на высоте baseY + height.
     */
    public static void drawConeOutline(MatrixStack matrices, VertexConsumerProvider consumers,
                                        double cx, double baseY, double cz, float baseRadius, float height,
                                        int segments, float r, float g, float b, float a) {
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry = matrices.peek();
        double apexY = baseY + height;
        for (int i = 0; i < segments; i++) {
            double a1 = (Math.PI * 2 * i) / segments;
            double a2 = (Math.PI * 2 * (i + 1)) / segments;
            double x1 = cx + Math.cos(a1) * baseRadius;
            double z1 = cz + Math.sin(a1) * baseRadius;
            double x2 = cx + Math.cos(a2) * baseRadius;
            double z2 = cz + Math.sin(a2) * baseRadius;
            // основание
            line(buffer, entry, x1, baseY, z1, x2, baseY, z2, r, g, b, a);
            // ребро к вершине (каждую 4-ю грань, чтобы не загромождать вид)
            if (i % 4 == 0) {
                line(buffer, entry, x1, baseY, z1, cx, apexY, cz, r, g, b, a);
            }
        }
    }

    /** Прямая линия между двумя произвольными точками мира — используется для Trails. */
    public static void drawLine(MatrixStack matrices, VertexConsumerProvider consumers,
                                 Vec3d from, Vec3d to, float r, float g, float b, float a) {
        VertexConsumer buffer = consumers.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry = matrices.peek();
        line(buffer, entry, from.x, from.y, from.z, to.x, to.y, to.z, r, g, b, a);
    }

    /** Выполняет обычные RenderSystem-настройки перед рисованием линий (толщина, blend, depth test выкл.). */
    public static void beginLines(boolean seeThroughWalls) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.lineWidth(2.0f);
        if (seeThroughWalls) {
            RenderSystem.disableDepthTest();
        }
    }

    public static void endLines() {
        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}
