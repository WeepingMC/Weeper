package io.papermc.testplugin;


import com.destroystokyo.paper.SkinParts;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.disguise.DisguiseData;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TestPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(this, this);


        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(Commands.literal("test")
                    .requires(source -> source.getExecutor() instanceof Player)
                    .executes(context -> {
                        if (context.getSource().getExecutor() instanceof Player player) {
                            player.getWorld().spawn(player.getLocation(), Zombie.class, (zombie) -> {
                                zombie.setDisguiseData(DisguiseData.player(ResolvableProfile.resolvableProfile(player.getPlayerProfile()))
                                        .description(Component.text("Zombie Meme", NamedTextColor.RED))
                                        .pose(Pose.FALL_FLYING)
                                        .skinParts(SkinParts.allParts())
                                        .build()
                                );
                                zombie.setCustomNameVisible(true);
                                zombie.customName(Component.text("ysl", NamedTextColor.RED));
                            });
                            player.sendMessage(Component.text("Test command executed!", NamedTextColor.GREEN));
                        }
                        return Command.SINGLE_SUCCESS;
                    })
                    .build());
        });


    }


}
