package io.github.stainlessstasis;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.PacketHandlers;
import io.github.stainlessstasis.network.PokemonDataPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = CobblemonSpawnAlerts.MOD_ID)
public class CSANeo {

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onInit(FMLCommonSetupEvent event) {
            CobblemonSpawnAlerts.initCommon();
        }

        @SubscribeEvent
        public static void onPacketRegistration(RegisterPayloadHandlersEvent event) {
            PayloadRegistrar registrar = event.registrar("1");
            registrar.playToClient(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC,
                    new DirectionalPayloadHandler<>(
                            PokemonDataPacketHandler::handleClient,
                            PokemonDataPacketHandler::handleServer
                    ));
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandRegistry.registerCommonCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }
    }

    public static class PokemonDataPacketHandler {
        public static void handleClient(final PokemonDataPacket data, final IPayloadContext context) {
            PacketHandlers.handlePokemonDataPacket(data.pokemonNetworkID(), data.ivs(), data.nature());
        }

        public static void handleServer(final PokemonDataPacket data, final IPayloadContext context) {}
    }
}
