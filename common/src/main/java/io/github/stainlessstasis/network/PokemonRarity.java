package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PokemonRarity(boolean isShiny, boolean isLegendary, boolean isMythical, boolean isUltraBeast, boolean isParadox, boolean isStarter) {
    public static final StreamCodec<FriendlyByteBuf, PokemonRarity> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PokemonRarity::isShiny,
            ByteBufCodecs.BOOL, PokemonRarity::isLegendary,
            ByteBufCodecs.BOOL, PokemonRarity::isMythical,
            ByteBufCodecs.BOOL, PokemonRarity::isUltraBeast,
            ByteBufCodecs.BOOL, PokemonRarity::isParadox,
            ByteBufCodecs.BOOL, PokemonRarity::isStarter,
            PokemonRarity::new
    );
}
