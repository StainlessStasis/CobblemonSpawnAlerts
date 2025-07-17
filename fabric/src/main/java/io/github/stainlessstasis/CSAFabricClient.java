package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.commands.ClientConfigCommandHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.util.EvsUtil;
import io.github.stainlessstasis.util.GlowEffectManager;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class CSAFabricClient implements ClientModInitializer {
    public static boolean doesServerHaveMod = false;

    @Override
    public void onInitializeClient() {
        CobblemonSpawnAlerts.initClient();

        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
        ClientPlayConnectionEvents.JOIN.register(this::onJoin);
        ClientEntityEvents.ENTITY_LOAD.register(this::onEntityLoad);
        
        // Register client tick event to manage glow effects
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            GlowEffectManager.updateGlowEffects();
        });

       ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
           // Register new v2.0 config commands
           ClientConfigCommandHandler.register(dispatcher);
           
           // Keep legacy commands for backwards compatibility
           dispatcher.register(
                   ClientCommandManager.literal("cobblemonspawnalerts")
                           .then(ClientCommandManager.literal("reload")
                                   .executes(ctx -> {
                                       return ClientCommandHandler.handleReloadCommand();
                                   })));
           dispatcher.register(
                   ClientCommandManager.literal("cobblemonspawnalerts")
                           .then(ClientCommandManager.literal("openconfig")
                                   .executes(ctx -> {
                                       return ClientCommandHandler.handleOpenConfigCommand();
                                   })));
           
           // Register the glow command for highlighting Pokémon
           dispatcher.register(
                   ClientCommandManager.literal("csa_glow")
                           .then(ClientCommandManager.argument("uuid", StringArgumentType.string())
                                   .executes(ClientCommandHandler::handleGlowCommand)));
       });


       // Packets
        ClientPlayNetworking.registerGlobalReceiver(PokemonDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPacketHandlers.handlePokemonDataPacket(payload.pokemonNetworkID(), payload.ivs(), payload.evYield(), payload.nature(), payload.ability());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AlertDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPacketHandlers.handleAlertDataPacket(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(DespawnDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientPacketHandlers.handleDespawnDataPacket(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModLoadedPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                doesServerHaveMod = true;
            });
        });
    }

    private void onJoin(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
        // Wait a bit for server mod detection, then check if warning should be shown
        minecraft.execute(() -> {
            // Schedule check after a brief delay to allow mod detection packet to arrive
            new Thread(() -> {
                try {
                    Thread.sleep(1000); // 1 second delay
                    minecraft.execute(() -> {
                        if (!minecraft.isSingleplayer() && 
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

    private void onClientStop(Minecraft minecraft) {
        AlertHandler.clearCache();
        EvsUtil.clearCache();
        GlowEffectManager.clearAllGlowEffects();
        doesServerHaveMod = false;
    }

    private void onDisconnect(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        AlertHandler.clearCache();
        EvsUtil.clearCache();
        GlowEffectManager.clearAllGlowEffects();
        doesServerHaveMod = false;
    }

    private void onEntityLoad(Entity entity, ClientLevel clientLevel) {
        if (entity instanceof PokemonEntity pe && !doesServerHaveMod) {
            AlertHandler.alertClientside(pe);
        }
    }

    /**
     * Sends a glow effect packet to the server
     */
    public static void sendGlowEffectPacket(GlowEffectPacket packet) {
        try {
            ClientPlayNetworking.send(packet);
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
