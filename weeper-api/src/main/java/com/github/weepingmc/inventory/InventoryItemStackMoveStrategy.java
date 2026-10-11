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
     * @deprecated Use {@link #allowedToModify(SlotItemStackMovementStrategyItem)} instead. There is no longer a distinction between placing and taking items in the slot.
     */
    @Deprecated
    default boolean canPlace(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }

    /**
     * Checks if the item stack can be taken from the slot.
     *
     * @param slotItemStackMovementStrategyItem the slot item stack movement strategy item
     * @return true if it can be taken, false otherwise
     * @deprecated Use {@link #allowedToModify(SlotItemStackMovementStrategyItem)} instead. There is no longer a distinction between placing and taking items in the slot.
     */
    @Deprecated
    default boolean canTake(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return true;
    }

    /**
     * Checks if the item stack can be modified in the slot (both placed and taken).
     *
     * @param slotItemStackMovementStrategyItem the slot item stack movement strategy item
     * @return true if it can be modified, false otherwise
     */
    default boolean allowedToModify(SlotItemStackMovementStrategyItem slotItemStackMovementStrategyItem) {
        return this.canPlace(slotItemStackMovementStrategyItem) && this.canTake(slotItemStackMovementStrategyItem);
    }

}
