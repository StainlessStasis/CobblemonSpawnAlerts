package io.github.stainlessstasis;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.*;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = CobblemonSpawnAlerts.MOD_ID)
public class CSANeo {

    @EventBusSubscriber()
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onInit(FMLCommonSetupEvent event) {
            CobblemonSpawnAlerts.initServer();
        }

        @SubscribeEvent
        public static void onPacketRegistration(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1").optional();
            registrar.playToClient(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            PokemonDataPacketHandler::handleClient,
                            PokemonDataPacketHandler::handleServer
            ));
            registrar.playToClient(AlertDataPacket.ID, AlertDataPacket.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            AlertDataPacketHandler::handleClient,
                            AlertDataPacketHandler::handleServer
                    ));
            registrar.playToClient(DespawnDataPacket.ID, DespawnDataPacket.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            DespawnDataPacketHandler::handleClient,
                            DespawnDataPacketHandler::handleServer
                    ));
            registrar.playToClient(ModLoadedPacket.ID, ModLoadedPacket.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            ModLoadedPacketHandler::handleClient,
                            ModLoadedPacketHandler::handleServer
            ));
        }
    }

    @EventBusSubscriber()
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandRegistry.registerCommonCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent
        public static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                PacketDistributor.sendToPlayer(player, new ModLoadedPacket(true));
            }
        }
    }

    public static class PokemonDataPacketHandler {
        public static void handleClient(final PokemonDataPacket data, final IPayloadContext context) {
            PacketHandlers.handlePokemonDataPacket(data.pokemonNetworkID(), data.ivs(), data.evYield(), data.nature(), data.ability(), data.bucket());
        }
        public static void handleServer(final PokemonDataPacket data, final IPayloadContext context) {}
    }

    public static class AlertDataPacketHandler {
        public static void handleClient(final AlertDataPacket data, final IPayloadContext context) {
            PacketHandlers.handleAlertDataPacket(data);
        }
        public static void handleServer(final AlertDataPacket data, final IPayloadContext context) {}
    }

    public static class DespawnDataPacketHandler {
        public static void handleClient(final DespawnDataPacket data, final IPayloadContext context) {
            PacketHandlers.handleDespawnDataPacket(data);
        }
        public static void handleServer(final DespawnDataPacket data, final IPayloadContext context) {}
    }

    public static class ModLoadedPacketHandler {
        public static void handleClient(final ModLoadedPacket data, final IPayloadContext context) {
            CSANeoClient.doesServerHaveMod = true;
        }

        public static void handleServer(final ModLoadedPacket data, final IPayloadContext context) {}
    }
}
