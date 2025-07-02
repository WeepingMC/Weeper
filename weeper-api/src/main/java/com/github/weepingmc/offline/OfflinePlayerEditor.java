package com.github.weepingmc.offline;

import java.util.UUID;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;

@NullMarked
public interface OfflinePlayerEditor {
    /**
     * Edits the information of an offline player.
     *
     * @param playerUUID         The UUID of the offline player to edit.
     * @param editPlayerConsumer A consumer function that takes an {@link com.github.weepingmc.offline.EditPlayer} object as input,
     *                           allowing the caller to perform edits on the player's information.
     */
    void editOfflinePlayer(UUID playerUUID, Consumer<EditPlayer> editPlayerConsumer);
}
