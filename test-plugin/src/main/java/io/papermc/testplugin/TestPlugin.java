package io.papermc.testplugin;

import com.destroystokyo.paper.MaterialTags;
import com.github.weepingmc.disguise.DisguiseData;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void onEntityTrack(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction().isLeftClick() || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (!playerInteractEvent.hasItem()) {
            return;
        }
        var stack = playerInteractEvent.getItem();
        if (stack == null || !MaterialTags.SPAWN_EGGS.isTagged(stack)) {
            return;
        }
        if (playerInteractEvent.getInteractionPoint() == null) {
            return;
        }
        var player = playerInteractEvent.getPlayer();

        var entityType = switch (stack.getType()) {
            case BLAZE_SPAWN_EGG -> EntityType.BLAZE;
            case ALLAY_SPAWN_EGG -> EntityType.ALLAY;
            default -> EntityType.ZOMBIE;
        };


        player.getWorld().spawn(playerInteractEvent.getInteractionPoint(), entityType.getEntityClass(), entity -> {
            entity.setDisuiseData(DisguiseData.player(player.getPlayerProfile()).listed(false).build());
        });
        playerInteractEvent.setCancelled(true);
    }
}
