package com.github.weepingmc.offline;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

public class CraftEditPlayer implements EditPlayer {

    private final Player player;

    public CraftEditPlayer(ServerPlayer serverPlayer){
        this.player = serverPlayer.getBukkitEntity();
    }

    @Override
    public @NotNull PlayerInventory getInventory() {
        return player.getInventory();
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return player.getPersistentDataContainer();
    }
}
