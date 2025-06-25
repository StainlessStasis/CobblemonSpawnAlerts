package io.github.stainlessstasis;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.PokemonConfig;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class CobblemonSpawnAlertsClient implements ClientModInitializer {
    public static final String MOD_ID = "cobblemon-spawn-alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();

    @Override
    public void onInitializeClient() {
        LOGGER.info("CobblemonSpawnAlerts initializing");
        ConfigManager.loadConfig();
        ClientEntityEvents.ENTITY_LOAD.register(this::onEntityLoaded);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cobblemonspawnalerts")
                    .then(ClientCommandManager.literal("reload")
                    .executes(context -> {
                ConfigManager.reload();
                return 1;
            })));
        });
    }

    public void onEntityLoaded(Entity entity, ClientLevel world) {
        if (!(entity instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        // pokemon is owned by someone so no alert
        if (pokemonEntity.getOwnerUUID() != null) {
            return;
        }
        // other shit
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (ConfigManager.isReloading()) {
            return;
        }
        
        PokemonConfig.PokemonSpecificConfig pokemonConfig;
        boolean isInConfig = false;
        // get the pokemon in the config
        if (ConfigManager.getPokemonConfig().pokemonConfigs().get(pokemonEntity.getName().getString().toLowerCase()) 
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            pokemonConfig = _config;
            isInConfig = true;
        } else {
            pokemonConfig = PokemonConfig.PokemonSpecificConfig.createDefault();
        }

        MainConfig config = ConfigManager.getMainConfig();
        Pokemon pokemon = pokemonEntity.getPokemon();

        boolean shouldAlertShiny = pokemon.getShiny() && (pokemonConfig.alertShiny() || config.alertAllShinies());
        boolean shouldAlertLegend = pokemon.isLegendary() && config.alertAllLegendaries();
        boolean shouldAlertMythical = pokemon.isMythical() && config.alertAllMythicals();
        boolean shouldAlertUltra = pokemon.isUltraBeast() && config.alertAllUltraBeasts();
        boolean shouldAlert_ =
                (pokemonConfig.alwaysAlert() && isInConfig)
                || shouldAlertShiny
                || shouldAlertLegend
                || shouldAlertMythical
                || shouldAlertUltra;
        boolean shouldAlert = !alreadyAlerted.contains(pokemonEntity.getUUID()) && shouldAlert_;
        if (!pokemonConfig.enabled() || !shouldAlert) {
            return;
        }

        alreadyAlerted.add(pokemonEntity.getUUID());

        // send the custom alert if one exits
        String message;
        if (!Objects.equals(pokemonConfig.customAlertMessage(), "")) {
            message = MessageUtils.applyDynamicReplacements(pokemonConfig.customAlertMessage(), pokemonEntity, pokemonConfig);
            MessageUtils.sendTranslated(message);
            return;
        }

        // use the default message if no custom one is provided
        message = MessageUtils.getTranslated(ConfigManager.getMessageTemplates().fullSpawnMessage());
        message = MessageUtils.applyDynamicReplacements(message, pokemonEntity, pokemonConfig);
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }
}
