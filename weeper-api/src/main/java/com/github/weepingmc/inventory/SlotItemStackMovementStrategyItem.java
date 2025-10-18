package com.github.weepingmc.inventory;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record SlotItemStackMovementStrategyItem(
        InventoryHolder inventoryHolder,
        ItemStack itemStack,
        int slot,
        HumanEntity human
) {
}
