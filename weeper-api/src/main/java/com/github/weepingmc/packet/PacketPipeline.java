package com.github.weepingmc.packet;

import com.destroystokyo.paper.SkinParts;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import org.bukkit.scoreboard.Team;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface PacketPipeline {

    PacketStepBuilder create();

    EntityMetaBuilder createEntityMetaBuilder();

    /**
     * Creates a skin part builder
     *
     * @return skinpart builder
     * @deprecated Use {@link org.bukkit.Server#newSkinPartsBuilder()}
     */
    @Deprecated
    SkinParts.Builder createSkinPartsBuilder();

    Team createTeam(String name);

    String generateRandomString(int count, boolean letters, boolean numbers);
}
