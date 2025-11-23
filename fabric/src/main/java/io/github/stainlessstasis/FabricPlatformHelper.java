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
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void onPokemonSpawned(PokemonEntity pokemonEntity) {
        ScheduledTask _task = new ScheduledTask.Builder().delay(0.5f).execute(task -> {
            Set<UUID> alreadyAlerted = new HashSet<>();

            // Send EVERY Pokemon to clients that have the entity loaded for IV/EV hunting, etc.
            for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
                ServerPlayNetworking.send(player, CobblemonSpawnAlerts.createPokemonData(pokemonEntity));
                alreadyAlerted.add(player.getUUID());
            }

            // Only send RARE Pokemon (e.g. legendaries) to all clients, so we dont kill the network
            if (!AlertUtil.shouldGlobalAlert(pokemonEntity)) {
                return Unit.INSTANCE;
            } else {
                CobblemonSpawnAlerts.globallyAlerted.add(pokemonEntity.getPokemon().getUuid());
            }

            if (pokemonEntity.level() instanceof ServerLevel level) {
                for (ServerPlayer player : level.players()) {
                    if (alreadyAlerted.contains(player.getUUID())) {
                        continue;
                    }

                    ServerPlayNetworking.send(player, CobblemonSpawnAlerts.createAlertData(pokemonEntity));
                }
            }

            return Unit.INSTANCE;
        }).build();
    }

    @Override
    public void onPokemonDespawned(Level _level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        IPlatformHelper.super.onPokemonDespawned(_level, pokemon, playerName, despawnReason);

        if (_level instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
                ServerPlayNetworking.send(player, CobblemonSpawnAlerts.createDespawnData(pokemon, playerName, despawnReason));
            }
        }
    }

    @Override
    public boolean doesServerHaveMod() {
        return CSAFabricClient.doesServerHaveMod;
    }
}