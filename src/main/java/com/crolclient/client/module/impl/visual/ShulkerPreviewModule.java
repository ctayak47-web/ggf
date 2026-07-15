package com.crolclient.client.module.impl.visual;

import com.crolclient.client.mixin.AccessorHandledScreen;
import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.module.setting.ColorSetting;
import com.crolclient.client.util.RenderUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.List;

/**
 * Показывает всплывающее превью 3x9 содержимого шалкер-бокса при наведении
 * курсора на его слот в любом инвентаре — экономит время на открытии/закрытии
 * шалкера ради проверки содержимого. Только чтение уже загруженных клиенту
 * данных предмета (компонент контейнера), никаких дополнительных запросов
 * к серверу не отправляется.
 * <p>
 * Подключается не через onRenderWorld/onRender (у ClickGUI и обычного HUD
 * разный жизненный цикл, а этот модуль должен рисовать поверх экрана
 * инвентаря) — сам рендер вызывается из {@code CrolClient#onInitializeClient}
 * через {@code ScreenEvents.afterRender}, см. метод {@link #renderPreview}.
 */
public class ShulkerPreviewModule extends Module {

    private final ColorSetting backgroundColorSetting = new ColorSetting("Фон превью", 0xE0101010);

    public ShulkerPreviewModule() {
        super("ShulkerPreview", ModuleCategory.VISUAL, "Превью содержимого шалкер-бокса при наведении в инвентаре.");
        addSetting(backgroundColorSetting);
    }

    /** Вызывается после рендера любого HandledScreen — ищет наведённый слот с шалкер-боксом. */
    public void renderPreview(HandledScreen<?> screen, DrawContext context, int mouseX, int mouseY) {
        if (!(screen instanceof AccessorHandledScreen accessor)) return;

        int screenX = accessor.crolclient$getX();
        int screenY = accessor.crolclient$getY();

        for (Slot slot : screen.getScreenHandler().slots) {
            int slotX = screenX + slot.x;
            int slotY = screenY + slot.y;
            if (mouseX < slotX || mouseX >= slotX + 16 || mouseY < slotY || mouseY >= slotY + 16) continue;

            ItemStack stack = slot.getStack();
            if (stack.isEmpty() || !isShulkerBox(stack)) return;

            List<ItemStack> contents = readContents(stack);
            drawGrid(context, contents, mouseX, mouseY);
            return;
        }
    }

    private boolean isShulkerBox(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().getTranslationKey().contains("shulker_box");
    }

    private List<ItemStack> readContents(ItemStack shulkerStack) {
        ContainerComponent component = shulkerStack.get(DataComponentTypes.CONTAINER);
        if (component == null) return List.of();
        return component.stream().toList();
    }

    private void drawGrid(DrawContext context, List<ItemStack> contents, int mouseX, int mouseY) {
        int columns = 9;
        int rows = 3;
        int slotSize = 18;
        int padding = 6;
        int width = columns * slotSize + padding * 2;
        int height = rows * slotSize + padding * 2;

        // позиционируем превью так, чтобы не вылезать за правый край экрана
        int px = mouseX + 16;
        int py = mouseY;
        int screenWidth = mc.getWindow().getScaledWidth();
        if (px + width > screenWidth) px = mouseX - width - 16;

        context.fill(px, py, px + width, py + height, backgroundColorSetting.get());
        RenderUtil.drawBorder(context, px, py, width, height, 1, 0x80FFFFFF);

        for (int i = 0; i < 27; i++) {
            int col = i % columns;
            int row = i / columns;
            int slotX = px + padding + col * slotSize;
            int slotY = py + padding + row * slotSize;

            ItemStack stack = i < contents.size() ? contents.get(i) : ItemStack.EMPTY;
            if (!stack.isEmpty()) {
                context.drawItem(stack, slotX, slotY);
                context.drawItemInSlot(mc.textRenderer, stack, slotX, slotY);
            }
        }
    }
}
