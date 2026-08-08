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
    private static final HandlerList HANDLERS = new HandlerList();

    private final World world;
    @Nullable
    private Mob mob;

    /**
     * Creates a new world create entity event.
     *
     * @param world the world the entity is being created in
     * @param mob   the entity being created
     */
    public WorldCreateEntityEvent(World world, Mob mob) {
        this.world = world;
        this.mob = mob;
    }

    /**
     * Gets the list of handlers for this event.
     *
     * @return the handler list
     */
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Gets the entity being created.
     *
     * @return the entity
     */
    public @Nullable Mob getEntity() {
        return this.mob;
    }

    /**
     * Sets the entity being created.
     *
     * @param mob the entity
     */
    public void setEntity(@Nullable Mob mob) {
        this.mob = mob;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Gets the world the entity is being created in.
     *
     * @return the world
     */
    public World getWorld() {
        return this.world;
    }
}
