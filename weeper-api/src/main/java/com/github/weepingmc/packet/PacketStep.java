package com.github.weepingmc.packet;

import org.bukkit.entity.Player;
import org.jspecify.annotations.NullMarked;

@FunctionalInterface
@NullMarked
public interface PacketStep {
    void execute(Player player);
}
