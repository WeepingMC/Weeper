package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record FlySpeedAbility(float speed) implements PlayerAbility{
}
