package com.github.weepingmc.inventory;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface InventoryItemStackMoveStrategy {
    default boolean canPlace(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }

    default boolean canTake(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }
}
