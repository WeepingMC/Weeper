package com.github.weepingmc.offline;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EditPlayer {

    /**
     * Retrieves the inventory of a player.
     *
     * @return The player's inventory.
     */
    PlayerInventory getInventory();

    /**
     * Retrieves the PersistentDataContainer of a player.
     *
     * @return The player's PersistentDataContainer.
     */
    PersistentDataContainer getPersistentDataContainer();

}
