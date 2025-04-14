package com.github.weepingmc.packet;


import com.destroystokyo.paper.PaperSkinParts;
import com.destroystokyo.paper.SkinParts.Builder;
import com.github.weepingmc.packet.options.EntityMetaBuilder;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

public class CraftPacketPipeline implements PacketPipeline {

    private final Supplier<ServerLevel> serverLevel;

    public CraftPacketPipeline(Supplier<ServerLevel> level) {
        serverLevel = level;
    }

    @Override
    @Nonnull
    public PacketStepBuilder create() {
        return new CraftPacketStepBuilder();
    }

    @Override
    @Nonnull
    public EntityMetaBuilder createEntityMetaBuilder() {
        return new CraftEntityMetaBuilder(serverLevel.get());
    }

    @Override
    public @NotNull Builder createSkinPartsBuilder() {
        return PaperSkinParts.builder();
    }

    @Override
    @Nonnull
    public Team createTeam(@Nonnull String name) {
        Scoreboard scoreboard = new Scoreboard();
        CraftScoreboard craftScoreboard = new CraftScoreboard(scoreboard);
        return craftScoreboard.registerNewTeam(name);
    }

    @Override
    @Nonnull
    public String generateRandomString(int count, boolean letters, boolean numbers) {
        return RandomStringUtils.secure().next(count, letters, numbers);
    }
}
