package io.github.stainlessstasis.network;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record PokemonDataPacket(int pokemonNetworkID, IVs ivs, EVs evYield, Nature nature, Ability ability) implements CustomPacketPayload {
    public static final Type<PokemonDataPacket> ID = new Type<>(ModPackets.POKEMON_DATA);
    public static final StreamCodec<FriendlyByteBuf, PokemonDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PokemonDataPacket::pokemonNetworkID,
            ByteBufCodecs.fromCodec(IVs.getCODEC()),
            PokemonDataPacket::ivs,
            ByteBufCodecs.fromCodec(EVs.getCODEC()),
            PokemonDataPacket::evYield,
            ByteBufCodecs.fromCodec(Nature.getBY_IDENTIFIER_CODEC()),
            PokemonDataPacket::nature,
            ByteBufCodecs.fromCodec(Ability.getCODEC()),
            PokemonDataPacket::ability,
            PokemonDataPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
