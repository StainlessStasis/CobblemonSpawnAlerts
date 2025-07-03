package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record AlertDataPacket(PokemonSpawnData spawnData, PokemonStats stats, PokemonTraits traits, String natureID, String genderID) implements CustomPacketPayload {
    public static final Type<AlertDataPacket> ID = new Type<>(ModPackets.ALERT_DATA);
    public static final StreamCodec<FriendlyByteBuf, AlertDataPacket> STREAM_CODEC = StreamCodec.composite(
            PokemonSpawnData.STREAM_CODEC, AlertDataPacket::spawnData,
            PokemonStats.STREAM_CODEC, AlertDataPacket::stats,
            PokemonTraits.STREAM_CODEC, AlertDataPacket::traits,
            ByteBufCodecs.STRING_UTF8, AlertDataPacket::natureID,
            ByteBufCodecs.STRING_UTF8, AlertDataPacket::genderID,
            AlertDataPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
