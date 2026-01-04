package com.github.weepingmc.event.world;

import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 *  Called when an entity is created. Not spawned.
 */
@NullMarked
public class WorldCreateEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final World world;
    @Nullable
    private Mob mob;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WorldCreateEntityEvent(World world, Mob mob) {
        this.world = world;
        this.mob = mob;
    }

    public @Nullable Mob getEntity() {
        return mob;
    }

    public void setEntity(@Nullable Mob mob) {
        this.mob = mob;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public World getWorld() {
        return world;
    }
}
