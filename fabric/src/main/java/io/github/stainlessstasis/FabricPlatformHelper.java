package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.platform.IPlatformHelper;
import io.github.stainlessstasis.platform.Platform;
import kotlin.Unit;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

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
        Pokemon pokemon = pokemonEntity.getPokemon();

        ScheduledTask _task = new ScheduledTask.Builder().delay(0.05f).execute(task -> {
            for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
                ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
                IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
                Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.INSTANCE.getNAUGHTY();
                ServerPlayNetworking.send(player, new PokemonDataPacket(pokemonEntity.getId(), ivs, nature));
            }
            return Unit.INSTANCE;
        }).build();
    }
}