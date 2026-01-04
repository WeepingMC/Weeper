package com.github.weepingmc.event.world;

import java.util.Objects;
import org.bukkit.World;
import org.bukkit.entity.Mob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jspecify.annotations.NullMarked;

/**
 *  Called when an entity is created. Not spawned.
 */
@NullMarked
public class WorldCreateEntityEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final World world;
    private Mob mob;

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public WorldCreateEntityEvent(World world, Mob mob) {
        this.world = world;
        this.mob = mob;
    }

    public Mob getEntity() {
        return mob;
    }

    public void setEntity(Mob mob) {
        Objects.requireNonNull(mob, "Mob cannot be null");
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
