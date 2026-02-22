package io.github.stainlessstasis.platform;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.alert.DespawnReason;
import io.github.stainlessstasis.compat.JourneymapCompat;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.RarityUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.nio.file.Path;

public interface IPlatformHelper {
    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    Platform getPlatform();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * Gets the name of the environment type as a string.
     *
     * @return The name of the environment type.
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Path getConfigDir();

    void onPokemonSpawned(PokemonEntity pokemonEntity, RarityUtil.Bucket bucket);

    default void onPokemonDespawned(Level _level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        CobblemonSpawnAlerts.globallyAlerted.remove(pokemon.getUuid());

        if (despawnReason != DespawnReason.DESPAWNED) {
            CobblemonSpawnAlerts.despawned.add(pokemon.getUuid());
        } else {
            CobblemonSpawnAlerts.despawned.remove(pokemon.getUuid());
        }
    };

    boolean doesServerHaveMod();

    Component parseMarkup(String markup);
}
