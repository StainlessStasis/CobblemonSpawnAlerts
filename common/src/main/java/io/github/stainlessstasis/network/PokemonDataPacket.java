package io.github.stainlessstasis.network;

import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record PokemonDataPacket(int pokemonNetworkID, IVs ivs, Nature nature) implements CustomPacketPayload {
    public static final Type<PokemonDataPacket> ID = new Type<>(ModPackets.POKEMON_DATA);
//    public static final Codec<PokemonDataPacket> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//            UUIDUtil.CODEC.fieldOf("pokemonUUID").forGetter(PokemonDataPacket::pokemonUUID),
//            IVs.getCODEC().fieldOf("ivs").forGetter(PokemonDataPacket::ivs),
//            Nature.getBY_IDENTIFIER_CODEC().fieldOf("nature").forGetter(PokemonDataPacket::nature)
//    ).apply(instance, PokemonDataPacket::new));
    public static final StreamCodec<FriendlyByteBuf, PokemonDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            PokemonDataPacket::pokemonNetworkID,
            ByteBufCodecs.fromCodec(IVs.getCODEC()),
            PokemonDataPacket::ivs,
            ByteBufCodecs.fromCodec(Nature.getBY_IDENTIFIER_CODEC()),
            PokemonDataPacket::nature,
            PokemonDataPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
