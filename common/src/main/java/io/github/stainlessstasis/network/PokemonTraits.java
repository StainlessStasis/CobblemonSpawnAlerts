package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PokemonTraits(String natureID, String abilityID, String genderID) {
    public static final StreamCodec<FriendlyByteBuf, PokemonTraits> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PokemonTraits::natureID,
            ByteBufCodecs.STRING_UTF8, PokemonTraits::abilityID,
            ByteBufCodecs.STRING_UTF8, PokemonTraits::genderID,
            PokemonTraits::new
    );
}
