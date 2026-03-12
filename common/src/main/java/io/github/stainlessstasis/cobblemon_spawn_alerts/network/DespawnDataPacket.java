package io.github.stainlessstasis.cobblemon_spawn_alerts.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record DespawnDataPacket(AlertDataPacket alertData, String despawnReason) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<DespawnDataPacket> ID = new CustomPacketPayload.Type<>(ModPackets.DESPAWN_DATA);
    public static final StreamCodec<FriendlyByteBuf, DespawnDataPacket> STREAM_CODEC = StreamCodec.composite(
            AlertDataPacket.STREAM_CODEC, DespawnDataPacket::alertData,
            ByteBufCodecs.STRING_UTF8, DespawnDataPacket::despawnReason,
            DespawnDataPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}
