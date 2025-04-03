package com.github.weepingmc.packet;

import com.github.weepingmc.packet.options.Animation;
import com.github.weepingmc.packet.options.EntityStatus;
import com.github.weepingmc.packet.options.PlayerAbility;
import com.github.weepingmc.packet.options.ProfileAction;
import com.github.weepingmc.packet.options.abilities.AllowFlying;
import com.github.weepingmc.packet.options.abilities.CreativeModeInstantBreak;
import com.github.weepingmc.packet.options.abilities.FlySpeedAbility;
import com.github.weepingmc.packet.options.abilities.Flying;
import com.github.weepingmc.packet.options.abilities.Invulnerable;
import com.github.weepingmc.packet.options.abilities.WalkSpeedAbility;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public final class CraftPacketConversion {

    private CraftPacketConversion() {
    }

    static ClientboundPlayerInfoUpdatePacket.Action from(ProfileAction action) {
        return switch (action) {
            case ADD_PLAYER -> ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;
            case UPDATE_LATENCY -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LATENCY;
            case INITIALIZE_CHAT -> ClientboundPlayerInfoUpdatePacket.Action.INITIALIZE_CHAT;
            case UPDATE_GAME_MODE -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE;
            case UPDATE_DISPLAY_NAME -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME;
            case UPDATE_LISTED -> ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED;
            default -> throw new UnsupportedOperationException("Invalid case!");
        };
    }

    static EquipmentSlot from(org.bukkit.inventory.EquipmentSlot equipmentSlot) {
        return switch (equipmentSlot) {
            case HAND -> EquipmentSlot.MAINHAND;
            case OFF_HAND -> EquipmentSlot.OFFHAND;
            case FEET -> EquipmentSlot.FEET;
            case LEGS -> EquipmentSlot.LEGS;
            case CHEST -> EquipmentSlot.CHEST;
            case HEAD -> EquipmentSlot.HEAD;
            case BODY -> EquipmentSlot.BODY;
            case SADDLE -> EquipmentSlot.SADDLE;
        };
    }

    static List<Pair<EquipmentSlot, ItemStack>> from(
        EnumMap<org.bukkit.inventory.EquipmentSlot, org.bukkit.inventory.ItemStack> equipment) {
        List<Pair<EquipmentSlot, ItemStack>> pairList = new ArrayList<>();
        equipment.forEach((equipmentSlot, itemStack) -> {
            pairList.add(new Pair<>(from(equipmentSlot), CraftItemStack.asNMSCopy(itemStack)));
        });
        return pairList;
    }

    static byte mapStatus(Set<EntityStatus> entityStatuses) {

        byte status = 0;

        for (EntityStatus entityStatus : entityStatuses) {
            switch (entityStatus) {
                case IS_ON_FIRE -> status |= 0x01;
                case IS_CROUCHING -> status |= 0x02;
                case IS_SPRINTING -> status |= 0x08;
                case IS_SWIMMING -> status |= 0x10;
                case IS_INVISIBLE -> status |= 0x20;
                case HAS_GLOWING_EFFECT -> status |= 0x40;
                case IS_FLYING_WITH_ELYTRA -> status |= 0x80;
            }
        }
        return status;
    }

    static int fromAnimation(Animation animation) {
        return switch (animation) {
            case SWING_MAIN_ARM, SWING_MAIN_HAND -> ClientboundAnimatePacket.SWING_MAIN_HAND;
            case LEAVE_BED -> ClientboundAnimatePacket.WAKE_UP;
            case SWING_OFFHAND -> ClientboundAnimatePacket.SWING_OFF_HAND;
            case CRITICAL_EFFECT, TAKE_DAMAGE, CRITICAL_HIT -> ClientboundAnimatePacket.CRITICAL_HIT;
            case MAGIC_CRITICAL_EFFECT -> ClientboundAnimatePacket.MAGIC_CRITICAL_HIT;
        };
    }

    static Abilities mapPlayerAbilitiesOld(Set<PlayerAbility> playerAbilities) {
        Abilities playerAbilitiesNMS = new Abilities();

        for (PlayerAbility playerAbilitiy : playerAbilities) {
            switch (playerAbilitiy) {
                case INVULNERABLE -> playerAbilitiesNMS.invulnerable = true;
                case FLYING -> playerAbilitiesNMS.flying = true;
                case ALLOW_FLYING -> playerAbilitiesNMS.mayfly = true;
                case CREATIVE_MODE_INSTANT_BREAK -> playerAbilitiesNMS.instabuild = true;
            }
        }
        return playerAbilitiesNMS;
    }

    static Abilities mapPlayerAbilities(Set<com.github.weepingmc.packet.options.abilities.PlayerAbility> playerAbilities) {
        Abilities playerAbilitiesNMS = new Abilities();
        for (com.github.weepingmc.packet.options.abilities.PlayerAbility playerAbilitiy : playerAbilities) {
            switch (playerAbilitiy) {
                case AllowFlying flying -> playerAbilitiesNMS.mayfly = true;
                case CreativeModeInstantBreak creativeModeInstantBreak -> playerAbilitiesNMS.instabuild = true;
                case FlySpeedAbility(var speed) -> playerAbilitiesNMS.flyingSpeed = speed;
                case Flying flying -> playerAbilitiesNMS.flying = true;
                case Invulnerable invulnerable -> playerAbilitiesNMS.invulnerable = true;
                case WalkSpeedAbility(var speed) -> playerAbilitiesNMS.walkingSpeed = speed;
            }
        }
        return playerAbilitiesNMS;
    }
}
