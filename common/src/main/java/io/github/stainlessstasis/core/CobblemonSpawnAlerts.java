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
import io.github.stainlessstasis.util.*;
import kotlin.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.StreamSupport;

public class CobblemonSpawnAlerts {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final String MOD_VERSION = "1.11.5-beta";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommonConfigManager COMMON_CONFIG_MANAGER = new CommonConfigManager();
    public static final String DEFAULT_POKEMON_CONFIG_NAME = "default (You can modify anything BELOW this, but dont delete it!)";
    public static Set<UUID> globallyAlerted = new HashSet<>();
    public static Set<UUID> despawned = new HashSet<>();

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
            if (evt.getKilled().getEntity() == null) {
                return Unit.INSTANCE;
            }
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

    public static PokemonDataPacket createPokemonData(PokemonEntity pokemonEntity) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        Pokemon pokemon = pokemonEntity.getPokemon();

        IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
        Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.NAUGHTY;
        Ability ability = config.broadcastAbility() ? pokemon.getAbility() : Abilities.get("levitate").create(false, Priority.LOWEST);

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
        String nature = config.broadcastNature() ? pokemon.getNature().getName().getPath() : Natures.NAUGHTY.getName().getPath();
        String ability = config.broadcastAbility() ? pokemon.getAbility().getName() : Abilities.get("levitate").create(false, Priority.LOWEST).getName();

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
                        BiomeUtil.getBiomeKey(pokemonEntity.level(), pokemonEntity.position()),
                        DimensionUtil.getDimensionKey(pokemonEntity)),
                new PokemonStats(
                        pokemon.getLevel(),
                        ivs,
                        evYield),
                new PokemonRarity(
                        shouldAlertShiny,
                        shouldAlertLegend,
                        shouldAlertMythical,
                        shouldAlertUltra,
                        shouldAlertParadox,
                        shouldAlertStarter),
                new PokemonTraits(
                        nature,
                        ability,
                        pokemon.getGender().name(),
                        pokemon.getForm().getName()
                )
            );
    }

    public static DespawnDataPacket createDespawnData(Level level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
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
                        "N/A",
                        DimensionUtil.getDimensionKey(level)),
                new PokemonRarity(
                        shouldAlertShiny,
                        shouldAlertLegend,
                        shouldAlertMythical,
                        shouldAlertUltra,
                        shouldAlertParadox,
                        shouldAlertStarter
                ),
                despawnReason.name()
        );
    }
}
