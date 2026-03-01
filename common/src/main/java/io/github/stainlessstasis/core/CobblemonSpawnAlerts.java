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
import io.github.stainlessstasis.compat.DiscordWebhookService;
import io.github.stainlessstasis.config.manager.CommonConfigManager;
import io.github.stainlessstasis.config.common.ServerConfig;
import io.github.stainlessstasis.config.manager.VersionMatcher;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.*;
import kotlin.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.StreamSupport;

public class CobblemonSpawnAlerts {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final String MOD_VERSION = "1.13.0";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommonConfigManager COMMON_CONFIG_MANAGER = new CommonConfigManager();
    public static final String DEFAULT_POKEMON_CONFIG_NAME = "default (You can modify anything BELOW this, but dont delete it!)";
    public static Set<UUID> globallyAlerted = new HashSet<>();
    public static Set<UUID> despawned = new HashSet<>();
    private static DiscordWebhookService discordWebhookService;

    public static void initServer() {
        LOGGER.info("CobblemonSpawnAlerts server initializing...");
        COMMON_CONFIG_MANAGER.loadConfig();

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {
            var entity = event.getEntity();
            RarityUtil.Bucket bucket = RarityUtil.Bucket.NONE;
            if (COMMON_CONFIG_MANAGER.getServerConfig().broadcastBucket()) {
                bucket = RarityUtil.getRarityBucket(entity, event.getSpawnablePosition());
            }
            Services.PLATFORM.onPokemonSpawned(entity, bucket);
            return Unit.INSTANCE;
        });

        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, event -> {
            if (CobblemonSpawnAlerts.globallyAlerted.contains(event.getPokemon().getUuid())) {
                Services.PLATFORM.onPokemonDespawned(event.getPlayer().level(), event.getPokemon(), event.getPlayer().getName().getString(), DespawnReason.CAPTURED);
            }
            return Unit.INSTANCE;
        });

        CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.NORMAL, event -> {
            if (event.getKilled().getEntity() == null) {
                return Unit.INSTANCE;
            }
            Pokemon pokemon = event.getKilled().getEntity().getPokemon();

            if (pokemon.getOwnerUUID() != null || !event.getBattle().isPvW() || !CobblemonSpawnAlerts.globallyAlerted.contains(pokemon.getUuid())) {
                return Unit.INSTANCE;
            }

            Optional<UUID> uuid = StreamSupport.stream(event.getBattle().getPlayerUUIDs().spliterator(), false).findFirst();
            String playerName = "N/A";
            if (uuid.isPresent()) {
                Player player = event.getKilled().getEntity().level().getPlayerByUUID(uuid.get());
                if (player != null) {
                    playerName = player.getName().getString();
                }
            }

            Services.PLATFORM.onPokemonDespawned(event.getKilled().getEntity().level(), pokemon, playerName, DespawnReason.FAINTED);

            return Unit.INSTANCE;
        });
    }

    public static synchronized DiscordWebhookService getWebhookService() {
        if (discordWebhookService == null) {
            discordWebhookService = new DiscordWebhookService();
        }
        return discordWebhookService;
    }

    public static String getLastKnownModVersion() {
        return VersionMatcher.getLastKnownModVersion();
    }

    public static PokemonDataPacket createPokemonData(PokemonEntity pokemonEntity, RarityUtil.Bucket bucket) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        Pokemon pokemon = pokemonEntity.getPokemon();

        IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
        Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.NAUGHTY;
        Ability ability = config.broadcastAbility() ? pokemon.getAbility() : Abilities.get("levitate").create(false, Priority.LOWEST);

        EVs finalEvYield = CobblemonStatProvider.INSTANCE.createEmptyEVs();
        if (config.broadcastEVs()) {
            finalEvYield = EvsUtil.getEVsFromYield(pokemon.getForm().getEvYield());
        }

        return new PokemonDataPacket(pokemonEntity.getId(), ivs, finalEvYield, nature, ability, bucket);
    }

    public static PokemonStats createPokemonStats(Pokemon pokemon) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : IVs.createRandomIVs(0);
        EVs evYield = config.broadcastEVs() ? EvsUtil.getEVsFromYield(pokemon.getForm().getEvYield()) : EVs.createEmpty();

        return new PokemonStats(pokemon.getLevel(), ivs, evYield);
    }

    public static PokemonRarityData createPokemonRarityData(Pokemon pokemon) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        return new PokemonRarityData(
                pokemon.getShiny() && config.broadcastShiny(),
                pokemon.isLegendary(),
                pokemon.isMythical(),
                pokemon.isUltraBeast(),
                pokemon.hasLabels(CobblemonPokemonLabels.PARADOX),
                RarityUtil.isStarter(pokemon.getSpecies().getNationalPokedexNumber())
        );
    }

    public static PokemonTraits createPokemonTraits(Pokemon pokemon) {
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        String nature = config.broadcastNature() ? pokemon.getNature().getName().getPath() : Natures.NAUGHTY.getName().getPath();
        String ability = config.broadcastAbility() ? pokemon.getAbility().getName() : Abilities.get("levitate").create(false, Priority.LOWEST).getName();

        return new PokemonTraits(
                nature,
                ability,
                pokemon.getGender().name(),
                pokemon.getForm().getName()
        );
    }

    public static AlertDataPacket createAlertData(PokemonEntity pokemonEntity, RarityUtil.Bucket bucket) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = PokemonNameUtil.getTranslationKey(pokemon);

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
                    DimensionUtil.getDimensionKey(pokemonEntity),
                    bucket
            ),
            createPokemonStats(pokemon),
            createPokemonRarityData(pokemon),
            createPokemonTraits(pokemon)
        );
    }

    public static DespawnDataPacket createDespawnData(Level level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        return new DespawnDataPacket(
                new AlertDataPacket(
                        new PokemonSpawnData(
                                PokemonNameUtil.getTranslationKey(pokemon),
                                pokemon.getUuid(),
                                new Vector3f(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE),
                                pokemon.getSpecies().getNationalPokedexNumber(),
                                playerName,
                                "N/A",
                                DimensionUtil.getDimensionKey(level),
                                RarityUtil.Bucket.NONE
                        ),
                        createPokemonStats(pokemon),
                        createPokemonRarityData(pokemon),
                        createPokemonTraits(pokemon)
                ),

                despawnReason.name()
        );
    }
}
