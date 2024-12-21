package com.github.weepingmc.inventory;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SlotItemStackMovementStrategyItem(
        @NotNull InventoryHolder inventoryHolder,
        @NotNull ItemStack itemStack,
        int slot
) { }
