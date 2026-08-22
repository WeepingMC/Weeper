package com.github.weepingmc.packet.options;

/**
 * Represents the team mode for team packets.
 */
public enum TeamMode {
    /**
     * Creates a new team.
     */
    CREATE_TEAM,
    /**
     * Removes an existing team.
     */
    REMOVE_TEAM,
    /**
     * Updates information for an existing team.
     */
    UPDATE_TEAM_INFO,
    /**
     * Adds players to a team.
     */
    ADD_PLAYERS_TO_TEAM,
    /**
     * Removes players from a team.
     */
    REMOVE_PLAYERS_FROM_TEAM
}
