package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.util.EvsUtil;
import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
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
            event.getDispatcher().register(
                    Commands.literal("csa")
                            .then(Commands.literal("reload")
                                    .executes(ctx -> {
                                        return CommandRegistry.handleReloadCommand();
                                    })));
            event.getDispatcher().register(
                    Commands.literal("csa")
                            .then(Commands.literal("openconfig")
                                    .executes(ctx -> {
                                        return CommandRegistry.handleOpenConfigCommand();
                                    })));
        }

        @SubscribeEvent
        public static void onConnect(ClientPlayerNetworkEvent.LoggingIn event) {
            if (!Minecraft.getInstance().isSingleplayer() && CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().multiplayerWarning()) {
                MessageUtils.sendTranslated("cobblemon-spawn-alerts.multiplayer_warning");
            }
        }

        @SubscribeEvent
        public static void onDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
            AlertHandler.clearCache();
            EvsUtil.clearCache();
            doesServerHaveMod = false;
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
}
