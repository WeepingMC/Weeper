package com.github.weepingmc.packet;

import com.destroystokyo.paper.SkinParts;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import com.github.weepingmc.packet.options.EntityStatus;
import io.papermc.paper.adventure.PaperAdventure;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.jspecify.annotations.Nullable;

import static com.github.weepingmc.packet.CraftPacketConversion.mapStatus;

public class CraftEntityMetaBuilder implements EntityMetaBuilder {

    private final List<SynchedEntityData.DataItem<?>> dataWatcherList = new ArrayList<>();

    private final ServerLevel serverLevel;

    public CraftEntityMetaBuilder(ServerLevel level) {
        this.serverLevel = level;
    }

    public List<SynchedEntityData.DataItem<?>> build() {
        return new ArrayList<>(this.dataWatcherList);
    }

    @Override
    public EntityMetaBuilder withEntityStatus(Set<EntityStatus> entityStatus) {
        this.dataWatcherList.add(new SynchedEntityData.DataItem<>(Entity.DATA_SHARED_FLAGS_ID, mapStatus(entityStatus)));
        return this;
    }

    public EntityMetaBuilder withSkinParts(SkinParts skinParts) {
        this.dataWatcherList.add(new SynchedEntityData.DataItem<>(Player.DATA_PLAYER_MODE_CUSTOMISATION, (byte) skinParts.getRaw()));
        return this;
    }

    @Override
    public EntityMetaBuilder withCustomName(@Nullable BaseComponent[] baseComponents) {
        return this.withCustomName(CraftChatMessage.bungeeToVanilla(baseComponents));
    }

    @Override
    public EntityMetaBuilder withCustomName(net.kyori.adventure.text.Component component) {
        return this.withCustomName(PaperAdventure.asVanilla(component));
    }

    private EntityMetaBuilder withCustomName(@Nullable Component component) {
        SynchedEntityData.DataItem<Optional<Component>> chat = new SynchedEntityData.DataItem<>(Entity.DATA_CUSTOM_NAME,
            Optional.ofNullable(component));
        this.dataWatcherList.add(chat);
        this.dataWatcherList.add(new SynchedEntityData.DataItem<>(Entity.DATA_CUSTOM_NAME_VISIBLE, true));
        return this;
    }
}
