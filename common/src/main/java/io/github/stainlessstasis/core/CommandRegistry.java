package io.github.stainlessstasis.core;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.util.ComponentUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.UUID;

public class CommandRegistry {
    public static void registerServerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("csa-server")
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
                        ComponentUtil.convertFromAdventure("<green>[CSA] </green><white>Server config reloading...</white>"));
                if (CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.loadConfig()) {
                    ctx.getSource().sendSystemMessage(
                            ComponentUtil.convertFromAdventure("<green>[CSA] </green><white>Server config reloaded!</white>"));
                } else {
                    ctx.getSource().sendSystemMessage(
                            ComponentUtil.convertFromAdventure("<green>[CSA] </green><red>Server config reload failed.</red>"));
                }
                return 1;
        })));
    }

    public static int handleReloadCommand() {
        CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.reload();
        return 1;
    }

    public static int handleOpenConfigCommand() {
        ClientConfigManager.openDirectory();
        return 1;
    }

    public static int handleGlowCommand(String uuid_) {
        UUID uuid = UUID.fromString(uuid_);
        if (CobblemonSpawnAlertsClient.glowing.contains(uuid)) {
            CobblemonSpawnAlertsClient.glowing.remove(uuid);
        } else {
            CobblemonSpawnAlertsClient.glowing.add(uuid);
        }

        return 1;
    }

    public static int handleGlowClearCommand() {
        CobblemonSpawnAlertsClient.glowing.clear();
        return 1;
    }
}
