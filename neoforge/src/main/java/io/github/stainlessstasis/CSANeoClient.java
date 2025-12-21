package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.util.EvsUtil;
import io.github.stainlessstasis.util.MessageUtils;
import kotlin.Unit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.commands.Commands;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;

@Mod(value = CobblemonSpawnAlerts.MOD_ID, dist = Dist.CLIENT)
public class CSANeoClient {
    public static boolean doesServerHaveMod = false;

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onInit(FMLClientSetupEvent event) {
            CobblemonSpawnAlertsClient.initClient();
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT)
    public static class GameBusEvents {
        @SubscribeEvent
        public static void onCommandRegistration(RegisterClientCommandsEvent event) {
            event.getDispatcher().register(
                    Commands.literal("csa")
                            .then(Commands.literal("reload")
                                    .executes(ctx -> {
                                        return CommandRegistry.handleReloadCommand();
                                    }))

                            .then(Commands.literal("openconfig")
                                    .executes(ctx -> {
                                        return CommandRegistry.handleOpenConfigCommand();
                                    }))

                            .then(Commands.literal("glow")
                                    .then(Commands.argument("uuid", StringArgumentType.string())
                                            .executes(ctx -> {
                                                String uuidString = ctx.getArgument("uuid", String.class);
                                                return CommandRegistry.handleGlowCommand(uuidString);
                                            }))
                                    .then(Commands.literal("clear")
                                            .executes(ctx -> {
                                                return CommandRegistry.handleGlowClearCommand();
                                            })
                                    )
                            ));
        }

        @SubscribeEvent
        public static void onConnect(ClientPlayerNetworkEvent.LoggingIn event) {
            if (!Minecraft.getInstance().isSingleplayer() && CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig().multiplayerWarning()) {
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

        @SubscribeEvent
        public static void onEntityUnload(EntityLeaveLevelEvent event) {
            if (!(event.getLevel().isClientSide)) {
                return;
            }

            CobblemonSpawnAlertsClient.onUnload(event.getEntity(), event.getLevel());
        }
    }
}
