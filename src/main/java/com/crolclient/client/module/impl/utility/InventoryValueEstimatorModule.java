package com.crolclient.client.module.impl.utility;

import com.crolclient.client.module.Module;
import com.crolclient.client.module.ModuleCategory;
import com.crolclient.client.auction.AuctionPriceDatabase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

/**
 * Оценивает суммарную "стоимость" инвентаря на основе локальной истории
 * цен, накопленной FunTime Auction Helper (см. AuctionPriceDatabase).
 * Не является биржей/аукционом — чисто информационная оценка на клиенте.
 */
public class InventoryValueEstimatorModule extends Module {
    public InventoryValueEstimatorModule() {
        super("InventoryValueEstimator", ModuleCategory.UTILITY, "Оценивает стоимость инвентаря по локальной истории цен аукциона.");
    }

    public double estimateTotalValue() {
        if (mc.player == null) return 0;
        double total = 0;
        DefaultedList<ItemStack> main = mc.player.getInventory().main;
        for (ItemStack stack : main) {
            if (stack.isEmpty()) continue;
            Double median = AuctionPriceDatabase.getInstance().getMedianPrice(stack.getItem());
            if (median != null) {
                total += median * stack.getCount();
            }
        }
        return total;
    }
}
