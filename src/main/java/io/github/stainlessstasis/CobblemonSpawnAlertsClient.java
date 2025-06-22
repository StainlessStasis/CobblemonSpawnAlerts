package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.config.Config;
import io.github.stainlessstasis.config.ConfigManager;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.TranslatedMessageUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;

import java.util.Objects;

public class CobblemonSpawnAlertsClient implements ClientModInitializer {
    public static final String MOD_ID = "cobblemon-spawn-alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("CobblemonSpawnAlerts initializing");
        ConfigManager.loadConfig();
        ClientEntityEvents.ENTITY_LOAD.register(this::onEntityLoaded);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cobblemonspawnalerts")
                    .then(ClientCommandManager.literal("reloadconfig")
                    .executes(context -> {
                ConfigManager.reload();
                return 1;
            })));
        });
    }

    public void onEntityLoaded(Entity entity, ClientLevel world) {
        if (!(entity instanceof PokemonEntity pokemon)) {
            return;
        }

        // pokemon is owned by someone so no alert
        if (pokemon.getOwnerUUID() != null) {
            return;
        }
        // other shit
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (ConfigManager.isReloading()) {
            return;
        }

        // get the pokemon in the config
        if (ConfigManager.getConfig().get(pokemon.getName().getString().toLowerCase()) instanceof Config.PokemonSpecificConfig config) {
            if (!config.enabled) {
                return;
            }

            boolean shouldAlertShiny = config.alertShiny && pokemon.getPokemon().getShiny();
            boolean shouldAlert = config.alwaysAlert || (shouldAlertShiny);
            if (!shouldAlert) {
                return;
            }

            // send the alert
            if (!Objects.equals(config.customAlertMessage, "")) {
                Component component = ComponentUtil.convertFromAdventure(config.customAlertMessage);
                player.sendSystemMessage(component);
                return;
            }

            String message = I18n.get(ConfigManager.getDefaultSpawnMessage(), pokemon.getName().getString());
            if (shouldAlertShiny) {
                message = message.replace("{shiny}", I18n.get(MOD_ID+".shiny"));
            } else {
                message = message.replace("{shiny}", "");
            }
            Component component = ComponentUtil.convertFromAdventure(message);
            player.sendSystemMessage(component);
        }
    }
}
