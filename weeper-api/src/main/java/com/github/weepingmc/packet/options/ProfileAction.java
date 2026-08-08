package com.github.weepingmc.packet.options;

/**
 * Represents the actions that can be performed on a player profile.
 */
public enum ProfileAction {
    /**
     * Adds a player to the tab list.
     */
    ADD_PLAYER,
    /**
     * Initializes chat for the player.
     */
    INITIALIZE_CHAT,
    /**
     * Updates the game mode of the player.
     */
    UPDATE_GAME_MODE,
    /**
     * Updates whether the player is listed in the tab list.
     */
    UPDATE_LISTED,
    /**
     * Updates the latency (ping) of the player.
     */
    UPDATE_LATENCY,
    /**
     * Updates the display name of the player.
     */
    UPDATE_DISPLAY_NAME
}
