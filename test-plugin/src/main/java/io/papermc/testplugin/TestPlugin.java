package io.papermc.testplugin;


import com.destroystokyo.paper.SkinParts;
import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import com.destroystokyo.paper.entity.ai.MobGoals;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import io.papermc.paper.disguise.DisguiseData;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.util.EnumSet;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Mannequin;
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
                    .then(Commands.argument("pose", PoseArgument.pose())
                            .executes(context -> {
                                if (context.getSource().getExecutor() instanceof Player player) {
                                    var pose = context.getArgument("pose", Pose.class);

                                    player.getWorld().spawn(player.getLocation(), Zombie.class, (zombie) -> {
                                        zombie.setDisguiseData(DisguiseData.player(ResolvableProfile.resolvableProfile(player.getPlayerProfile()))
                                                .description(Component.text("Zombie Meme", NamedTextColor.RED))
                                                .pose(pose)
                                                .skinParts(SkinParts.allParts())
                                                .build()
                                        );
                                        zombie.setCustomNameVisible(true);
                                        zombie.customName(Component.text("ysl", NamedTextColor.RED));

                                        Bukkit.getMobGoals().addGoal(zombie, 1, new TestGoal(zombie));
                                    });
                                    player.sendMessage(Component.text("Test command executed!", NamedTextColor.GREEN));
                                }
                                return Command.SINGLE_SUCCESS;
                            })
                    )

                    .build());
        });


    }

    private static class PoseArgument implements CustomArgumentType.Converted<Pose, String> {

        public static PoseArgument pose() {
            return new PoseArgument();
        }

        private static final DynamicCommandExceptionType ERROR_INVALID = new DynamicCommandExceptionType(value -> {
            return MessageComponentSerializer.message().serialize(Component.text(value + " is not a valid type!"));
        });

        @Override
        public ArgumentType<String> getNativeType() {
            return StringArgumentType.word();
        }

        @Override
        public Pose convert(String nativeType) throws CommandSyntaxException {
            try {
                return Pose.valueOf(nativeType.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException ignored) {
                throw ERROR_INVALID.create(nativeType);
            }
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            for (Pose pose : Mannequin.validPoses()) {
                String name = pose.toString().toLowerCase(Locale.ROOT);

                if (name.startsWith(builder.getRemainingLowerCase())) {
                    builder.suggest(pose.toString());
                }
            }

            return builder.buildFuture();
        }
    }

    private static class TestGoal implements Goal<Zombie> {

        private final Zombie zombie;

        private TestGoal(Zombie zombie) {
            this.zombie = zombie;
        }


        @Override
        public boolean shouldActivate() {
            return true;
        }

        @Override
        public void tick() {
            zombie.getNearbyEntities(10, 10, 10).stream().filter(Player.class::isInstance)
                .forEach(player -> player.sendMessage(Component.text("Hello, I am a zombie!", NamedTextColor.RED)));
        }

        @Override
        public GoalKey<Zombie> getKey() {
            return GoalKey.of(Zombie.class, new NamespacedKey("test-plugin", "test-goal"));
        }

        @Override
        public EnumSet<GoalType> getTypes() {
            return EnumSet.of(GoalType.MOVE);
        }
    }

}
