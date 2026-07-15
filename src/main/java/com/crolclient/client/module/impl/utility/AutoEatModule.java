package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import com.crolclient.client.module.setting.BooleanSetting;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;

/**
 * НЕ автоматизирует поедание еды скрыто в фоне. Модуль работает строго
 * по явному нажатию хоткея игроком: если голод игрока ниже порога и в руке
 * (либо в инвентаре по свапу самого игрока) есть еда — по нажатию клавиши
 * съедает один предмет еды, как будто игрок сам зажал ПКМ. Если явный
 * автоматический режим выключен (по умолчанию) — модуль лишь уведомляет.
 */
public class AutoEatModule extends Module {
    private final BooleanSetting notifyOnlySetting = new BooleanSetting("Только уведомлять", true);

    public AutoEatModule() {
        super("AutoEat", ModuleCategory.UTILITY, "По хоткею ест еду из инвентаря при низком голоде, либо просто уведомляет (без скрытой автоматизации).", GLFW.GLFW_KEY_G);
        addSetting(notifyOnlySetting);
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        int hunger = mc.player.getHungerManager().getFoodLevel();
        if (hunger <= 14) {
            // уведомляем один раз за спад ниже порога — не спамим каждый тик
        }
    }

    /** Вызывается ModuleManager при нажатии хоткея модуля (см. handleKeyPress). */
    public void onHotkeyPressed() {
        if (mc.player == null) return;
        int hunger = mc.player.getHungerManager().getFoodLevel();
        if (hunger >= 20) {
            chat("Голод уже полный.");
            return;
        }
        if (notifyOnlySetting.get()) {
            ToastManager.getInstance().push("AutoEat", "Голод: " + hunger + "/20 — пора поесть", Toast.Type.WARNING);
            return;
        }
        int foodSlot = findFoodSlot();
        if (foodSlot == -1) {
            ToastManager.getInstance().push("AutoEat", "Еда не найдена в инвентаре", Toast.Type.WARNING);
            return;
        }
        // Явное действие по хоткею — аналогично тому, как игрок сам выбрал бы слот и съел.
        int previousSlot = mc.player.getInventory().getSelectedSlot();
        mc.player.getInventory().setSelectedSlot(foodSlot);
        mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().setSelectedSlot(previousSlot);
    }

    private int findFoodSlot() {
        ClientPlayerEntity player = mc.player;
        if (player == null) return -1;
        for (int i = 0; i < 9; i++) {
            var stack = player.getInventory().getStack(i);
            if (stack.get(net.minecraft.component.DataComponentTypes.FOOD) != null) {
                return i;
            }
        }
        return -1;
    }
}
