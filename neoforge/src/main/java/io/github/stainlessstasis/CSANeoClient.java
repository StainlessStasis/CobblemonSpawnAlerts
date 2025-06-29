package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.core.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@Mod(value = CobblemonSpawnAlerts.MOD_ID, dist = Dist.CLIENT)
public class CSANeoClient {
    @EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onInit(FMLClientSetupEvent event) {
            CobblemonSpawnAlerts.initClient();
        }
    }

    @EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onCommandRegistration(RegisterCommandsEvent event) {
            CommandRegistry.registerClientCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            AlertHandler.clearCache();
        }

        // TODO: CHECK IF MOD IS ENABLED ON SERVER
        @SubscribeEvent
        public static void onEntityLoad(EntityJoinLevelEvent event) {
            if (!(event.getLevel() instanceof ClientLevel level)) {
                return;
            }

            if (event.getEntity() instanceof PokemonEntity pe) {
                AlertHandler.alert(pe);
            }
        }
    }
}
