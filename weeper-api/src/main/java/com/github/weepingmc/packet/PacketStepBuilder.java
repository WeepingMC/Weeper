package com.github.weepingmc.packet;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import com.github.weepingmc.packet.options.PlayerAbility;
import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.TeamMode;
import java.awt.Color;
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
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PacketStepBuilder {

    @NotNull
    default PacketStepBuilder sendPlayerProfile(@NotNull PlayerProfile playerProfile, @NotNull ProfileAction profileAction){
        return sendPlayerProfile(playerProfile, profileAction, true);
    }

    @NotNull
    PacketStepBuilder sendPlayerProfile(@NotNull PlayerProfile playerProfile, @NotNull ProfileAction profileAction, boolean listed);

    @NotNull
    PacketStepBuilder removePlayerProfile(@NotNull PlayerProfile playerProfile);

    @NotNull
    PacketStepBuilder spawnPlayer(int entityId, @NotNull UUID uuid, @NotNull Location location);

    @NotNull
    PacketStepBuilder setPlayerTeam(@NotNull Team team, @NotNull TeamMode teamMode);

    @NotNull
    PacketStepBuilder teleportEntity(int entityId, @NotNull Location location, boolean onGround);

    @NotNull
    PacketStepBuilder mountEntity(int entityId, int @NotNull ... passenger);

    @NotNull
    PacketStepBuilder sendBlockChange(@NotNull Location location, @NotNull BlockData blockData);

    @NotNull
    PacketStepBuilder sleepAt(int entityId, @NotNull Location location);

    @NotNull
    PacketStepBuilder rotateHead(int entityId, float yaw);

    @NotNull
    PacketStepBuilder rotateFullHead(int entityId, float yaw, float pitch, boolean onGround);

    @NotNull
    PacketStepBuilder movePositionAndRotateFullHead(int entityId, @NotNull Vector direction, byte yaw, byte pitch, boolean onGround);

    @NotNull
    PacketStepBuilder destroyEntity(int entityId);

    @NotNull
    PacketStepBuilder animateBlockBreak(int entityID, @NotNull Location location, byte destroyStage);

    @NotNull
    PacketStepBuilder setEquipmentItem(int entityId, @NotNull EquipmentSlot equipmentSlot, @NotNull ItemStack itemStack);

    @NotNull
    PacketStepBuilder delay(@NotNull JavaPlugin javaPlugin, long time, @NotNull TimeUnit timeUnit);

    @NotNull
    PacketStepBuilder custom(@NotNull PacketStep packetStep);

    @NotNull
    PacketStepBuilder removeEntity(int entityId);

    @NotNull
    PacketStepBuilder animateEntity(int entityId, @NotNull Animation animation);

    @NotNull
    PacketStepBuilder spawnEntity(int entityId, @NotNull UUID uuid, @NotNull Location location, @NotNull EntityType entityType, @NotNull Vector movementDirection);

    @NotNull
    PacketStepBuilder withMeta(int entityId, @NotNull EntityMetaBuilder metaBuilder);

    /**
     * sets the player abilities
     *
     * @param playerAbilities [Invulnerable, Flying, Allow Flying, Creative Mode (Instant Break)]
     * @param flySpeed 0.05 by default.
     * @param fieldOfViewModifier Modifies the field of view, like a speed potion. A Notchian server will use the same value as the movement speed sent in the Entity Properties packet, which defaults to 0.1 for players.
     * @return builder
     */
    @NotNull
    PacketStepBuilder playerAbilities(@NotNull Set<PlayerAbility> playerAbilities, float flySpeed, float fieldOfViewModifier);

    /**
     * sets the player abilities
     *
     * @param playerAbilities the abilities to modify
     * @return builder
     */
    @NotNull
    PacketStepBuilder withPlayerAbilities(@NotNull Set<com.github.weepingmc.packet.options.abilities.PlayerAbility> playerAbilities);

    /**
     * Shows a test marker on the client.
     *
     * @param location the location to show the marker at
     * @param color the color of the marker
     * @param text the text to show on the marker
     * @param time the time to show the marker for in ticks
     * @return builder
     *
     * @deprecated Packet is removed in Minecraft 1.21.9
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    @Deprecated(forRemoval = true, since = "1.21.9")
    @NotNull
    PacketStepBuilder showTestMarker(@NotNull Location location, @NotNull Color color, @Nullable String text, int time);

    default void send(){
        send(Bukkit.getOnlinePlayers());
    }

    void send(@NotNull Collection<? extends Player> players);
}
