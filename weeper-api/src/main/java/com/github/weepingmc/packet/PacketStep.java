package com.github.weepingmc.packet;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

/**
 * Represents a step in a packet pipeline.
 */
@FunctionalInterface
@NullMarked
public interface PacketStep {
    /**
     * Executes this packet step for a player.
     *
     * @param player the player to execute the step for
     */
    void execute(Player player);
}
