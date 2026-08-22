package com.github.weepingmc.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

/**
 * Represents an item in a slot for item stack movement strategy.
 *
 * @param inventoryHolder  the inventory holder
 * @param itemStack        the item stack
 * @param slot             the slot index
 * @param human            the human entity involved
 */
@NullMarked
public record SlotItemStackMovementStrategyItem(
        InventoryHolder inventoryHolder,
        ItemStack itemStack,
        int slot,
        HumanEntity human
) {
}
