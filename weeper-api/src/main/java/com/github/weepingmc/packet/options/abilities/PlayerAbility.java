package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

@NullMarked
public sealed interface PlayerAbility permits AllowFlying, CreativeModeInstantBreak, WalkSpeedAbility, FlySpeedAbility, Flying, Invulnerable {
    PlayerAbility ALLOW_FLYING = new AllowFlying();
    PlayerAbility CREATIVE_MODE_INSTANT_BREAK = new CreativeModeInstantBreak();
    PlayerAbility FLYING = new Flying();
    PlayerAbility INVULNERABLE = new Invulnerable();

    static PlayerAbility flySpeedAbilityOf(float speed) {
        return new FlySpeedAbility(speed);
    }

    static PlayerAbility walkSpeedAbilityOf(float speed) {
        return new WalkSpeedAbility(speed);
    }
}

