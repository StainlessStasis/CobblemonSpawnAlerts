package io.github.stainlessstasis.network;

import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.UUID;

public record PokemonDataPacket(UUID pokemonUUID, IVs ivs, Nature nature) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<PokemonDataPacket> ID = new CustomPacketPayload.Type<>(ModPackets.POKEMON_DATA);
//    public static final Codec<PokemonDataPacket> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//            UUIDUtil.CODEC.fieldOf("pokemonUUID").forGetter(PokemonDataPacket::pokemonUUID),
//            IVs.getCODEC().fieldOf("ivs").forGetter(PokemonDataPacket::ivs),
//            Nature.getBY_IDENTIFIER_CODEC().fieldOf("nature").forGetter(PokemonDataPacket::nature)
//    ).apply(instance, PokemonDataPacket::new));
    public static final StreamCodec<FriendlyByteBuf, PokemonDataPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,
            PokemonDataPacket::pokemonUUID,
            ByteBufCodecs.fromCodec(IVs.getCODEC()),
            PokemonDataPacket::ivs,
            ByteBufCodecs.fromCodec(Nature.getBY_IDENTIFIER_CODEC()),
            PokemonDataPacket::nature,
            PokemonDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
