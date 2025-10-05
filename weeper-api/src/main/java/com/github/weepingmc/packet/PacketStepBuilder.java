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
import org.jetbrains.annotations.Nullable;
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
     * @deprecated Use {@link #withPlayerAbilities(Set)}
     * @param playerAbilities [Invulnerable, Flying, Allow Flying, Creative Mode (Instant Break)]
     * @param flySpeed 0.05 by default.
     * @param fieldOfViewModifier Modifies the field of view, like a speed potion. A Notchian server will use the same value as the movement speed sent in the Entity Properties packet, which defaults to 0.1 for players.
     * @return builder
     */
    @Deprecated
    PacketStepBuilder playerAbilities(Set<PlayerAbility> playerAbilities, float flySpeed, float fieldOfViewModifier);

    /**
     * sets the player abilities
     *
     * @param playerAbilities the abilities to modify
     * @return builder
     */
    PacketStepBuilder withPlayerAbilities(Set<com.github.weepingmc.packet.options.abilities.PlayerAbility> playerAbilities);

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
    PacketStepBuilder showTestMarker(Location location, Color color, @Nullable String text, int time);

    default void send(){
        send(Bukkit.getOnlinePlayers());
    }

    void send(Collection<? extends Player> players);
}
