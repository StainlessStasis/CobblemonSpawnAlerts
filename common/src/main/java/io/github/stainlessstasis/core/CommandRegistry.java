package io.github.stainlessstasis.core;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stainlessstasis.util.ComponentUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandRegistry {
    public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("cobblemonspawnalerts-server")
            .then(Commands.literal("reload")
            .executes(ctx -> {
                if (!ctx.getSource().hasPermission(3)) {
                    if (ctx.getSource().getPlayer() != null) {
                        ctx.getSource().sendFailure(
                                ComponentUtil.convertFromAdventure("<red>You do not have permission to use this command!</red>"));
                    }

                    return 0;
                }

                ctx.getSource().sendSystemMessage(
                        ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><white>Server config reloading...</white>"));
                if (CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.loadConfig()) {
                    ctx.getSource().sendSystemMessage(
                            ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><white>Server config reloaded!</white>"));
                } else {
                    ctx.getSource().sendSystemMessage(
                            ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><red>Server config reload failed.</red>"));
                }
                return 1;
        })));
    }
}
