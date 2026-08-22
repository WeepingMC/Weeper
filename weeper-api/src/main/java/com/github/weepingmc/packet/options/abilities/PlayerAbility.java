package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

/**
 * Represents an ability a player can have.
 */
@NullMarked
public sealed interface PlayerAbility permits AllowFlying, CreativeModeInstantBreak, WalkSpeedAbility, FlySpeedAbility, Flying, Invulnerable {
    /**
     * Ability to allow flying.
     */
    PlayerAbility ALLOW_FLYING = new AllowFlying();
    /**
     * Ability for creative mode instant break.
     */
    PlayerAbility CREATIVE_MODE_INSTANT_BREAK = new CreativeModeInstantBreak();
    /**
     * Ability for flying.
     */
    PlayerAbility FLYING = new Flying();
    /**
     * Ability for being invulnerable.
     */
    PlayerAbility INVULNERABLE = new Invulnerable();

    /**
     * Creates a fly speed ability.
     *
     * @param speed the fly speed
     * @return the player ability
     */
    static PlayerAbility flySpeedAbilityOf(float speed) {
        return new FlySpeedAbility(speed);
    }

    /**
     * Creates a walk speed ability.
     *
     * @param speed the walk speed
     * @return the player ability
     */
    static PlayerAbility walkSpeedAbilityOf(float speed) {
        return new WalkSpeedAbility(speed);
    }
}

