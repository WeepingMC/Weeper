package com.github.weepingmc.packet;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityMetaBuilder;

import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.TeamMode;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketStepBuilder {

    default PacketStepBuilder sendPlayerProfile(PlayerProfile playerProfile, ProfileAction profileAction){
        return sendPlayerProfile(playerProfile, profileAction, true);
    }

    PacketStepBuilder sendPlayerProfile(PlayerProfile playerProfile, ProfileAction profileAction, boolean listed);

    PacketStepBuilder removePlayerProfile(PlayerProfile playerProfile);

    PacketStepBuilder spawnPlayer(int entityId, UUID uuid, Location location);

    PacketStepBuilder setPlayerTeam(Team team, TeamMode teamMode);

    PacketStepBuilder teleportEntity(int entityId, Location location, boolean onGround);

    PacketStepBuilder mountEntity(int entityId, int... passenger);

    PacketStepBuilder sendBlockChange(Location location, BlockData blockData);

    PacketStepBuilder sleepAt(int entityId, Location location);

    PacketStepBuilder rotateHead(int entityId, float yaw);

    PacketStepBuilder rotateFullHead(int entityId, float yaw, float pitch, boolean onGround);

    PacketStepBuilder movePositionAndRotateFullHead(int entityId, Vector direction, byte yaw, byte pitch, boolean onGround);

    PacketStepBuilder destroyEntity(int entityId);

    PacketStepBuilder animateBlockBreak(int entityID, Location location, byte destroyStage);

    PacketStepBuilder setEquipmentItem(int entityId, EquipmentSlot equipmentSlot, ItemStack itemStack);

    PacketStepBuilder delay(JavaPlugin javaPlugin, long time, TimeUnit timeUnit);

    PacketStepBuilder custom(PacketStep packetStep);

    PacketStepBuilder removeEntity(int entityId);

    PacketStepBuilder animateEntity(int entityId, Animation animation);

    PacketStepBuilder spawnEntity(int entityId, UUID uuid, Location location, EntityType entityType, Vector movementDirection);

    PacketStepBuilder withMeta(int entityId, EntityMetaBuilder metaBuilder);

    /**
     * sets the player abilities
     *
     * @param playerAbilities the abilities to modify
     * @return builder
     */
    PacketStepBuilder withPlayerAbilities(Set<com.github.weepingmc.packet.options.abilities.PlayerAbility> playerAbilities);

    default void send(){
        send(Bukkit.getOnlinePlayers());
    }

    void send(Collection<? extends Player> players);
}
