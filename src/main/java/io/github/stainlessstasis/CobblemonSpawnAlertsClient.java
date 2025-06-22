package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.config.Config;
import io.github.stainlessstasis.config.ConfigManager;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
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
            String message;
            if (!Objects.equals(config.customAlertMessage, "")) {
                message = MessageUtils.applyDynamicReplacements(config.customAlertMessage, pokemon.getName().getString(), shouldAlertShiny);
                MessageUtils.sendTranslated(message);
                return;
            }

            // use the default message if no custom one is provided
            message = MessageUtils.getTranslated(ConfigManager.getDefaultSpawnMessage());
            message = MessageUtils.applyDynamicReplacements(message, pokemon.getName().getString(), shouldAlertShiny);
            Component component = ComponentUtil.convertFromAdventure(message);
            player.sendSystemMessage(component);

            // testing commit
        }
    }
}
