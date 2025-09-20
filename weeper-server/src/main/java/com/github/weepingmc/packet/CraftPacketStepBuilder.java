package com.github.weepingmc.packet;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.weepingmc.packet.ChainablePacketStep.DelayPacketStep;
import com.github.weepingmc.packet.ChainablePacketStep.NmsPacketStep;
import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import com.github.weepingmc.packet.options.PlayerAbility;
import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.TeamMode;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundRotateHeadPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.protocol.game.ClientboundSetPassengersPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Action;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.scoreboard.CraftTeam;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static com.github.weepingmc.packet.CraftPacketConversion.from;
import static com.github.weepingmc.packet.CraftPacketConversion.mapPlayerAbilities;

public class CraftPacketStepBuilder implements PacketStepBuilder {

    private final ChainablePacketStep initial = new ChainablePacketStep();

    @Override
    @Nonnull
    public PacketStepBuilder sendPlayerProfile(@Nonnull PlayerProfile playerProfile, @Nonnull ProfileAction profileAction, boolean listed) {
        GameProfile gameProfile = ((CraftPlayerProfile) playerProfile).getGameProfile();

        if (profileAction == ProfileAction.REMOVE_PLAYER) {
            removePlayerProfile(playerProfile);
        } else {
            ClientboundPlayerInfoUpdatePacket.Entry playerUpdate =
                new ClientboundPlayerInfoUpdatePacket.Entry(
                    gameProfile.id(),
                    gameProfile,
                    listed,
                    0,
                    GameType.DEFAULT_MODE,
                    null,
                    true,
                    0,
                    null
                );
            ClientboundPlayerInfoUpdatePacket clientboundPlayerInfoUpdatePacket = new ClientboundPlayerInfoUpdatePacket(EnumSet.of(from(profileAction)), Collections.singletonList(playerUpdate));
            initial.setNext(of(clientboundPlayerInfoUpdatePacket));
        }

        return this;
    }

