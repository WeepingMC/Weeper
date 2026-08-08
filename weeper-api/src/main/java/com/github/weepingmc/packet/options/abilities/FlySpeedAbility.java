package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

/**
 * Represents the fly speed ability.
 *
 * @param speed the fly speed
 */
@NullMarked
public record FlySpeedAbility(float speed) implements PlayerAbility {
}
