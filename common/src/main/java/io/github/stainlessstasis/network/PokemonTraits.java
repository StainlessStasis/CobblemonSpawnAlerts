package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PokemonTraits(boolean isShiny, boolean isLegendary, boolean isMythical, boolean isUltraBeast, boolean isParadox, boolean isStarter) {
    public static final StreamCodec<FriendlyByteBuf, PokemonTraits> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PokemonTraits::isShiny,
            ByteBufCodecs.BOOL, PokemonTraits::isLegendary,
            ByteBufCodecs.BOOL, PokemonTraits::isMythical,
            ByteBufCodecs.BOOL, PokemonTraits::isUltraBeast,
            ByteBufCodecs.BOOL, PokemonTraits::isParadox,
            ByteBufCodecs.BOOL, PokemonTraits::isStarter,
            PokemonTraits::new
    );
}
