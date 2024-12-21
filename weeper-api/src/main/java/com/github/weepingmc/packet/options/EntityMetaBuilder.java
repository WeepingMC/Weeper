package com.github.weepingmc.packet.options;

import com.destroystokyo.paper.SkinParts;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.NotNull;

public interface EntityMetaBuilder {
    @NotNull
    EntityMetaBuilder withEntityStatus(@NotNull Set<EntityStatus> entityStatus);

    @NotNull
    EntityMetaBuilder withCustomName(@NotNull BaseComponent[] baseComponents);

    @NotNull
    EntityMetaBuilder withCustomName(@NotNull Component component);

    @NotNull
    EntityMetaBuilder withSkinParts(@NotNull SkinParts skinParts);
}
