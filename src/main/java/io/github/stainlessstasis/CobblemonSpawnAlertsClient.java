package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.config.ConfigManager;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.RarityUtil;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
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
        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cobblemonspawnalerts")
                    .then(ClientCommandManager.literal("reload")
                    .executes(context -> {
                ConfigManager.reload();
                return 1;
            })));
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cobblemonspawnalerts")
                            .then(ClientCommandManager.literal("openconfig")
                                    .executes(context -> {
                                        ConfigManager.openDirectory();
                                        return 1;
                                    })));
        });
    }

    private void onClientStop(Minecraft minecraft) {
        alreadyAlerted.clear();
    }

    private void onDisconnect(ClientPacketListener clientPacketListener, Minecraft minecraft) {
        alreadyAlerted.clear();
    }

    private void onEntityLoaded(Entity entity, ClientLevel world) {
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
        if (alreadyAlerted.contains(pokemonEntity.getUUID())) {
            return;
        }

        MainConfig config = ConfigManager.getMainConfig();
        PokemonConfig.PokemonSpecificConfig pokemonConfig;
        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = pokemon.getSpecies().getName().toLowerCase();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();

        boolean isInConfig = false;
        // get the pokemon in the config
        if (ConfigManager.getPokemonConfig().pokemonConfigs().get(pokemonName)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            pokemonConfig = _config;
            isInConfig = true;
        } else {
            pokemonConfig = PokemonConfig.PokemonSpecificConfig.createDefault();
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
        message = MessageUtils.getTranslated(ConfigManager.getMessageTemplates().fullSpawnMessage());
        message = MessageUtils.applyDynamicReplacements(message, pokemonEntity, pokemonConfig);
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }
}
