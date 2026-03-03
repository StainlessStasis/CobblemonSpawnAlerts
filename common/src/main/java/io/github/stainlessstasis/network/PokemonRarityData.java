package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PokemonRarityData(boolean isShiny, boolean isLegendary, boolean isMythical, boolean isUltraBeast, boolean isParadox, boolean isStarter) {
    public static final StreamCodec<FriendlyByteBuf, PokemonRarityData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PokemonRarityData::isShiny,
            ByteBufCodecs.BOOL, PokemonRarityData::isLegendary,
            ByteBufCodecs.BOOL, PokemonRarityData::isMythical,
            ByteBufCodecs.BOOL, PokemonRarityData::isUltraBeast,
            ByteBufCodecs.BOOL, PokemonRarityData::isParadox,
            ByteBufCodecs.BOOL, PokemonRarityData::isStarter,
            PokemonRarityData::new
    );
}
