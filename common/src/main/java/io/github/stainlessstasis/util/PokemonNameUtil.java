package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.client.resources.language.I18n;

public class PokemonNameUtil {
    public static String getName(Pokemon pokemon) {
        return pokemon.getSpecies().getName();
    }

    public static String getTranslatedName(String name) {
        return I18n.get(name);
    }

    public static String getTranslatedName(Pokemon pokemon) {
        return I18n.get(getTranslationKey(pokemon));
    }

    public static String getTranslationKey (Pokemon pokemon) {
        return pokemon.getSpecies().getTranslatedName().getString();
    }


    public static String fixName(String name) {
        return name.toLowerCase().replaceAll("[ _-]", "");
    }
}
