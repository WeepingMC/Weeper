package io.papermc.testplugin;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

public final class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);
    }


    @EventHandler
    public void onSpawn(PlayerInteractEvent playerInteractEvent) {
        if (playerInteractEvent.getAction() == Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (!playerInteractEvent.hasItem()) {
            return;
        }
        var stack = playerInteractEvent.getItem();
        if (!Tag.WOOL.isTagged(stack.getType())) {
            return;
        }

        Location clicked = playerInteractEvent.getClickedBlock().getLocation();

        spawnTextMarker(playerInteractEvent.getPlayer(), clicked, Component.text("Hellow Dude", NamedTextColor.GREEN), 2000);
        spawnGlassCube(playerInteractEvent.getPlayer(), clicked, DyeColor.BROWN, 2000);


        playerInteractEvent.setCancelled(true);
    }

    private void spawnTextMarker(Player p, Location clicked, Component text, int durationMs) {
        World w = p.getWorld();

        TextDisplay td = w.spawn(clicked.toBlockLocation().add(0.5,1.2, 0.5), TextDisplay.class, d -> {
            d.text(text);
            d.setBillboard(Display.Billboard.CENTER);     // always face the player
            d.setSeeThrough(true);
            d.setShadowed(true);
            d.setLineWidth(200);
            // translucent dark background (A,R,G,B) — Paper API
            d.setBackgroundColor(Color.fromARGB(112, 0, 0, 0)); // ~44% black
            d.setGlowing(true); // optional glow
        });

        long ticks = Math.max(1, durationMs / 50);
        Bukkit.getScheduler().runTaskLater(this, td::remove, ticks);
    }

    private void spawnGlassCube(Player p, Location clicked, DyeColor color, int durationMs) {
        World w = p.getWorld();

        Material mat = stainedFromNamed(color);
        BlockData data = Bukkit.createBlockData(mat);

        BlockDisplay bd = w.spawn(clicked.toBlockLocation(), BlockDisplay.class, d -> {
            d.setBlock(data);
            d.setBrightness(new Display.Brightness(15, 15)); // vivid
            // Slight scale-up to avoid z-fighting with existing blocks
            Transformation t = d.getTransformation();
            t.getScale().set(1.01f, 1.01f, 1.03f);
            d.setTransformation(t);
            d.setGlowing(true); // optional
        });

        long ticks = Math.max(1, durationMs / 50);
        Bukkit.getScheduler().runTaskLater(this, bd::remove, ticks);
    }

    private static Material stainedFromNamed(DyeColor color) {
        return switch (color) {
            case WHITE -> Material.WHITE_STAINED_GLASS;
            case ORANGE -> Material.ORANGE_STAINED_GLASS;
            case MAGENTA -> Material.MAGENTA_STAINED_GLASS;
            case LIGHT_BLUE -> Material.LIGHT_BLUE_STAINED_GLASS;
            case YELLOW -> Material.YELLOW_STAINED_GLASS;
            case LIME -> Material.LIME_STAINED_GLASS;
            case PINK -> Material.PINK_STAINED_GLASS;
            case GRAY -> Material.GRAY_STAINED_GLASS;
            case LIGHT_GRAY -> Material.LIGHT_GRAY_STAINED_GLASS;
            case CYAN -> Material.CYAN_STAINED_GLASS;
            case PURPLE -> Material.PURPLE_STAINED_GLASS;
            case BLUE -> Material.BLUE_STAINED_GLASS;
            case BROWN -> Material.BROWN_STAINED_GLASS;
            case GREEN -> Material.GREEN_STAINED_GLASS;
            case RED -> Material.RED_STAINED_GLASS;
            case BLACK -> Material.BLACK_STAINED_GLASS;
        };
    }
}
