package com.github.weepingmc.packet.options.abilities;

import org.jspecify.annotations.NullMarked;

@NullMarked
public record WalkSpeedAbility(float speed) implements PlayerAbility{
}
