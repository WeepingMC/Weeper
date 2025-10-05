package com.github.weepingmc.packet.options;

import com.destroystokyo.paper.SkinParts;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface EntityMetaBuilder {
    EntityMetaBuilder withEntityStatus(Set<EntityStatus> entityStatus);

    /**
     * Setting the custom name for the fake entity
     *
     * @param baseComponents the bungee component containing the name
     * @return self
     */
    @Deprecated
    EntityMetaBuilder withCustomName(BaseComponent[] baseComponents);

    EntityMetaBuilder withCustomName(Component component);

    EntityMetaBuilder withSkinParts(SkinParts skinParts);
}
