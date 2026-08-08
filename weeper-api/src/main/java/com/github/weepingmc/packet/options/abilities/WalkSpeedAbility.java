package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

/**
 * Represents the walk speed ability.
 *
 * @param speed the walk speed
 */
@NullMarked
public record WalkSpeedAbility(float speed) implements PlayerAbility {
}
