package com.github.weepingmc.offline;

import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public interface EditPlayer {

    /**
     * Retrieves the inventory of a player.
     *
     * @return The player's inventory.
     */
    @NotNull
    PlayerInventory getInventory();

    /**
     * Retrieves the PersistentDataContainer of a player.
     *
     * @return The player's PersistentDataContainer.
     */
    @NotNull
    PersistentDataContainer getPersistentDataContainer();

}
