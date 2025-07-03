package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
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

       ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) -> {
           dispatcher.register(
                   ClientCommandManager.literal("cobblemonspawnalerts")
                           .then(ClientCommandManager.literal("reload")
                                   .executes(ctx -> {
                                       return CommandRegistry.handleReloadCommand();
                                   })));
           dispatcher.register(
                   ClientCommandManager.literal("cobblemonspawnalerts")
                           .then(ClientCommandManager.literal("openconfig")
                                   .executes(ctx -> {
                                       return CommandRegistry.handleOpenConfigCommand();
                                   })));
       });


       // Packets
        ClientPlayNetworking.registerGlobalReceiver(PokemonDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                PacketHandlers.handlePokemonDataPacket(payload.pokemonNetworkID(), payload.ivs(), payload.evYield(), payload.nature());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AlertDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                PacketHandlers.handleAlertDataPacket(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(DespawnDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                PacketHandlers.handleDespawnDataPacket(payload);
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModLoadedPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                doesServerHaveMod = true;
            });
        });
    }

    private void onJoin(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
        if (!minecraft.isSingleplayer() && CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().multiplayerWarning()) {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.multiplayer_warning");
        }
    }

    private void onClientStop(Minecraft minecraft) {
        AlertHandler.clearCache();
        doesServerHaveMod = false;
    }

    private void onDisconnect(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        AlertHandler.clearCache();
        doesServerHaveMod = false;
    }

    private void onEntityLoad(Entity entity, ClientLevel clientLevel) {
        if (entity instanceof PokemonEntity pe && !doesServerHaveMod) {
            AlertHandler.alertClientside(pe);
        }
    }
}
