package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.mojang.brigadier.CommandDispatcher;
import io.github.stainlessstasis.core.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.ModLoadedPacket;
import io.github.stainlessstasis.network.PacketHandlers;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.Commands;
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
                PacketHandlers.handlePokemonDataPacket(payload.pokemonNetworkID(), payload.ivs(), payload.nature());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ModLoadedPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                doesServerHaveMod = true;
            });
        });
    }

    private void onJoin(ClientPacketListener clientPacketListener, PacketSender packetSender, Minecraft minecraft) {
        if (!minecraft.isSingleplayer()) {
            MessageUtils.sendTranslated("<green>[CobblemonSpawnAlerts]</green> <yellow>WARNING!</yellow> <white>You are playing on a server. If the server doesn't have the mod installed, or has disabled broadcasting of Pokemon info, certain things, like IVs or Nature, may be displayed incorrectly!");
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
            AlertHandler.alert(pe);
        }
    }
}
