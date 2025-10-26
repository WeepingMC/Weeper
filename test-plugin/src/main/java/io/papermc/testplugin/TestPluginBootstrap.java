package io.papermc.testplugin;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.GameRules;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TestPluginBootstrap implements PluginBootstrap {

    @Override
    public void bootstrap(@NotNull BootstrapContext context) {

        LiteralCommandNode<CommandSourceStack> buildCommand = Commands.literal("testcmd")
                .requires(stack -> stack.getExecutor() instanceof Player)
                .executes(commandContext -> {
                    var sender = commandContext.getSource().getSender();

                    if (sender instanceof Player player) {
                        commandContext.getSource().getSender().sendRichMessage("<red>Test Command");

                        boolean b = Boolean.TRUE.equals(player.getWorld().getGameRuleValue(GameRules.LEADER_ZOMBIE_BONUS));
                        player.sendRichMessage("<green>GameRule Value: " + b);
                    }
                    return Command.SINGLE_SUCCESS;
                })
                .build();

        context.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(buildCommand);
        });

    }

}
