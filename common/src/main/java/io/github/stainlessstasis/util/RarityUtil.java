package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.spawning.CobblemonSpawnPools;
import com.cobblemon.mod.common.api.spawning.detail.PokemonSpawnDetail;
import com.cobblemon.mod.common.api.spawning.detail.SpawnDetail;
import com.cobblemon.mod.common.api.spawning.position.SpawnablePosition;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.mojang.serialization.Codec;
import io.github.stainlessstasis.config.RaritiesConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;

public class RarityUtil {
    public enum Bucket implements StringRepresentable {
        COMMON("common", 0),
        UNCOMMON("uncommon", 1),
        RARE("rare", 2),
        ULTRA_RARE("ultra_rare", 3),
        NONE("none", 4);

        private final String name;
        private final int id;

        public static final IntFunction<Bucket> BY_ID = ByIdMap.continuous(
                b -> b.id,
                values(),
                ByIdMap.OutOfBoundsStrategy.ZERO
        );

        public static final Codec<Bucket> CODEC = Codec.INT.xmap(
                BY_ID::apply,
                b -> b.id
        );

        public static final StreamCodec<ByteBuf, Bucket> STREAM_CODEC = ByteBufCodecs.VAR_INT.map(
                BY_ID::apply,
                b -> b.id
        );

        Bucket(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public static Bucket fromString(String name) {
            return StringRepresentable.fromEnum(Bucket::values).byName(name.toLowerCase());
        }
    }

    public static Bucket getRarityBucket(PokemonEntity entity, SpawnablePosition spawnablePosition) {
        String speciesName = entity.getPokemon().getSpecies().getName().toLowerCase();
        List<SpawnDetail> matchingSpawns = CobblemonSpawnPools.WORLD_SPAWN_POOL.getDetails().stream()
                .filter(
                        detail -> detail instanceof PokemonSpawnDetail pokemonSpawnDetail
                                &&
                                pokemonSpawnDetail.getPokemon().getSpecies().equals(speciesName)
                )
                .filter(detail -> detail.isSatisfiedBy(spawnablePosition))
                .toList();

        Bucket bucket = Bucket.COMMON;
        if (!matchingSpawns.isEmpty()) {
            bucket = Bucket.fromString(matchingSpawns.getFirst().getBucket().getName());
        }

        return bucket;
    }

    public static boolean isLegendary(int dexId) {
        return getRaritiesConfig().legendaries().contains(dexId);
    }

    public static boolean isMythical(int dexId) {
        return getRaritiesConfig().mythicals().contains(dexId);
    }

    public static boolean isUltraBeast(int dexId) {
        return getRaritiesConfig().ultra_beasts().contains(dexId);
    }

    public static boolean isParadox(int dexId) {return getRaritiesConfig().paradox().contains(dexId);}

    public static boolean isStarter(int dexId) {
        return getRaritiesConfig().starters().contains(dexId);
    }

    private static RaritiesConfig getRaritiesConfig() {
        return CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getRaritiesConfig();
    }
}
