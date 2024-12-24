package io.papermc.testplugin;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.disguise.DisguiseData;
import io.papermc.paper.disguise.EntityTypeDisguise;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public final class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void onSpawn(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction().isLeftClick() || playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (!playerInteractEvent.hasItem()) {
            return;
        }
        var stack = playerInteractEvent.getItem();
        if (stack == null || !(MaterialTags.SPAWN_EGGS.isTagged(stack) || stack.getType() == Material.ARMOR_STAND)) {
            return;
        }
        if (playerInteractEvent.getInteractionPoint() == null) {
            return;
        }
        var player = playerInteractEvent.getPlayer();

        switch (stack.getType()) {
            case ARMOR_STAND ->
                    spawnOtherEntity(ArmorStand.class, DisguiseData.entity(EntityType.BEE).build(), playerInteractEvent.getInteractionPoint());
            case SHULKER_SPAWN_EGG ->
                    spawnPlayerLikeEntity(player, Shulker.class, playerInteractEvent.getInteractionPoint());
        };
        playerInteractEvent.setCancelled(true);
    }

    private void spawnOtherEntity(Class<? extends Entity> entityClass, EntityTypeDisguise entityTypeDisguise, Location location) {
        location.getWorld().spawn(location, entityClass, entity -> {
            entity.setDisguiseData(entityTypeDisguise);
        });
    }

    private void spawnPlayerLikeEntity(Player player, Class<? extends Entity> entityClass, Location location) {
        player.getWorld().spawn(location, entityClass, entity -> {
            entity.setDisguiseData(DisguiseData
                    .player(player.getPlayerProfile()).listed(false)
                    .skinParts(Bukkit.getServer().newSkinPartsBuilder().withJacket(true).withCape(true).withHat(true).build())
                    .build());


            entity.customName(Component.text("Gollum"));
            Scoreboard scoreboard = player.getScoreboard();

            Team team = scoreboard.getTeam("test");
            if (team == null) {
                team = scoreboard.registerNewTeam("test");
            }
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.addEntry(entity.getScoreboardEntryName());
            team.prefix(Component.text("Happy Coding ", NamedTextColor.GOLD));
            team.suffix(Component.text(" :D", NamedTextColor.RED));
            team.addEntry(entity.getName());
        });
    }
}
