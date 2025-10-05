package com.github.weepingmc.event.packet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

/**
 * Is called before a {@link Player} untracks an {@link Entity}.
 * @deprecated use {@link io.papermc.paper.event.player.PlayerUntrackEntityEvent}
 */
@Deprecated(forRemoval = true, since = "1.21.3")
@org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.24.0")
@NullMarked
public class PlayerUntrackEntityEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private final long entityId;

    public PlayerUntrackEntityEvent(Player player, long entityId, @Nullable Entity entity) {
        super(player);
        this.entity = entity;
        this.entityId = entityId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the entity that will be untracked
     * @return the entity untracked or null if entity is already removed
     */
    @Nullable
    public Entity getEntity() {
        return entity;
    }

    /**
     * Returns the entity id that gets despawned
     *
     * @return entity id
     */
    public long getEntityId(){
        return entityId;
    }
}
