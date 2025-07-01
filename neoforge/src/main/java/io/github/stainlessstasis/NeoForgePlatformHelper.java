package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.EVs;
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
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

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
    public void onPokemonSpawned(PokemonEntity pokemonEntity) {
        Pokemon pokemon = pokemonEntity.getPokemon();

        ScheduledTask _task = new ScheduledTask.Builder().delay(0.05f).execute(task -> {
            ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
            IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
            Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.INSTANCE.getNAUGHTY();
            PacketDistributor.sendToPlayersTrackingEntity(pokemonEntity, new PokemonDataPacket(pokemonEntity.getId(), ivs, nature));
            return Unit.INSTANCE;
        }).build();
    }
}