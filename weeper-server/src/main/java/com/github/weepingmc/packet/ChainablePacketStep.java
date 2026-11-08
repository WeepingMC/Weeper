package com.github.weepingmc.packet;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import net.minecraft.network.protocol.Packet;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.Nullable;

public class ChainablePacketStep implements PacketStep {

    protected @Nullable ChainablePacketStep next;
    private final @Nullable PacketStep current;

    public ChainablePacketStep() {
        this.current = null;
    }

    public ChainablePacketStep(PacketStep current) {
        this.current = current;
    }

    public void setNext(ChainablePacketStep next) {
        if (this.next == null) {
            this.next = next;
        } else {
            this.next.setNext(next);
        }
    }

    protected void executeNext(Collection<? extends Player> players) {
        if (this.next != null) {
            this.next.execute(players);
        }
    }

    public final void execute(Collection<? extends Player> players) {
        this.executeAll(players);
        this.executeNext(players);
    }

    protected void executeAll(Collection<? extends Player> players) {
        for (Player player : players) {
            this.execute(player);
        }
    }

    @Override
    public void execute(Player player) {
        if (this.current != null) {
            this.current.execute(player);
        }
    }

    public static class NmsPacketStep implements PacketStep {

        private final Packet<?> packet;

        public NmsPacketStep(Packet<?> packet) {
            this.packet = packet;
        }

        @Override
        public void execute(Player player) {
            this.sendPacket(player, this.packet);
        }

        private void sendPacket(Player player, Packet<?> packet) {
            ((CraftPlayer) player).getHandle().connection.send(packet);
        }
    }

    public static class DelayPacketStep extends ChainablePacketStep {

        private final JavaPlugin javaPlugin;
        private final long delay;
        private final TimeUnit timeUnit;

        public DelayPacketStep(JavaPlugin javaPlugin, long delay, TimeUnit timeUnit) {
            this.javaPlugin = javaPlugin;
            this.delay = delay;
            this.timeUnit = timeUnit;
        }

        @Override
        protected void executeNext(Collection<? extends Player> players) {
            Bukkit.getScheduler().runTaskLater(this.javaPlugin, () -> {
                super.executeNext(players);
            }, 20 * this.timeUnit.toSeconds(this.delay));
        }
    }
}
