package com.crolclient.client.module.impl.utility;

import com.crolclient.client.mixin.AccessorHandledScreen;
import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;

/**
 * Позволяет быстро перемещать стак предмета между инвентарём игрока и открытым
 * контейнером прокруткой колёсика мыши над слотом — аналог Shift+ЛКМ, но без
 * необходимости кликать. Прокрутка вверх — переместить стак из слота под
 * курсором в другую часть инвентаря; прокрутка вниз — то же в обратную сторону.
 * Использует штатный обработчик клика ({@code QUICK_MOVE}), то есть с точки
 * зрения сервера это обычный клик Shift+ЛКМ, просто вызванный колёсиком.
 */
public class ItemScrollerModule extends Module {

    public ItemScrollerModule() {
        super("ItemScroller", ModuleCategory.UTILITY, "Быстрое перемещение стака колёсиком мыши над слотом.");
    }

    /**
     * Обрабатывает событие прокрутки колёсика над экраном инвентаря.
     *
     * @return false, если событие обработано (стак перемещён) и не должно
     * дальше распространяться (например, для прокрутки списка рецептов);
     * true — событие не относится к ItemScroller и должно обрабатываться как обычно.
     */
    public boolean handleScroll(HandledScreen<?> screen, double mouseX, double mouseY, double verticalAmount) {
        if (verticalAmount == 0) return true;
        if (!(screen instanceof AccessorHandledScreen accessor)) return true;

        int screenX = accessor.crolclient$getX();
        int screenY = accessor.crolclient$getY();

        for (Slot slot : screen.getScreenHandler().slots) {
            int slotX = screenX + slot.x;
            int slotY = screenY + slot.y;
            boolean hovered = mouseX >= slotX && mouseX < slotX + 16 && mouseY >= slotY && mouseY < slotY + 16;
            if (!hovered) continue;
            if (slot.getStack().isEmpty()) return true;

            mc.interactionManager.clickSlot(
                    screen.getScreenHandler().syncId,
                    slot.id,
                    0,
                    SlotActionType.QUICK_MOVE,
                    mc.player
            );
            return false;
        }
        return true;
    }
}
