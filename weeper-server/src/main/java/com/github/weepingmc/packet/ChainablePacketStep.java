package com.github.weepingmc.packet;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class ChainablePacketStep implements PacketStep {

    protected ChainablePacketStep next;
    private final PacketStep current;

    public ChainablePacketStep(){
        this.current = null;
    }
    public ChainablePacketStep(PacketStep current) {
        this.current = current;
    }

    public void setNext(@Nonnull ChainablePacketStep next) {
        if (this.next == null) {
            this.next = next;
        } else {
            this.next.setNext(next);
        }
    }

    protected void executeNext(@Nonnull Collection<? extends Player> players) {
        if (next != null) {
            next.execute(players);
        }
    }

    public final void execute(@Nonnull Collection<? extends Player> players) {
        executeAll(players);
        executeNext(players);
    }

    protected void executeAll(@Nonnull Collection<? extends Player> players) {
        for (Player player : players) {
            execute(player);
        }
    }

    @Override
    public void execute(@Nonnull Player player) {
        if(current != null) current.execute(player);
    }

    public static class NmsPacketStep implements PacketStep {

        private final Packet<?> packet;

        public NmsPacketStep(Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void execute(@Nonnull Player player) {
            sendPacket(player, packet);
        }

        private void sendPacket(Player player, Packet<?> packet) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }
    }

    public static class DelayPacketStep extends ChainablePacketStep {

        private final JavaPlugin javaPlugin;
        private final long delay;
        private final TimeUnit timeUnit;

        public DelayPacketStep(@Nonnull JavaPlugin javaPlugin, long delay,
            @Nonnull TimeUnit timeUnit) {
            super();
            this.javaPlugin = javaPlugin;
            this.delay = delay;
            this.timeUnit = timeUnit;
        }

        @Override
        protected void executeNext(@NotNull Collection<? extends Player> players) {
            Bukkit.getScheduler().runTaskLater(javaPlugin, () -> {
                super.executeNext(players);
            }, 20 * timeUnit.toSeconds(delay));
        }
    }
}
