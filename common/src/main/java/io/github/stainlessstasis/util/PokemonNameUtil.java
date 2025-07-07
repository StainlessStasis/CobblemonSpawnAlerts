package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.network.chat.Component;

public class PokemonNameUtil {
    public static String getName(Pokemon pokemon) {
        return pokemon.getSpecies().getName();
    }

    public static String getTranslatedName(String name) {
        return Component.translatable(name).getString();
    }

    public static String getTranslatedName(Pokemon pokemon) {
        return Component.translatable(getTranslationKey(pokemon)).getString();
    }

    public static String getTranslationKey (Pokemon pokemon) {
        return pokemon.getSpecies().getTranslatedName().getString();
    }


    public static String fixName(String name) {
        return name.toLowerCase().replaceAll("[ _-]", "");
    }
}
