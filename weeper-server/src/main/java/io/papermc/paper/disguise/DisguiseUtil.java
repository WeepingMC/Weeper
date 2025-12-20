package io.papermc.paper.disguise;

import io.papermc.paper.adventure.PaperAdventure;
import io.papermc.paper.datacomponent.item.PaperResolvableProfile;
import io.papermc.paper.entity.meta.EntityTypeToEntityClass;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftEntityType;
import org.bukkit.entity.Pose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DisguiseUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DisguiseUtil.class);

    private DisguiseUtil() {
    }

    public static boolean tryDisguise(ServerPlayer player, Entity entity, Packet<?> packet) {
        if (!(packet instanceof ClientboundAddEntityPacket clientboundAddEntityPacket)) {
            return !(com.github.weepingmc.event.packet.PlayerTrackEntityEvent.getHandlerList().getRegisteredListeners().length == 0 || new com.github.weepingmc.event.packet.PlayerTrackEntityEvent(player.getBukkitEntity(), entity.getBukkitEntity()).callEvent()); // Weeper - player track entity events
        }
        return switch (entity.getBukkitEntity().getDisguiseData()) {
            case DisguiseData.OriginalDisguise disguise -> false;
            case EntityTypeDisguise(var type) -> {
                player.connection.send(create(clientboundAddEntityPacket, CraftEntityType.bukkitToMinecraft(type)));
                yield true;
            }
            case PlayerDisguise(var playerProfile, var skinParts, var description, var pose) -> {
                PaperResolvableProfile profile = (PaperResolvableProfile) playerProfile;

                player.connection.send(create(clientboundAddEntityPacket, EntityType.MANNEQUIN));

                var data = new ArrayList<SynchedEntityData.DataValue<?>>();
                data.add(new SynchedEntityData.DataItem<>(Mannequin.DATA_PROFILE, profile.getHandle()).value());

                if(entity.hasCustomName()) {
                    Component name = entity.getCustomName();
                    data.add(new SynchedEntityData.DataItem<>(Entity.DATA_CUSTOM_NAME, Optional.of(name)).value());
                }
                if(entity.isCustomNameVisible()) {
                    data.add(new SynchedEntityData.DataItem<>(Entity.DATA_CUSTOM_NAME_VISIBLE, true).value());
                }

                if (skinParts != null) {
                    data.add(new SynchedEntityData.DataItem<>(Mannequin.DATA_PLAYER_MODE_CUSTOMISATION, (byte) skinParts.getRaw()).value());
                }
                net.minecraft.world.entity.Pose internalPose = net.minecraft.world.entity.Pose.values()[pose.ordinal()];
                data.add(new SynchedEntityData.DataItem<>(Mannequin.DATA_POSE, internalPose).value());
                if (description != null) {
                    var vanillaDescription = PaperAdventure.asVanilla(description);
                    data.add(new SynchedEntityData.DataItem<>(Mannequin.DATA_DESCRIPTION, Optional.of(vanillaDescription)).value());
                }

                player.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket(
                                clientboundAddEntityPacket.getId(),
                                data
                        )
                );

                yield true;
            }
        };
    }

    /*
     * Only player disguise needs to be handled specially
     * because the client doesn't forget the player profile otherwise.
     * This would result in player being kicked cause the entities type mismatches the previously disguised one.
     */
    public static void tryDespawn(ServerPlayer player, Entity entity) {
        if (entity.getBukkitEntity().getDisguiseData() instanceof PlayerDisguise) {
            player.connection.send(new ClientboundPlayerInfoRemovePacket(List.of(entity.getUUID())));
        }
    }

    private static ClientboundAddEntityPacket create(ClientboundAddEntityPacket packet, EntityType<?> entityType) {
        return new ClientboundAddEntityPacket(
                packet.getId(),
                packet.getUUID(),
                packet.getX(),
                packet.getY(),
                packet.getZ(),
                packet.getXRot(),
                packet.getYRot(),
                entityType,
                0,
                Vec3.ZERO.add(packet.getX(), packet.getY(), packet.getZ()).scale(1 / 8000.0D),
                packet.getYHeadRot()
        );
    }

    private static final org.bukkit.NamespacedKey skinKey = new org.bukkit.NamespacedKey("npclib", "npclib-skin"); // Weeper - track entity events

    /*
     * Is used to skip entity meta that doesn't fit the disguised type.
     * e.g. Player having a float at index 15 (additional hearts) and the server side entity is an Armorstand
     * that has a byte at that index.
     */

    public static boolean shouldSkip(Entity entity, EntityDataAccessor<?> dataAccessor) {
        return shouldSkip(entity, dataAccessor.serializer(), dataAccessor.id());
    }

    public static boolean shouldSkip(Entity entity, EntityDataSerializer<?> entityDataSerializer, int id) {
        // Weeper start - track entity events
        if (entity.getBukkitEntity().getPersistentDataContainer().has(skinKey, org.bukkit.persistence.PersistentDataType.STRING)) {
            return !io.papermc.paper.entity.meta.EntityMetaWatcher.isValidForClass(ServerPlayer.class, entityDataSerializer, id);
        }
        // Weeper end - track entity events
        return switch (entity.getBukkitEntity().getDisguiseData()) {
            case DisguiseData.OriginalDisguise original -> false;
            case EntityTypeDisguise entityTypeDisguise ->
                    !io.papermc.paper.entity.meta.EntityMetaWatcher.isValidForClass(
                            EntityTypeToEntityClass.getClassByEntityType(entityTypeDisguise.entityType()),
                            entityDataSerializer, id
                    );
            case PlayerDisguise playerDisguise -> !io.papermc.paper.entity.meta.EntityMetaWatcher.isValidForClass(
                    ServerPlayer.class,
                    entityDataSerializer, id
            );
        };
    }

    public static List<SynchedEntityData.DataValue<?>> filter(Entity entity, List<SynchedEntityData.DataValue<?>> values) {
        List<SynchedEntityData.DataValue<?>> list = new ArrayList<>();
        for (SynchedEntityData.DataValue<?> value : values) {
            if (!shouldSkip(entity, value.serializer(), value.id())) {
                list.add(value);
            }
        }
        return list;
    }

    public static boolean shouldSkipAttributeSending(Entity entity) {
        return switch (entity.getBukkitEntity().getDisguiseData()) {
            case DisguiseData.OriginalDisguise original -> false;
            case EntityTypeDisguise entityTypeDisguise -> !entityTypeDisguise.entityType().hasDefaultAttributes();
            case PlayerDisguise playerDisguise -> false;
        };
    }

    public static boolean canSendAnimation(Entity entity) {
        return switch (entity.getBukkitEntity().getDisguiseData()) {
            case DisguiseData.OriginalDisguise original -> true;
            case EntityTypeDisguise entityTypeDisguise ->
                    LivingEntity.class.isAssignableFrom(EntityTypeToEntityClass.getClassByEntityType(entityTypeDisguise.entityType()));
            case PlayerDisguise playerDisguise -> true;
        };
    }
}
