package io.github.stainlessstasis.network;

import com.cobblemon.mod.common.api.abilities.Ability;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.alert.AlertHandler;
import io.github.stainlessstasis.util.RarityUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class PacketHandlers {
    public static void handlePokemonDataPacket(int pokemonNetworkID, IVs ivs, EVs evYield, Nature nature, Ability ability, RarityUtil.Bucket bucket) {
        if (!(Minecraft.getInstance().level instanceof ClientLevel level)) {
            return;
        }
        if (!(level.getEntity(pokemonNetworkID) instanceof PokemonEntity pokemonEntity)) {
            return;
        }

        Pokemon pokemon = pokemonEntity.getPokemon();
        pokemon.setIvs$common(ivs);
        pokemon.setNature(nature);
        pokemon.setAbility$common(ability);
        AlertHandler.alertClientside(pokemonEntity, evYield, bucket);
    }

    public static void handleAlertDataPacket(AlertDataPacket payload) {
        AlertHandler.alert(payload);
    }

    public static void handleDespawnDataPacket(DespawnDataPacket payload) {
        AlertHandler.alertDespawned(payload);
    }
}
