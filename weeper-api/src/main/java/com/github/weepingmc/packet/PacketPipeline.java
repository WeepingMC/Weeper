package com.github.weepingmc.packet;

import com.destroystokyo.paper.SkinParts;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public interface PacketPipeline {

    @NotNull
    PacketStepBuilder create();

    @NotNull
    EntityMetaBuilder createEntityMetaBuilder();

    /**
     * Creates a skin part builder
     *
     * @return skinpart builder
     * @deprecated Use {@link org.bukkit.Server#newSkinPartsBuilder()}
     */
    @NotNull
    @Deprecated
    SkinParts.Builder createSkinPartsBuilder();

    @NotNull
    Team createTeam(@NotNull String name);

    @NotNull
    String generateRandomString(int count, boolean letters, boolean numbers);
}
