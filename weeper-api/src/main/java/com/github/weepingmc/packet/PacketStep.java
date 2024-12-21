package com.github.weepingmc.packet;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface PacketStep {
    void execute(@NotNull Player player);
}
