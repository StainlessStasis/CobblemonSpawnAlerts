package io.github.stainlessstasis.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record ModLoadedPacket(boolean loaded) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ModLoadedPacket> ID = new CustomPacketPayload.Type<>(ModPackets.MOD_LOADED);
    public static final StreamCodec<FriendlyByteBuf, ModLoadedPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL,
            ModLoadedPacket::loaded,
            ModLoadedPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

