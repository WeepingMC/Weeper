package com.github.weepingmc.offline;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;

public class CraftEditPlayer implements EditPlayer {

    private final Player player;

    public CraftEditPlayer(ServerPlayer serverPlayer) {
        this.player = serverPlayer.getBukkitEntity();
    }

    @Override
    public PlayerInventory getInventory() {
        return this.player.getInventory();
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer() {
        return this.player.getPersistentDataContainer();
    }
}
