package io.github.stainlessstasis.cobblemon_spawn_alerts.network;

import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record PokemonStats(int level, IVs ivs, EVs evYield) {
    public static final StreamCodec<FriendlyByteBuf, PokemonStats> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PokemonStats::level,
            ByteBufCodecs.fromCodec(IVs.getCODEC()), PokemonStats::ivs,
            ByteBufCodecs.fromCodec(EVs.getCODEC()), PokemonStats::evYield,
            PokemonStats::new
    );
}
