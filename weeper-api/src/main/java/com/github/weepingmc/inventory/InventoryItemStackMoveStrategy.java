package com.github.weepingmc.inventory;

import org.jspecify.annotations.NullMarked;

/**
 * Strategy for inventory item stack movement.
 */
@NullMarked
public interface InventoryItemStackMoveStrategy {
    /**
     * Checks if the item stack can be placed in the slot.
     *
     * @param slotItemStackMovementStrategyItem the slot item stack movement strategy item
     * @return true if it can be placed, false otherwise
     */
    default boolean canPlace(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }

    /**
     * Checks if the item stack can be taken from the slot.
     *
     * @param slotItemStackMovementStrategyItem the slot item stack movement strategy item
     * @return true if it can be taken, false otherwise
     */
    default boolean canTake(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }
}
