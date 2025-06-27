package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.RarityUtil;
import io.github.stainlessstasis.util.MessageUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CobblemonSpawnAlertsClient implements ClientModInitializer {
//    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();
    public static final ClientConfigManager configManager = new ClientConfigManager();

    @Override
    public void onInitializeClient() {
        CobblemonSpawnAlerts.LOGGER.info("CobblemonSpawnAlerts client initializing");
        configManager.loadConfig();
//        ClientPlayConnectionEvents.DISCONNECT.register(this::onDisconnect);
//        ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);

        // Packets
        ClientPlayNetworking.registerGlobalReceiver(PokemonDataPacket.ID, (payload, context) -> {
            context.client().execute(() -> {
                handlePokemonDataPacket(payload.pokemonNetworkID(), payload.ivs(), payload.nature());
            });
        });

        // Commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("cobblemonspawnalerts")
                    .then(ClientCommandManager.literal("reload")
                        .executes(context -> {
                            configManager.reload();
                            return 1;
            }))
                    .then(ClientCommandManager.literal("reload-server")
                            .executes(context -> {return 1;})));
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("cobblemonspawnalerts")
                            .then(ClientCommandManager.literal("openconfig")
                                    .executes(context -> {
                                        ClientConfigManager.openDirectory();
                                        return 1;
                                    })));
        });
    }

//    private void onClientStop(Minecraft minecraft) {
//        alreadyAlerted.clear();
//    }
//
//    private void onDisconnect(ClientPacketListener clientPacketListener, Minecraft minecraft) {
//        alreadyAlerted.clear();
//    }

    private void handlePokemonDataPacket(int pokemonNetworkID, IVs ivs, Nature nature) {
        // -PACKET HANDLING-

        if (!(Minecraft.getInstance().level instanceof ClientLevel level)) {
            return;
        }
        if (!(level.getEntity(pokemonNetworkID) instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        System.out.println("POKEMON DATA: "+pokemonNetworkID+" "+ivs.get(Stats.HP)+" "+nature.getDisplayName());
        Pokemon pokemon = pokemonEntity.getPokemon();
        pokemon.setIvs$common(ivs);
        pokemon.setNature(nature);


        // -ALERT MESSAGE-

        // pokemon is owned by someone so no alert
        if (pokemonEntity.getOwnerUUID() != null) {
            return;
        }
        // other shit
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (configManager.isReloading()) {
            return;
        }
//        if (alreadyAlerted.contains(pokemonEntity.getUUID())) {
//            return;
//        }

        MainConfig config = configManager.getMainConfig();
        PokemonConfig.PokemonSpecificConfig pokemonConfig;
        String pokemonName = pokemon.getSpecies().getName().toLowerCase();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();

        boolean isInConfig = false;
        // get the pokemon in the config
        if (configManager.getPokemonConfig().pokemonConfigs().get(pokemonName)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            pokemonConfig = _config;
            isInConfig = true;
        }
        else if (configManager.getPokemonConfig().pokemonConfigs().get("default (You can modify anything BELOW this, but dont delete it!)")
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

//        alreadyAlerted.add(pokemonEntity.getUUID());

        // send the custom alert if one exits
        String message;
        if (!Objects.equals(pokemonConfig.customAlertMessage(), "")) {
            message = MessageUtils.applyDynamicReplacements(pokemonConfig.customAlertMessage(), pokemonEntity, pokemonConfig);
            MessageUtils.sendTranslated(message);
            return;
        }

        // use the default message if no custom one is provided
        message = MessageUtils.getTranslated(configManager.getMessageTemplates().fullSpawnMessage());
        message = MessageUtils.applyDynamicReplacements(message, pokemonEntity, pokemonConfig);
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }
}
