package com.github.weepingmc.event.packet;

import io.papermc.paper.disguise.DisguiseData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jspecify.annotations.NullMarked;

/**
 * Is called before a {@link Player} tracks an {@link Entity}.
 * <p>
 * E.g. can be used for the following:
 *
 * <ul>
 *     <li>Hiding all kind of entities by calling {@link Cancellable#setCancelled(boolean)} with value true</li>
 *     <li>Disguising entities as others e.g. player npc.
 *         Cancel the event and replace it with a playerpacket with the same entity id.
 *     </li>
 * </ul>
 * @deprecated for entity disguising use {@link Entity#setDisguiseData(DisguiseData)}
 * for cancellation {@link io.papermc.paper.event.player.PlayerTrackEntityEvent}
 */
@Deprecated(forRemoval = true, since = "1.21.3")
@org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.24.0")
@NullMarked
public class PlayerTrackEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Entity entity;
    private boolean cancelled;

    public PlayerTrackEntityEvent(Player player, Entity entity) {
        super(player);
        this.entity = entity;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Gets the entity that will be tracked
     * @return the entity tracked
     */
    public Entity getEntity() {
        return entity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
