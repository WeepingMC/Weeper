package com.github.weepingmc.offline;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import static org.bukkit.Bukkit.createProfile;

public class CraftOfflinePlayerEditor implements OfflinePlayerEditor {
    private final Map<UUID, ServerPlayer> offlineEditedPlayers = new ConcurrentHashMap<>();

    private final MinecraftServer server;

    public CraftOfflinePlayerEditor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void editOfflinePlayer(UUID uuid, Consumer<EditPlayer> consumer) {
        GameProfile playerProfile = ((CraftPlayerProfile) createProfile(uuid)).buildGameProfile();
        ServerPlayer serverPlayer = this.server.getPlayerList().getPlayer(uuid);

        if (serverPlayer == null) {
            serverPlayer = this.offlineEditedPlayers.get(uuid);
        }
        if (serverPlayer == null) {

            serverPlayer = new ServerPlayer(
                    this.server,
                    this.server.overworld(),
                    playerProfile,
                    ClientInformation.createDefault()
            );
            var target = serverPlayer.getBukkitEntity();
            target.loadData();
            this.offlineEditedPlayers.put(playerProfile.id(), serverPlayer);
        }
        // ensure cleanup if user fucks something up
        try {
            consumer.accept(new CraftEditPlayer(serverPlayer));
        } finally {
            serverPlayer.getBukkitEntity().saveData();
            this.offlineEditedPlayers.remove(playerProfile.id());
        }

    }

    public ServerPlayer createOrGet(MinecraftServer server, ServerLevel serverLevel, GameProfile gameProfile, ClientInformation clientInformation) {
        ServerPlayer entity = this.offlineEditedPlayers.remove(gameProfile.id());
        if (entity != null) {
            // saves data edited by offline player editing mechanic
            // server itself should load the playerdata some time later
            entity.gameProfile = gameProfile; // set original game profile to so we do not end up with a lot of empty name player.
            entity.getBukkitEntity().saveData();
        } else {
            entity = new ServerPlayer(server, serverLevel, gameProfile, clientInformation);
        }
        return entity;
    }
}
