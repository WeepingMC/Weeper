package com.github.weepingmc.packet.options;

import com.destroystokyo.paper.profile.PlayerProfile;

public enum ProfileAction {
    ADD_PLAYER,
    INITIALIZE_CHAT,
    UPDATE_GAME_MODE,
    UPDATE_LISTED,
    UPDATE_LATENCY,
    UPDATE_DISPLAY_NAME,
    /**
     * @deprecated there is a separate packet now use {@link com.github.weepingmc.packet.PacketStepBuilder#removePlayerProfile(PlayerProfile)}
     */
    @Deprecated(forRemoval = true, since = "1.19.3")
    @org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    REMOVE_PLAYER;
}
