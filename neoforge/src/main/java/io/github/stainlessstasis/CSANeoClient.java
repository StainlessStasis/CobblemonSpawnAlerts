package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.brigadier.arguments.StringArgumentType;

import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.commands.NeoForgeClientCommandHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.EvsUtil;
import io.github.stainlessstasis.util.GlowEffectManager;
import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@Mod(value = CobblemonSpawnAlerts.MOD_ID, dist = Dist.CLIENT)
public class CSANeoClient {
    public static boolean doesServerHaveMod = false;

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onInit(FMLClientSetupEvent event) {
            CobblemonSpawnAlerts.initClient();
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            // Register new v2.0 config commands
            NeoForgeClientCommandHandler.register(event.getDispatcher());
            
            // Keep legacy commands for backwards compatibility
            event.getDispatcher().register(
                    Commands.literal("cobblemonspawnalerts")
                            .then(Commands.literal("reload")
                                    .executes(ctx -> {
                                        return ClientCommandHandler.handleReloadCommand();
                                    })));
            event.getDispatcher().register(
                    Commands.literal("cobblemonspawnalerts")
                            .then(Commands.literal("openconfig")
                                    .executes(ctx -> {
                                        return ClientCommandHandler.handleOpenConfigCommand();
                                    })));
            
            // Register the glow command for highlighting Pokémon
            event.getDispatcher().register(
                    Commands.literal("csa_glow")
                            .then(Commands.argument("uuid", StringArgumentType.string())
                                    .executes(ClientCommandHandler::handleGlowCommand)));
        }

        @SubscribeEvent
        public static void onConnect(ClientPlayerNetworkEvent.LoggingIn event) {
            // Wait a bit for server mod detection, then check if warning should be shown
            Minecraft.getInstance().execute(() -> {
                new Thread(() -> {
                    try {
                        Thread.sleep(1000); // 1 second delay
                        Minecraft.getInstance().execute(() -> {
                            if (!Minecraft.getInstance().isSingleplayer() && 
                                CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().multiplayerWarning() && 
                                !doesServerHaveMod) {
                                MessageUtils.sendTranslated("cobblemon-spawn-alerts.multiplayer_warning");
                            }
                        });
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            });
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            AlertHandler.clearCache();
            EvsUtil.clearCache();
            GlowEffectManager.clearAllGlowEffects();
            doesServerHaveMod = false;
        }
        
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            GlowEffectManager.updateGlowEffects();
        }

        @SubscribeEvent
        public static void onEntityLoad(EntityJoinLevelEvent event) {
            if (!(event.getLevel().isClientSide)) {
                return;
            }

            if (event.getEntity() instanceof PokemonEntity pe && !doesServerHaveMod) {
                AlertHandler.alertClientside(pe);
            }
        }
    }

    /**
     * Sends a glow effect packet to the server
     */
    public static void sendGlowEffectPacket(io.github.stainlessstasis.network.GlowEffectPacket packet) {
        try {
            net.neoforged.neoforge.network.PacketDistributor.sendToServer(packet);
            if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                System.out.println("[CSA CLIENT] Successfully sent glow effect packet to server for UUID: " + packet.pokemonUUID());
                System.out.println("[CSA CLIENT] Packet duration: " + packet.durationSeconds() + " seconds");
            }
        } catch (Exception e) {
            if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput()) {
                System.out.println("[CSA CLIENT] Failed to send glow effect packet: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
