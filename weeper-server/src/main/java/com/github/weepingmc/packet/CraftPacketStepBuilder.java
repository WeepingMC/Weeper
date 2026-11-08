package com.github.weepingmc.packet;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.github.weepingmc.packet.ChainablePacketStep.DelayPacketStep;
import com.github.weepingmc.packet.ChainablePacketStep.NmsPacketStep;
import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.TeamMode;
import com.github.weepingmc.packet.options.abilities.PlayerAbility;
import com.google.common.base.Preconditions;
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
import net.minecraft.network.protocol.game.ClientboundSwingAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.protocol.game.VecDelta;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.scoreboard.CraftTeam;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;
import org.jspecify.annotations.Nullable;

import static com.github.weepingmc.packet.CraftPacketConversion.from;
import static com.github.weepingmc.packet.CraftPacketConversion.mapPlayerAbilities;

public class CraftPacketStepBuilder implements PacketStepBuilder {

    private final ChainablePacketStep initial = new ChainablePacketStep();

    @Override
    public PacketStepBuilder sendPlayerProfile(PlayerProfile playerProfile, ProfileAction profileAction, boolean listed) {
        GameProfile gameProfile = ((CraftPlayerProfile) playerProfile).getGameProfile();

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
        this.initial.setNext(this.of(clientboundPlayerInfoUpdatePacket));

        return this;
    }

    @Override
    public PacketStepBuilder removePlayerProfile(@Nullable PlayerProfile playerProfile) {
        Preconditions.checkArgument(playerProfile != null, "playerProfile cannot be null");
        Preconditions.checkArgument(playerProfile.getId() != null, "playerProfile id cannot be null");
        ClientboundPlayerInfoRemovePacket clientboundPlayerInfoRemovePacket = new ClientboundPlayerInfoRemovePacket(List.of(playerProfile.getId()));
        this.initial.setNext(this.of(clientboundPlayerInfoRemovePacket));
        return this;
    }

    @Override
    public PacketStepBuilder spawnPlayer(int entityId, UUID uuid, Location location) {
        this.initial.setNext(this.of(new ClientboundAddEntityPacket(
                entityId,
                uuid,
                location.x(),
                location.y(),
                location.z(),
                location.getPitch(),
                location.getYaw(),
                EntityTypes.PLAYER,
                0,
                Vec3.ZERO,
                location.getYaw()
        )));
        return this;
    }

    @Override
    public PacketStepBuilder setPlayerTeam(Team team, TeamMode teamMode) {
        var nmsTeam = ((CraftTeam) team).getHandle();
        var packet = switch (teamMode) {
            case CREATE_TEAM, UPDATE_TEAM_INFO -> ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(nmsTeam, true);
            case REMOVE_TEAM -> ClientboundSetPlayerTeamPacket.createRemovePacket(nmsTeam);
            case ADD_PLAYERS_TO_TEAM, REMOVE_PLAYERS_FROM_TEAM -> {
                var operation = (teamMode == TeamMode.ADD_PLAYERS_TO_TEAM) ? Action.ADD : Action.REMOVE;
                yield ClientboundSetPlayerTeamPacket.createPlayerPacket(nmsTeam, "", operation);
            }
        };
        this.initial.setNext(this.of(packet));
        return this;
    }

    @Override
    public PacketStepBuilder teleportEntity(int entityId, Location location, boolean onGround) {
        var transition = new TeleportTransition(((CraftWorld) location.getWorld()).getHandle(), CraftLocation.toVec3(location), Vec3.ZERO, location.getPitch(), location.getYaw(), Set.of(), TeleportTransition.DO_NOTHING);
        ClientboundTeleportEntityPacket teleportEntityPacket = ClientboundTeleportEntityPacket.teleport(entityId, PositionMoveRotation.of(transition), Set.of(), onGround);
        this.initial.setNext(this.of(teleportEntityPacket));
        return this;
    }

    @Override
    public PacketStepBuilder mountEntity(int entityId, int... passenger) {
        ClientboundSetPassengersPacket passengersPacket = new ClientboundSetPassengersPacket(entityId, passenger);
        this.initial.setNext(this.of(passengersPacket));
        return this;
    }

    @Override
    public PacketStepBuilder sendBlockChange(Location location, BlockData blockData) {
        this.initial.setNext(new ChainablePacketStep((player) -> player.sendBlockChange(location, blockData)));
        return this;
    }

