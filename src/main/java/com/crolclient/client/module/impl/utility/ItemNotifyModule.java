package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.notification.Toast;
import com.crolclient.client.notification.ToastManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashSet;
import java.util.Set;

/** Уведомляет всплывающим тостом о подборе редких предметов (настраиваемый список). */
public class ItemNotifyModule extends Module {
    private static final Set<net.minecraft.item.Item> RARE_ITEMS = new HashSet<>(java.util.List.of(
            Items.NETHERITE_INGOT, Items.NETHER_STAR, Items.ELYTRA, Items.TOTEM_OF_UNDYING,
            Items.DIAMOND, Items.ENCHANTED_GOLDEN_APPLE
    ));

    private final Set<net.minecraft.item.Item> lastSeenSlots = new HashSet<>();

    public ItemNotifyModule() {
        super("ItemNotify", ModuleCategory.UTILITY, "Уведомляет о подборе редких предметов (алмазы, незерит, тотемы и т.д.).");
    }

    @Override
    public void onTick() {
        if (mc.player == null) return;
        DefaultedList<ItemStack> inventory = mc.player.getInventory().main;
        for (ItemStack stack : inventory) {
            if (stack.isEmpty()) continue;
            if (RARE_ITEMS.contains(stack.getItem()) && !lastSeenSlots.contains(stack.getItem())) {
                ToastManager.getInstance().push("Редкий предмет", stack.getName().getString() + " x" + stack.getCount(), Toast.Type.SUCCESS);
                lastSeenSlots.add(stack.getItem());
            }
        }
        lastSeenSlots.removeIf(item -> inventory.stream().noneMatch(s -> s.getItem() == item));
    }
}
