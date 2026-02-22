package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.alert.DespawnReason;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.platform.IPlatformHelper;
import io.github.stainlessstasis.platform.Platform;
import io.github.stainlessstasis.util.AlertUtil;
import kotlin.Unit;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;
import java.util.*;

public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public void onPokemonSpawned(PokemonEntity pokemonEntity, String bucketName) {
        ScheduledTask _task = new ScheduledTask.Builder().delay(0.5f).execute(task -> {
            Set<UUID> alreadyAlerted = new HashSet<>();

            List<ServerPlayer> players = new ArrayList<>();
            if (pokemonEntity.level().getChunkSource() instanceof ServerChunkCache chunkCache) {
                 players = chunkCache.chunkMap.getPlayersCloseForSpawning(pokemonEntity.chunkPosition());
            }

            // Send EVERY Pokemon to clients that have the entity loaded for IV/EV hunting, etc.
            for (ServerPlayer player : players) {
                PacketDistributor.sendToPlayer(player, CobblemonSpawnAlerts.createPokemonData(pokemonEntity, bucketName));
                alreadyAlerted.add(player.getUUID());
            }

            // Only send RARE Pokemon (e.g. legendaries) to all clients, so we dont overload the network
            if (!AlertUtil.shouldGlobalAlert(pokemonEntity, bucketName)) {
                return Unit.INSTANCE;
            } else {
                CobblemonSpawnAlerts.globallyAlerted.add(pokemonEntity.getPokemon().getUuid());
            }

            if (pokemonEntity.level() instanceof ServerLevel level) {
                for (ServerPlayer player : level.players()) {
                    if (alreadyAlerted.contains(player.getUUID())) {
                        continue;
                    }

                    PacketDistributor.sendToPlayer(player, CobblemonSpawnAlerts.createAlertData(pokemonEntity));
                }
            }

            return Unit.INSTANCE;
        }).build();
    }

    @Override
    public void onPokemonDespawned(Level _level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        IPlatformHelper.super.onPokemonDespawned(_level, pokemon, playerName, despawnReason);
        if (_level instanceof ServerLevel level) {
            PacketDistributor.sendToAllPlayers(CobblemonSpawnAlerts.createDespawnData(level, pokemon, playerName, despawnReason));

        }
    }

    @Override
    public boolean doesServerHaveMod() {
        return CSANeoClient.doesServerHaveMod;
    }
}