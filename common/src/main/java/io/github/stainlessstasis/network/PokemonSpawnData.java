package io.github.stainlessstasis.network;

import io.github.stainlessstasis.util.RarityUtil;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.joml.Vector3f;

import java.util.UUID;

public record PokemonSpawnData(String pokemonName, UUID pokemonUUID, Vector3f position, int dexId, String nearestPlayerName, String biomeKey, String dimensionKey, RarityUtil.Bucket bucket) {
    public static final StreamCodec<FriendlyByteBuf, PokemonSpawnData> STREAM_CODEC = BigStreamCodecs.composite(
            ByteBufCodecs.STRING_UTF8, PokemonSpawnData::pokemonName,
            UUIDUtil.STREAM_CODEC, PokemonSpawnData::pokemonUUID,
            ByteBufCodecs.VECTOR3F, PokemonSpawnData::position,
            ByteBufCodecs.INT, PokemonSpawnData::dexId,
            ByteBufCodecs.STRING_UTF8, PokemonSpawnData::nearestPlayerName,
            ByteBufCodecs.STRING_UTF8, PokemonSpawnData::biomeKey,
            ByteBufCodecs.STRING_UTF8, PokemonSpawnData::dimensionKey,
            RarityUtil.Bucket.STREAM_CODEC, PokemonSpawnData::bucket,
            PokemonSpawnData::new
    );
}
