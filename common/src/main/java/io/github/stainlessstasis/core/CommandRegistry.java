package io.github.stainlessstasis.core;

import com.mojang.brigadier.CommandDispatcher;
import io.github.stainlessstasis.config.ClientConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class CommandRegistry {
    public static void registerCommonCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment) {
        dispatcher.register(
            Commands.literal("csa-common")
            .then(Commands.literal("reload")
            .executes(ctx -> {
                if (!ctx.getSource().hasPermission(3)) {
                    if (ctx.getSource().getPlayer() != null) {
                        ctx.getSource().sendFailure(
                                Component.literal("You do not have permission to use this command!").withStyle(ChatFormatting.RED));
                    }

                    return 0;
                }

                ctx.getSource().sendSystemMessage(
                        Component.literal("[CSA] ").withStyle(ChatFormatting.GREEN)
                                .append(Component.literal("Common configs reloading...").withStyle(ChatFormatting.WHITE)));
                if (CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.loadConfig()) {
                    ctx.getSource().sendSystemMessage(
                            Component.literal("[CSA] ").withStyle(ChatFormatting.GREEN)
                                    .append(Component.literal("Common configs reloaded!").withStyle(ChatFormatting.WHITE)));
                } else {
                    ctx.getSource().sendSystemMessage(
                            Component.literal("[CSA] ").withStyle(ChatFormatting.GREEN)
                                    .append(Component.literal("Common configs reload failed.").withStyle(ChatFormatting.RED)));
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
        if (CobblemonSpawnAlertsClient.glowing.containsKey(uuid)) {
            CobblemonSpawnAlertsClient.glowing.remove(uuid);
        } else {
            CobblemonSpawnAlertsClient.glowing.put(uuid, 0xffffffff);
        }

        return 1;
    }

    public static int handleGlowClearCommand() {
        CobblemonSpawnAlertsClient.glowing.clear();
        return 1;
    }
}