    @Override
    public @NotNull PacketStepBuilder removePlayerProfile(@NotNull PlayerProfile playerProfile) {
        ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(playerProfile.getId()));
        initial.setNext(of(clientboundPlayerInfoRemovePacket));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder spawnPlayer(int entityId, @Nonnull UUID uuid, @Nonnull Location location) {
        initial.setNext(of(new ClientboundAddEntityPacket(
            entityId,
            uuid,
            location.x(),
            location.y(),
            location.z(),
            location.getPitch(),
            location.getYaw(),
            net.minecraft.world.entity.EntityType.PLAYER,
            0,
            Vec3.ZERO,
            location.getYaw()
        )));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder setPlayerTeam(@Nonnull Team team, @Nonnull TeamMode teamMode) {
        var nmsTeam = ((CraftTeam)team).getHandle();
        var packet = switch (teamMode){
            case CREATE_TEAM, UPDATE_TEAM_INFO -> ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(nmsTeam, true);
            case REMOVE_TEAM -> ClientboundSetPlayerTeamPacket.createRemovePacket(nmsTeam);
            case ADD_PLAYERS_TO_TEAM, REMOVE_PLAYERS_FROM_TEAM -> {
                var operation = (teamMode == TeamMode.ADD_PLAYERS_TO_TEAM) ? Action.ADD : Action.REMOVE;
                yield ClientboundSetPlayerTeamPacket.createPlayerPacket(nmsTeam, "", operation);
            }
        };
        initial.setNext(of(packet));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder teleportEntity(int entityId, @Nonnull Location location, boolean onGround) {
        var transition = new TeleportTransition(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toVec3(location), Vec3.ZERO, location.getPitch(), location.getYaw(), Set.of(), TeleportTransition.DO_NOTHING, PlayerTeleportEvent.TeleportCause.PLUGIN);
        ClientboundTeleportEntityPacket teleportEntityPacket = ClientboundTeleportEntityPacket.teleport(entityId, PositionMoveRotation.of(transition), Set.of(), onGround);
        initial.setNext(of(teleportEntityPacket));
        return this;
    }

    @Override
    public @NotNull PacketStepBuilder mountEntity(int entityId, int @NotNull ... passenger) {
        ClientboundSetPassengersPacket passengersPacket = new ClientboundSetPassengersPacket(entityId, passenger);
        initial.setNext(of(passengersPacket));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder sendBlockChange(@Nonnull Location location, @Nonnull BlockData blockData) {
        initial.setNext(new ChainablePacketStep((player) -> player.sendBlockChange(location, blockData)));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder sleepAt(int entityId, @Nonnull Location location) {
        List<SynchedEntityData.DataValue<?>> dataWatcherItems = new ArrayList<>();
        dataWatcherItems.add(new SynchedEntityData.DataItem<>(Entity.DATA_POSE, Pose.SLEEPING).value());
        dataWatcherItems.add(new SynchedEntityData.DataItem<>(LivingEntity.SLEEPING_POS_ID,
            Optional.of(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()))).value()
        );
        ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(entityId, dataWatcherItems);
        initial.setNext(of(meta));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder rotateHead(int entityId, float yaw) {
        initial.setNext(of(new ClientboundRotateHeadPacket(
            entityId,
            (byte) ((int) (yaw * 256.0F / 360.0F))
        )));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder rotateFullHead(int entityId, float yaw, float pitch, boolean onGround) {
        initial.setNext(of(new ClientboundMoveEntityPacket.Rot(
            entityId,
            (byte) ((int) (yaw * 256.0F / 360.0F)),
            (byte) ((int) (pitch * 256.0F / 360.0F)),
            onGround
        )));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder movePositionAndRotateFullHead(int entityId, @Nonnull Vector direction, byte yaw, byte pitch, boolean onGround) {
        initial.setNext(of(new ClientboundMoveEntityPacket.PosRot(
            entityId,
            (short) direction.getX(),
            (short) direction.getY(),
            (short) direction.getZ(),
            (byte) ((int) (yaw * 256.0F / 360.0F)),
            (byte) ((int) (pitch * 256.0F / 360.0F)),
            onGround)));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder destroyEntity(int entityId) {
        initial.setNext(of(new ClientboundRemoveEntitiesPacket(entityId)));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder animateBlockBreak(int entityID, @Nonnull Location location, byte destroyStage) {
        initial.setNext(of(new ClientboundBlockDestructionPacket(entityID,
            new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), destroyStage)));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder setEquipmentItem(int entityId, @Nonnull org.bukkit.inventory.EquipmentSlot equipmentSlot, @Nonnull ItemStack itemStack) {
        initial.setNext(of(new ClientboundSetEquipmentPacket(entityId, Collections.singletonList(new Pair<>(from(equipmentSlot), CraftItemStack.asNMSCopy(itemStack))))));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder delay(@Nonnull JavaPlugin javaPlugin, long time, @Nonnull TimeUnit timeUnit) {
        initial.setNext(new DelayPacketStep(javaPlugin, time, timeUnit));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder custom(@Nonnull PacketStep packetStep) {
        initial.setNext(new ChainablePacketStep(packetStep));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder removeEntity(int entityId) {
        initial.setNext(of(new ClientboundRemoveEntitiesPacket(entityId)));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder animateEntity(int entityId, @Nonnull Animation animation) {
        initial.setNext(of(new ClientboundAnimatePacket(entityId, CraftPacketConversion.fromAnimation(animation))));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder spawnEntity(int entityId, @Nonnull UUID uuid, @Nonnull Location location, @Nonnull org.bukkit.entity.EntityType entityType, @Nonnull Vector movementDirection) {
        initial.setNext(of(new ClientboundAddEntityPacket(
            entityId,
            uuid,
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch(),
            BuiltInRegistries.ENTITY_TYPE.getOptional(org.bukkit.craftbukkit.util.CraftNamespacedKey.toMinecraft(entityType.getKey())).orElse(net.minecraft.world.entity.EntityType.ARMOR_STAND),
            1,
            new Vec3(movementDirection.getX(), movementDirection.getY(), movementDirection.getZ()),
            0
        )));
        return this;
    }

    @Override
    @Nonnull
    public PacketStepBuilder withMeta(int entityId, @Nonnull EntityMetaBuilder metaBuilder) {
        initial.setNext(
            of(new ClientboundSetEntityDataPacket(entityId,
                ((CraftEntityMetaBuilder) metaBuilder).build().stream().map(SynchedEntityData.DataItem::value).collect(Collectors.toList())))
        );
        return this;
    }

    @Override
    public @NotNull PacketStepBuilder playerAbilities(@Nonnull Set<PlayerAbility> playerAbilities, float flySpeed, float fieldOfViewModifier) {
        Abilities playerAbilitiesNMS = CraftPacketConversion.mapPlayerAbilitiesOld(playerAbilities);
        playerAbilitiesNMS.flyingSpeed = flySpeed;
        playerAbilitiesNMS.walkingSpeed = fieldOfViewModifier;
        initial.setNext(of(new ClientboundPlayerAbilitiesPacket(playerAbilitiesNMS)));
        return this;
    }

    @Override
    public @NotNull PacketStepBuilder withPlayerAbilities(@NotNull Set<com.github.weepingmc.packet.options.abilities.PlayerAbility> playerAbilities) {
        Abilities playerAbilitiesNMS = mapPlayerAbilities(playerAbilities);
        initial.setNext(of(new ClientboundPlayerAbilitiesPacket(playerAbilitiesNMS)));
        return this;
    }

    @Override
    public @NotNull PacketStepBuilder showTestMarker(@NotNull Location location, @NotNull java.awt.Color color, @org.jetbrains.annotations.Nullable String text, int time) {
        /*var payload = new net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload(
            new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
            org.bukkit.Color.fromARGB(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue()).asARGB(),
            text==null?"": text,
            time
        );
        initial.setNext(of(new net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket(payload)));*/
        // todo: fixme
        return this;
    }

    @Override
    public void send(@Nonnull Collection<? extends org.bukkit.entity.Player> players) {
        initial.execute(players);
    }

    private ChainablePacketStep of(Packet<?> packet) {
        return new ChainablePacketStep(new NmsPacketStep(packet));
    }
}
