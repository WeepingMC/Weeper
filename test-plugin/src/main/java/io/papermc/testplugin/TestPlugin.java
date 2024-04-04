package io.papermc.testplugin;

import com.destroystokyo.paper.MaterialTags;
import com.github.weepingmc.disguise.DisguiseData;
import com.github.weepingmc.disguise.EntityTypeDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Allay;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

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

        switch (stack.getType()) {
            case BLAZE_SPAWN_EGG -> spawnOtherEntity(Blaze.class, DisguiseData.entity(EntityType.BEE), playerInteractEvent.getInteractionPoint());
            case ALLAY_SPAWN_EGG -> spawnPlayerLikeEntity(player, Allay.class, playerInteractEvent.getInteractionPoint());
        };
        playerInteractEvent.setCancelled(true);
    }

    private void spawnOtherEntity(Class<? extends Entity> entityClass, EntityTypeDisguise entityTypeDisguise, Location location) {
        location.getWorld().spawn(location, entityClass, entity -> {
            entity.setDisuiseData(entityTypeDisguise);
        });
    }

    private void spawnPlayerLikeEntity(Player player, Class<? extends Entity> entityClass, Location location) {
        player.getWorld().spawn(location, entityClass, entity -> {
            entity.customName(Component.text("Gollum"));
            entity.setDisuiseData(DisguiseData
                .player(player.getPlayerProfile()).listed(false)
                .skinParts(Bukkit.getPacketPipeline().createSkinPartsBuilder().withJacket().withCape().build())
                .build());
            Team team = player.getServer().getScoreboardManager().getMainScoreboard().registerNewTeam(Bukkit.getPacketPipeline().generateRandomString(10, true, true));
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.addEntry(entity.getScoreboardEntryName());
            team.prefix(Component.text("Happy Coding ", NamedTextColor.GOLD));
            team.suffix(Component.text(" :D", NamedTextColor.RED));
        });
    }
}