    @Override
    public PacketStepBuilder sleepAt(int entityId, Location location) {
        List<SynchedEntityData.DataValue<?>> dataWatcherItems = new ArrayList<>();
        dataWatcherItems.add(new SynchedEntityData.DataItem<>(Entity.DATA_POSE, Pose.SLEEPING).value());
        dataWatcherItems.add(new SynchedEntityData.DataItem<>(LivingEntity.SLEEPING_POS_ID,
                Optional.of(new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()))).value()
        );
        ClientboundSetEntityDataPacket meta = new ClientboundSetEntityDataPacket(entityId, dataWatcherItems);
        this.initial.setNext(this.of(meta));
        return this;
    }

    @Override
    public PacketStepBuilder rotateHead(int entityId, float yaw) {
        this.initial.setNext(this.of(new ClientboundRotateHeadPacket(
                entityId,
                (byte) ((int) (yaw * 256.0f / 360.0f))
        )));
        return this;
    }

    @Override
    public PacketStepBuilder rotateFullHead(int entityId, float yaw, float pitch, boolean onGround) {
        this.initial.setNext(this.of(new ClientboundMoveEntityPacket.Rot(
                entityId,
                (byte) ((int) (yaw * 256.0f / 360.0f)),
                (byte) ((int) (pitch * 256.0f / 360.0f)),
                onGround
        )));
        return this;
    }

    @Override
    public PacketStepBuilder movePositionAndRotateFullHead(int entityId, Vector direction, byte yaw, byte pitch, boolean onGround) {
        this.initial.setNext(this.of(new ClientboundMoveEntityPacket.PosRot(
                entityId,
                new VecDelta.Linear((short) direction.getX(),
                (short) direction.getY(),
                (short) direction.getZ()),
                (byte) ((int) (yaw * 256.0f / 360.0f)),
                (byte) ((int) (pitch * 256.0f / 360.0f)),
                onGround)));
        return this;
    }

    @Override
    public PacketStepBuilder destroyEntity(int entityId) {
        this.initial.setNext(this.of(new ClientboundRemoveEntitiesPacket(entityId)));
        return this;
    }

    @Override
    public PacketStepBuilder animateBlockBreak(int entityID, Location location, byte destroyStage) {
        this.initial.setNext(this.of(new ClientboundBlockDestructionPacket(entityID,
                new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ()), destroyStage)));
        return this;
    }

    @Override
    public PacketStepBuilder setEquipmentItem(int entityId, EquipmentSlot equipmentSlot, ItemStack itemStack) {
        this.initial.setNext(this.of(new ClientboundSetEquipmentPacket(entityId, Collections.singletonList(new Pair<>(from(equipmentSlot), CraftItemStack.asNMSCopy(itemStack))))));
        return this;
    }

    @Override
    public PacketStepBuilder delay(JavaPlugin javaPlugin, long time, TimeUnit timeUnit) {
        this.initial.setNext(new DelayPacketStep(javaPlugin, time, timeUnit));
        return this;
    }

    @Override
    public PacketStepBuilder custom(PacketStep packetStep) {
        this.initial.setNext(new ChainablePacketStep(packetStep));
        return this;
    }

    @Override
    public PacketStepBuilder removeEntity(int entityId) {
        this.initial.setNext(this.of(new ClientboundRemoveEntitiesPacket(entityId)));
        return this;
    }

    @Override
    public PacketStepBuilder animateEntity(int entityId, Animation animation) {
        var packect = switch (animation) {
            case SWING_MAIN_HAND -> new ClientboundSwingAnimationPacket(entityId, InteractionHand.MAIN_HAND, SwingAnimation.DEFAULT);
            case LEAVE_BED -> new ClientboundAnimatePacket(entityId, ClientboundAnimatePacket.WAKE_UP);
            case SWING_OFFHAND -> new ClientboundSwingAnimationPacket(entityId, InteractionHand.OFF_HAND, SwingAnimation.DEFAULT);
            case CRITICAL_HIT, MAGIC_CRITICAL_EFFECT -> new ClientboundAnimatePacket(entityId, CraftPacketConversion.fromAnimation(animation));
        };
        this.initial.setNext(this.of(packect));
        return this;
    }

    @Override
    public PacketStepBuilder spawnEntity(int entityId, UUID uuid, Location location, EntityType entityType, Vector movementDirection) {
        this.initial.setNext(this.of(new ClientboundAddEntityPacket(
                entityId,
                uuid,
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                BuiltInRegistries.ENTITY_TYPE.getOptional(CraftNamespacedKey.toMinecraft(entityType.getKey())).orElse(EntityTypes.ARMOR_STAND),
                1,
                new Vec3(movementDirection.getX(), movementDirection.getY(), movementDirection.getZ()),
                0
        )));
        return this;
    }

    @Override
    public PacketStepBuilder withMeta(int entityId, EntityMetaBuilder metaBuilder) {
        this.initial.setNext(
                this.of(new ClientboundSetEntityDataPacket(entityId,
                        ((CraftEntityMetaBuilder) metaBuilder).build().stream().map(SynchedEntityData.DataItem::value).collect(Collectors.toList())))
        );
        return this;
    }

    @Override
    public PacketStepBuilder withPlayerAbilities(Set<PlayerAbility> playerAbilities) {
        Abilities playerAbilitiesNMS = mapPlayerAbilities(playerAbilities);
        this.initial.setNext(this.of(new ClientboundPlayerAbilitiesPacket(playerAbilitiesNMS)));
        return this;
    }

    @Override
    public void send(Collection<? extends Player> players) {
        this.initial.execute(players);
    }

    private ChainablePacketStep of(Packet<?> packet) {
        return new ChainablePacketStep(new NmsPacketStep(packet));
    }
}
