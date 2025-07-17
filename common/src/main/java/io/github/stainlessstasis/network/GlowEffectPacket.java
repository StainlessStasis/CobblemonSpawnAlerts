package io.github.stainlessstasis.network;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

/**
 * Packet sent from client to server to request a glowing effect on a Pokemon
 */
public record GlowEffectPacket(UUID pokemonUUID, int durationSeconds) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<GlowEffectPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "glow_effect"));
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "glow_effect");
    
    public static final StreamCodec<FriendlyByteBuf, GlowEffectPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(
            (buf, uuid) -> {
                buf.writeLong(uuid.getMostSignificantBits());
                buf.writeLong(uuid.getLeastSignificantBits());
            },
            buf -> new UUID(buf.readLong(), buf.readLong())
        ), GlowEffectPacket::pokemonUUID,
        StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), GlowEffectPacket::durationSeconds,
        GlowEffectPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
