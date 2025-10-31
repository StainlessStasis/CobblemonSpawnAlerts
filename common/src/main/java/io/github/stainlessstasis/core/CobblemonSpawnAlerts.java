package io.github.stainlessstasis.core;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import io.github.stainlessstasis.alert.DespawnReason;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.config.CommonConfigManager;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.BiomeUtil;
import io.github.stainlessstasis.util.EvsUtil;
import io.github.stainlessstasis.util.PokemonNameUtil;
import io.github.stainlessstasis.util.RarityUtil;
import kotlin.Unit;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.stainlessstasis.util.RarityUtil;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.StreamSupport;

public class CobblemonSpawnAlerts {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final String MOD_VERSION = "1.9";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommonConfigManager COMMON_CONFIG_MANAGER = new CommonConfigManager();
    public static final ClientConfigManager CLIENT_CONFIG_MANAGER = new ClientConfigManager();
    public static final String DEFAULT_POKEMON_CONFIG_NAME = "default (You can modify anything BELOW this, but dont delete it!)";
    public static Set<UUID> globallyAlerted = new HashSet<>();
    public static Set<UUID> despawned = new HashSet<>();
    public static Set<UUID> glowing = new HashSet<>();

    public static void initServer() {
        LOGGER.info("CobblemonSpawnAlerts server initializing...");
        COMMON_CONFIG_MANAGER.loadConfig();

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, evt -> {
            Services.PLATFORM.onPokemonSpawned(evt.getEntity());
            return Unit.INSTANCE;
        });

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, evt -> {
            if (CobblemonSpawnAlerts.globallyAlerted.contains(evt.getPokemon().getUuid())) {
                Services.PLATFORM.onPokemonDespawned(evt.getPlayer().level(), evt.getPokemon(), evt.getPlayer().getName().getString(), DespawnReason.CAPTURED);
            }
            return Unit.INSTANCE;
        });

        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, evt -> {
            Pokemon pokemon = evt.getKilled().getEntity().getPokemon();

            if (pokemon.getOwnerUUID() != null || !evt.getBattle().isPvW() || !CobblemonSpawnAlerts.globallyAlerted.contains(pokemon.getUuid())) {
                return Unit.INSTANCE;
            }

            Optional<UUID> uuid = StreamSupport.stream(evt.getBattle().getPlayerUUIDs().spliterator(), false).findFirst();
            String playerName = "N/A";
            if (uuid.isPresent()) {
                Player player = evt.getKilled().getEntity().level().getPlayerByUUID(uuid.get());
                if (player != null) {
                    playerName = player.getName().getString();
                }
            }

            Services.PLATFORM.onPokemonDespawned(evt.getKilled().getEntity().level(), pokemon, playerName, DespawnReason.FAINTED);

            return Unit.INSTANCE;
        });
    }

    public static void initClient() {
        LOGGER.info("CobblemonSpawnAlerts client initializing...");
        CLIENT_CONFIG_MANAGER.loadConfig();
    }

    public static PokemonDataPacket createPokemonData(PokemonEntity pokemonEntity) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        Pokemon pokemon = pokemonEntity.getPokemon();

        IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
        Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.INSTANCE.getNAUGHTY();
        Ability ability = config.broadcastAbility() ? pokemon.getAbility() : Abilities.INSTANCE.get("levitate").create(false, Priority.LOWEST);

        EVs finalEvYield = CobblemonStatProvider.INSTANCE.createEmptyEVs();
        if (config.broadcastEVs()) {
            finalEvYield = EvsUtil.getEVsFromYield(pokemon.getForm().getEvYield());
        }

        return new PokemonDataPacket(pokemonEntity.getId(), ivs, finalEvYield, nature, ability);
    }

    public static AlertDataPacket createAlertData(PokemonEntity pokemonEntity) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = PokemonNameUtil.getTranslationKey(pokemon);

        boolean shouldAlertShiny = pokemon.getShiny() && config.alertShinies();
        boolean shouldAlertLegend = pokemon.isLegendary() && config.alertLegendaries();
        boolean shouldAlertMythical = pokemon.isMythical() && config.alertMythicals();
        boolean shouldAlertUltra = pokemon.isUltraBeast() && config.alertUltraBeasts();
        boolean shouldAlertParadox = pokemon.hasLabels(CobblemonPokemonLabels.PARADOX) && config.alertParadox();
        boolean shouldAlertStarter = RarityUtil.isStarter(pokemon.getSpecies().getNationalPokedexNumber()) && config.alertStarters();

        IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : IVs.createRandomIVs(0);
        EVs evYield = config.broadcastEVs() ? EvsUtil.getEVsFromYield(pokemonEntity.getForm().getEvYield()) : EVs.createEmpty();
        String nature = config.broadcastNature() ? pokemon.getNature().getName().getPath() : Natures.INSTANCE.getNAUGHTY().getName().getPath();
        String ability = config.broadcastAbility() ? pokemon.getAbility().getName() : Abilities.INSTANCE.get("levitate").create(false, Priority.LOWEST).getName();

        String nearestPlayerName = "N/A";
        if (pokemonEntity.level().getNearestPlayer(pokemonEntity, 128d) instanceof Player player) {
            nearestPlayerName = player.getName().getString();
        }

        return new AlertDataPacket(
                new PokemonSpawnData(
                        pokemonName,
                        pokemon.getUuid(),
                        pokemonEntity.position().toVector3f(),
                        pokemon.getSpecies().getNationalPokedexNumber(),
                        nearestPlayerName,
                        BiomeUtil.getBiomeKeyFromCoords(pokemonEntity.level(), pokemonEntity.position())),
                new PokemonStats(
                        pokemon.getLevel(),
                        ivs,
                        evYield),
                new PokemonTraits(
                        shouldAlertShiny,
                        shouldAlertLegend,
                        shouldAlertMythical,
                        shouldAlertUltra,
                        shouldAlertParadox,
                        shouldAlertStarter),
                nature,
                ability,
                pokemon.getGender().name());
    }

    public static DespawnDataPacket createDespawnData(Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        String pokemonName = PokemonNameUtil.getTranslationKey(pokemon);

        boolean shouldAlertShiny = pokemon.getShiny() && config.alertShinies();
        boolean shouldAlertLegend = pokemon.isLegendary() && config.alertLegendaries();
        boolean shouldAlertMythical = pokemon.isMythical() && config.alertMythicals();
        boolean shouldAlertUltra = pokemon.isUltraBeast() && config.alertUltraBeasts();
        boolean shouldAlertParadox = pokemon.hasLabels(CobblemonPokemonLabels.PARADOX) && config.alertParadox();
        boolean shouldAlertStarter = RarityUtil.isStarter(pokemon.getSpecies().getNationalPokedexNumber()) && config.alertStarters();

        return new DespawnDataPacket(
                playerName,
                new PokemonSpawnData(
                        pokemonName,
                        pokemon.getUuid(),
                        new Vector3f(0, 0, 0),
                        pokemon.getSpecies().getNationalPokedexNumber(),
                        "N/A",
                        "N/A"),
                new PokemonTraits(
                        shouldAlertShiny,
                        shouldAlertLegend,
                        shouldAlertMythical,
                        shouldAlertUltra,
                        shouldAlertParadox,
                        shouldAlertStarter),
                despawnReason.name()
        );
    }
}
