package com.github.weepingmc.packet;

import com.destroystokyo.paper.SkinParts;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;

/**
 * Some api handling packets.
 */
@NullMarked
public interface PacketPipeline {

    /**
     * Creates a new packet step builder.
     *
     * @return a new instance
     */
    PacketStepBuilder create();

    /**
     * Creates a entity metadata builder.
     *
     * @return a new instance
     */
    EntityMetaBuilder createEntityMetaBuilder();

    /**
     * Creates a skin part builder.
     *
     * @return skinpart builder
     * @deprecated Use {@link org.bukkit.Server#newSkinPartsBuilder()}
     */
    @ApiStatus.ScheduledForRemoval(inVersion = "26.2")
    @Deprecated(forRemoval = true)
    SkinParts.Builder createSkinPartsBuilder();

    Team createTeam(String name);

    String generateRandomString(int count, boolean letters, boolean numbers);
}
