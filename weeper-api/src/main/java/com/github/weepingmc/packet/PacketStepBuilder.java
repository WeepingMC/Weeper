package com.github.weepingmc.packet;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.TeamMode;
import com.github.weepingmc.packet.options.abilities.PlayerAbility;
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

/**
 * Builds and sends ordered packet steps to one or more players.
 */
@NullMarked
public interface PacketStepBuilder {

    /**
     * Sends the given player profile with the supplied action and keeps it listed.
     *
     * @param playerProfile the profile to send
     * @param profileAction the profile action to apply
     * @return builder
     */
    default PacketStepBuilder sendPlayerProfile(PlayerProfile playerProfile, ProfileAction profileAction) {
        return this.sendPlayerProfile(playerProfile, profileAction, true);
    }

    /**
     * Sends the given player profile with the supplied action.
     *
     * @param playerProfile the profile to send
     * @param profileAction the profile action to apply
     * @param listed        whether the profile should appear as listed
     * @return builder
     */
    PacketStepBuilder sendPlayerProfile(PlayerProfile playerProfile, ProfileAction profileAction, boolean listed);

    /**
     * Removes the given player profile.
     *
     * @param playerProfile the profile to remove
     * @return builder
     */
    PacketStepBuilder removePlayerProfile(PlayerProfile playerProfile);

    /**
     * Spawns a player entity.
     *
     * @param entityId the entity id
     * @param uuid     the entity uuid
     * @param location the spawn location
     * @return builder
     */
    PacketStepBuilder spawnPlayer(int entityId, UUID uuid, Location location);

    /**
     * Updates the team state for the given team.
     *
     * @param team     the team to update
     * @param teamMode the team operation to perform
     * @return builder
     */
    PacketStepBuilder setPlayerTeam(Team team, TeamMode teamMode);

    /**
     * Teleports an entity.
     *
     * @param entityId the entity id
     * @param location the target location
     * @param onGround whether the entity is on the ground
     * @return builder
     */
    PacketStepBuilder teleportEntity(int entityId, Location location, boolean onGround);

    /**
     * Mounts passengers onto an entity.
     *
     * @param entityId  the vehicle entity id
     * @param passenger the passenger entity ids
     * @return builder
     */
    PacketStepBuilder mountEntity(int entityId, int... passenger);

    /**
     * Sends a block change to clients.
     *
     * @param location  the block location
     * @param blockData the new block data
     * @return builder
     */
    PacketStepBuilder sendBlockChange(Location location, BlockData blockData);

    /**
     * Puts an entity into the sleeping pose at the given location.
     *
     * @param entityId the entity id
     * @param location the bed location
     * @return builder
     */
    PacketStepBuilder sleepAt(int entityId, Location location);

    /**
     * Rotates an entity head.
     *
     * @param entityId the entity id
     * @param yaw      the head yaw
     * @return builder
     */
    PacketStepBuilder rotateHead(int entityId, float yaw);

    /**
     * Rotates an entity body and head.
     *
     * @param entityId the entity id
     * @param yaw      the yaw
     * @param pitch    the pitch
     * @param onGround whether the entity is on the ground
     * @return builder
     */
    PacketStepBuilder rotateFullHead(int entityId, float yaw, float pitch, boolean onGround);

    /**
     * Moves an entity relative to its current position and rotates it.
     *
     * @param entityId  the entity id
     * @param direction the relative movement direction
     * @param yaw       the yaw byte value
     * @param pitch     the pitch byte value
     * @param onGround  whether the entity is on the ground
     * @return builder
     */
    PacketStepBuilder movePositionAndRotateFullHead(int entityId, Vector direction, byte yaw, byte pitch, boolean onGround);

    /**
     * Destroys an entity for clients.
     *
     * @param entityId the entity id
     * @return builder
     */
    PacketStepBuilder destroyEntity(int entityId);

    /**
     * Sends a block break animation.
     *
     * @param entityID     the breaker entity id
     * @param location     the block location
     * @param destroyStage the destroy stage
     * @return builder
     */
    PacketStepBuilder animateBlockBreak(int entityID, Location location, byte destroyStage);

    /**
     * Sets an equipment item for an entity slot.
     *
     * @param entityId      the entity id
     * @param equipmentSlot the equipment slot
     * @param itemStack     the item to display
     * @return builder
     */
    PacketStepBuilder setEquipmentItem(int entityId, EquipmentSlot equipmentSlot, ItemStack itemStack);

    /**
     * Delays execution of subsequent packet steps.
     *
     * @param javaPlugin the plugin scheduling the delay
     * @param time       the delay amount
     * @param timeUnit   the delay unit
     * @return builder
     */
    PacketStepBuilder delay(JavaPlugin javaPlugin, long time, TimeUnit timeUnit);

    /**
     * Adds a custom packet step.
     *
     * @param packetStep the step to add
     * @return builder
     */
    PacketStepBuilder custom(PacketStep packetStep);

    /**
     * Removes an entity from clients.
     *
     * @param entityId the entity id
     * @return builder
     */
    PacketStepBuilder removeEntity(int entityId);

    /**
     * Plays an entity animation.
     *
     * @param entityId  the entity id
     * @param animation the animation to play
     * @return builder
     */
    PacketStepBuilder animateEntity(int entityId, Animation animation);

    /**
     * Spawns a generic entity.
     *
     * @param entityId          the entity id
     * @param uuid              the entity uuid
     * @param location          the spawn location
     * @param entityType        the entity type
     * @param movementDirection the initial movement direction
     * @return builder
     */
    PacketStepBuilder spawnEntity(int entityId, UUID uuid, Location location, EntityType entityType, Vector movementDirection);

    /**
     * Sends entity metadata.
     *
     * @param entityId    the entity id
     * @param metaBuilder the metadata builder
     * @return builder
     */
    PacketStepBuilder withMeta(int entityId, EntityMetaBuilder metaBuilder);

    /**
     * sets the player abilities.
     *
     * @param playerAbilities the abilities to modify
     * @return builder
     */
    PacketStepBuilder withPlayerAbilities(Set<PlayerAbility> playerAbilities);

    /**
     * Sends the configured packets to all players.
     */
    default void send() {
        this.send(Bukkit.getOnlinePlayers());
    }

    /**
     * Sends the configured packets to a collection of players.
     *
     * @param players the players that receive the packets
     */
    void send(Collection<? extends Player> players);
}
