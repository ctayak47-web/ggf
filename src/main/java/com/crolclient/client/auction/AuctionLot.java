package com.crolclient.client.auction;

import net.minecraft.item.Item;

/**
 * Один просканированный лот аукциона: предмет, извлечённая цена и слот
 * в контейнере GUI, из которого он был считан. Используется как единица
 * данных между AuctionScanner (парсинг) и AuctionPriceDatabase (статистика).
 */
public class AuctionLot {

    public final int slotIndex;
    public final Item item;
    public final String displayName;
    public final int stackCount;
    public final double price;

    public AuctionLot(int slotIndex, Item item, String displayName, int stackCount, double price) {
        this.slotIndex = slotIndex;
        this.item = item;
        this.displayName = displayName;
        this.stackCount = stackCount;
        this.price = price;
    }

    /** Цена за единицу предмета (для стаков > 1) — используется при сравнении со средней ценой по типу. */
    public double pricePerUnit() {
        return stackCount > 0 ? price / stackCount : price;
    }
}
