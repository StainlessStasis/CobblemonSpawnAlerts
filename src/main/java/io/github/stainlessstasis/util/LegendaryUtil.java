package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.pokemon.Species;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LegendaryUtil {
    // Generation 1
    // Assuming Terapagos is Legendary
    private static final Set<String> LEGENDARIES = Set.of("articuno", "zapdos", "moltres", "mewtwo",
            "raikou", "entei", "suicune", "lugia", "ho-oh",
            "regirock", "regice", "registeel", "latias", "latios", "kyogre", "groudon", "rayquaza",
            "uxie", "mesprit", "azelf", "dialga", "palkia", "giratina", "heatran", "regigigas", "cresselia",
            "cobalion", "terrakion", "virizion", "tornadus", "thundurus", "landorus", "reshiram", "zekrom", "kyurem",
            "xerneas", "yveltal", "zygarde",
            "type: null", "silvally", "tapu koko", "tapu lele", "tapu bulu", "tapu fini", "cosmog", "cosmoem", "solgaleo", "lunala", "necrozma",
            "zacian", "zamazenta", "eternatus", "kubfu", "urshifu", "regieleki", "regidrago", "glastrier", "spectrier", "calyrex", "enamorus",
            "wo-chien", "chien-pao", "ting-lu", "chi-yu", "koraidon", "miraidon", "ogerpon", "okidogi", "munkidori", "fezandipiti", "terapagos");

    private static final Set<String> MYTHICALS = Set.of(
            "mew",
            "celebi",
            "jirachi", "deoxys",
            "manaphy", "phione", "darkrai", "shaymin", "arceus",
            "victini", "keldeo", "meloetta", "genesect",
            "diancie", "hoopa", "volcanion",
            "magearna", "marshadow", "zeraora",
            "meltan", "melmetal", "zarude",
            "pecharunt"
    );

    private static final Set<String> ULTRA_BEASTS = Set.of(
            "nihilego", "buzzwole", "pheromosa", "xurkitree", "celesteela", "kartana",
            "guzzlord", "blacephalon", "stakataka", "poipole", "naganadel"
    );


    // This method should be called once, after Cobblemon has initialized its data.
    // A good place is usually within your main client mod class's onInitializeClient() method.
    public static void initializePokemonSets() {
    }

    public static boolean isLegendary(String name) {
        return LEGENDARIES.contains(name);
    }

    public static boolean isMythical(String name) {
        return MYTHICALS.contains(name);
    }

    public static boolean isUltraBeast(String name) {
        return ULTRA_BEASTS.contains(name);
    }
}
