package io.github.stainlessstasis;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.GlowEffectManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class ClientCommandHandler {
    
    public static int handleReloadCommand() {
        CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.reload();
        return 1;
    }

    public static int handleOpenConfigCommand() {
        ClientConfigManager.openDirectory();
        return 1;
    }

    public static int handleGlowCommand(CommandContext<?> ctx) {
        try {
            // Check if server has the mod first
            if (!io.github.stainlessstasis.platform.Services.PLATFORM.doesServerHaveMod()) {
                Player player = Minecraft.getInstance().player;
                if (player != null) {
                    Component errorMessage = ComponentUtil.convertFromAdventure("<red>[CSA]</red> <yellow>Glow effects are not available - the server does not have CobblemonSpawnAlerts installed.</yellow>");
                    player.sendSystemMessage(errorMessage);
                }
                if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                    System.out.println("[CSA] Glow command blocked - server doesn't have mod");
                }
                return 0;
            }
            
            String uuidString = StringArgumentType.getString(ctx, "uuid");
            UUID pokemonUUID = UUID.fromString(uuidString);
            
            // Debug output to see if command is being called
            if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                System.out.println("[CSA] Glow command called for UUID: " + uuidString);
            }
            
            boolean success = GlowEffectManager.applyGlowEffect(pokemonUUID);
            
            // Send feedback message to the player
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                if (success) {
                    Component successMessage = ComponentUtil.convertFromAdventure("<green>[CSA]</green> <white>Glow effect applied successfully!</white>");
                    player.sendSystemMessage(successMessage);
                    if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                        System.out.println("[CSA] Glow effect applied successfully");
                    }
                } else {
                    Component failMessage = ComponentUtil.convertFromAdventure("<red>[CSA]</red> <white>Failed to apply glow effect - Pokemon not found in world.</white>");
                    player.sendSystemMessage(failMessage);
                    if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                        System.out.println("[CSA] Failed to apply glow effect - Pokemon not found");
                    }
                }
            }
            
            return success ? 1 : 0;
        } catch (Exception e) {
            // Send error feedback to the player
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                Component errorMessage = ComponentUtil.convertFromAdventure("<red>[CSA]</red> <white>Error applying glow effect: " + e.getMessage() + "</white>");
                player.sendSystemMessage(errorMessage);
            }
            
            if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                System.out.println("[CSA] Error in glow command: " + e.getMessage());
                e.printStackTrace();
            }
            return 0;
        }
    }
}
