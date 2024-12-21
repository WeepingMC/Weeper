package com.github.weepingmc.inventory;

import org.jetbrains.annotations.NotNull;

public interface InventoryItemStackMoveStrategy {
    default boolean canPlace(@NotNull SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }

    default boolean canTake(@NotNull SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }
}
