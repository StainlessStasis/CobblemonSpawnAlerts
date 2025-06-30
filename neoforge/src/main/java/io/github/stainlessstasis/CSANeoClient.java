package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.core.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
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
            CommandRegistry.registerClientCommands(event.getDispatcher(), event.getBuildContext(), null);
        }

        @SubscribeEvent
        public static void onConnect(ClientPlayerNetworkEvent.LoggingIn event) {
            if (!Minecraft.getInstance().isSingleplayer()) {
                MessageUtils.sendTranslated("<green>[CobblemonSpawnAlert]</green> <yellow>WARNING!</yellow> <white>You are playing on a server. If the server doesn't have the mod installed, or has disabled broadcasting of Pokemon info, certain things, like IVs or Nature, may be displayed incorrectly!");
            }
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            AlertHandler.clearCache();
        }

        @SubscribeEvent
        public static void onEntityLoad(EntityJoinLevelEvent event) {
            if (!(event.getLevel().isClientSide)) {
                return;
            }

            if (event.getEntity() instanceof PokemonEntity pe && !doesServerHaveMod) {
                AlertHandler.alert(pe);
            }
        }
    }
}
