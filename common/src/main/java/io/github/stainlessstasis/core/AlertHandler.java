package io.github.stainlessstasis.core;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.MessageUtils;
import io.github.stainlessstasis.util.RarityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

public class AlertHandler {
    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();

    public static void clearCache() {
        alreadyAlerted.clear();
    }

    public static void alert(PokemonEntity pokemonEntity) {
        // pokemon is owned by someone so no alert
        if (pokemonEntity.getOwnerUUID() != null) {
            return;
        }
        // other shit
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.isReloading()) {
            return;
        }
        if (alreadyAlerted.contains(pokemonEntity.getUUID())) {
            return;
        }

        Pokemon pokemon = pokemonEntity.getPokemon();
        MainConfig config = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig();
        PokemonConfig.PokemonSpecificConfig pokemonConfig;
        String pokemonName = pokemon.getSpecies().getName().toLowerCase();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();

        boolean isInConfig = false;
        // get the pokemon in the config
        if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(pokemonName)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            pokemonConfig = _config;
            isInConfig = true;
        }
        else if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get("default (You can modify anything BELOW this, but dont delete it!)")
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            pokemonConfig = _config;
        } else {
            pokemonConfig = PokemonConfig.PokemonSpecificConfig.createDefault();
            CobblemonSpawnAlerts.LOGGER.warn("No default config found in `pokemon.json`, creating a new one.");
        }

        if (!pokemonConfig.enabled()) {
            return;
        }

        // the reason i have to do this is because for some fucking reason isLegendary and stuff just dont work on the client when on a server
        boolean shouldAlertLegend = RarityUtil.isLegendary(pokemonName) && config.alertAllLegendaries();
        boolean shouldAlertMythical = RarityUtil.isMythical(pokemonName) && config.alertAllMythicals();
        boolean shouldAlertUltra = RarityUtil.isUltraBeast(pokemonName) && config.alertAllUltraBeasts();
        boolean shouldAlertParadox = RarityUtil.isParadox(pokemonName) && config.alertAllParadox();

        boolean shouldAlertNotInDex = config.alertAllNotInDex();
        boolean shouldAlertUncaught = config.alertAllUncaught();
        SpeciesDexRecord record = dex.getSpeciesRecord(pokemon.getSpecies().resourceIdentifier);
        if (record != null) {
            shouldAlertNotInDex = false;
            if (record.hasAtLeast(PokedexEntryProgress.CAUGHT)) {
                shouldAlertUncaught = false;
            }
        }

        boolean shouldAlertShiny =
                isInConfig ?
                        pokemon.getShiny() && pokemonConfig.alertShiny() || config.alertAllShinies()
                        :
                        pokemon.getShiny() && config.alertAllShinies();
        boolean shouldAlertInConfig = pokemonConfig.alwaysAlert() || shouldAlertShiny;
        boolean shouldAlertNotInConfig =
                shouldAlertShiny
                        || shouldAlertLegend
                        || shouldAlertMythical
                        || shouldAlertUltra
                        || shouldAlertParadox
                        || shouldAlertNotInDex
                        || shouldAlertUncaught;

        if (isInConfig) {
            if (!shouldAlertInConfig) {
                return;
            }
        } else {
            if (!shouldAlertNotInConfig) {
                return;
            }
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
        message = MessageUtils.getTranslated(CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates().fullSpawnMessage());
        message = MessageUtils.applyDynamicReplacements(message, pokemonEntity, pokemonConfig);
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }
}
