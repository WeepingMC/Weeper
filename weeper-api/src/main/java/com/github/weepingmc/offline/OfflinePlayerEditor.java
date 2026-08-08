package com.github.weepingmc.offline;

import java.util.UUID;
import java.util.function.Consumer;
import org.jspecify.annotations.NullMarked;

/**
 * The OfflinePlayerEditor.
 */
@NullMarked
public interface OfflinePlayerEditor {
    /**
     * Edits the information of an offline player.
     *
     * @param uuid         the UUID of the offline player to edit.
     * @param consumer           a consumer function that takes an EditPlayer object as input.
     */
    void editOfflinePlayer(UUID uuid, Consumer<EditPlayer> consumer);
}
