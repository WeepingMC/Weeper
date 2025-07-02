package com.github.weepingmc.offline;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.mojang.authlib.GameProfile;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerPlayer;

import static org.bukkit.Bukkit.createProfile;

public class CraftOfflinePlayerEditor implements OfflinePlayerEditor {
    private final Map<UUID, ServerPlayer> offlineEditedPlayers = new java.util.concurrent.ConcurrentHashMap<>();

    private final MinecraftServer server;

    public CraftOfflinePlayerEditor(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void editOfflinePlayer(UUID playerUUID, Consumer<EditPlayer> editPlayerConsumer) {
        GameProfile playerProfile = ((CraftPlayerProfile) createProfile(playerUUID)).buildGameProfile();
        ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerUUID);

        if (serverPlayer == null) {
            serverPlayer = offlineEditedPlayers.get(playerUUID);
        }
        if (serverPlayer == null) {

            serverPlayer = new ServerPlayer(
                    this.server,
                    this.server.overworld(),
                    playerProfile,
                    net.minecraft.server.level.ClientInformation.createDefault()
            );
            var target = serverPlayer.getBukkitEntity();
            target.loadData();
            offlineEditedPlayers.put(playerProfile.getId(), serverPlayer);
        }
        // ensure cleanup if user fucks something up
        try {
            editPlayerConsumer.accept(new com.github.weepingmc.offline.CraftEditPlayer(serverPlayer));
        } finally {
            serverPlayer.getBukkitEntity().saveData();
            offlineEditedPlayers.remove(playerProfile.getId());
        }

    }

    public ServerPlayer createOrGet(MinecraftServer server, GameProfile gameProfile, ClientInformation clientInformation) {
        ServerPlayer entity = offlineEditedPlayers.remove(gameProfile.getId());
        if (entity != null) {
            // saves data edited by offline player editing mechanic
            // server itself should load the playerdata some time later
            entity.gameProfile = gameProfile; // set original game profile to so we do not end up with a lot of empty name player.
            entity.getBukkitEntity().saveData();
        } else {
            entity = new ServerPlayer(server, server.overworld(), gameProfile, clientInformation);
        }
        return entity;
    }
}
