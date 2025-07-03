package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;

public class PokemonNameUtil {
    public static String getName(Pokemon pokemon) {
        return pokemon.getSpecies().getName();
    }

    public static String getName(PokemonEntity pokemon) {
        return pokemon.getPokemon().getSpecies().getName();
    }

    public static String fixName(String name) {
        return name.toLowerCase().replaceAll("[ _-]", "");
    }
}
