package io.github.stainlessstasis.util;

import java.util.Set;

public class RarityUtil {
    // had ai generate this instead of typing it all manually so sorry if some of it is wrong

    private static final Set<Integer> LEGENDARIES = Set.of(
            144, // Articuno
            145, // Zapdos
            146, // Moltres
            150, // Mewtwo
            243, // Raikou
            244, // Entei
            245, // Suicune
            249, // Lugia
            250, // Ho-Oh
            377, // Regirock
            378, // Regice
            379, // Registeel
            380, // Latias
            381, // Latios
            382, // Kyogre
            383, // Groudon
            384, // Rayquaza
            480, // Uxie
            481, // Mesprit
            482, // Azelf
            483, // Dialga
            484, // Palkia
            487, // Giratina
            485, // Heatran
            486, // Regigigas
            488, // Cresselia
            638, // Cobalion
            639, // Terrakion
            640, // Virizion
            641, // Tornadus
            642, // Thundurus
            645, // Landorus
            643, // Reshiram
            644, // Zekrom
            646, // Kyurem
            716, // Xerneas
            717, // Yveltal
            718, // Zygarde
            772, // Type: Null
            773, // Silvally
            785, // Tapu Koko
            786, // Tapu Lele
            787, // Tapu Bulu
            788, // Tapu Fini
            789, // Cosmog
            790, // Cosmoem
            791, // Solgaleo
            792, // Lunala
            793, // Necrozma
            888, // Zacian
            889, // Zamazenta
            890, // Eternatus
            891, // Kubfu
            892, // Urshifu
            894, // Regieleki
            895, // Regidrago
            896, // Glastrier
            897, // Spectrier
            898, // Calyrex
            905, // Enamorus
            1001, // Wo-Chien
            1002, // Chien-Pao
            1003, // Ting-Lu
            1004, // Chi-Yu
            1007, // Koraidon
            1008, // Miraidon
            1017, // Ogerpon
            1013, // Okidogi
            1014, // Munkidori
            1015, // Fezandipiti
            1024  // Terapagos
    );

    private static final Set<Integer> MYTHICALS = Set.of(
            151, // Mew
            251, // Celebi
            385, // Jirachi
            386, // Deoxys
            489, // Manaphy
            490, // Phione
            491, // Darkrai
            492, // Shaymin
            493, // Arceus
            494, // Victini
            647, // Keldeo
            648, // Meloetta
            649, // Genesect
            719, // Diancie
            720, // Hoopa
            721, // Volcanion
            801, // Magearna
            802, // Marshadow
            807, // Zeraora
            808, // Meltan
            809, // Melmetal
            893, // Zarude
            1025 // Pecharunt
    );

    private static final Set<Integer> ULTRA_BEASTS = Set.of(
            794, // Nihilego
            795, // Buzzwole
            796, // Pheromosa
            797, // Xurkitree
            798, // Celesteela
            799, // Kartana
            800, // Guzzlord
            806, // Blacephalon
            805, // Stakataka
            803, // Poipole
            804  // Naganadel
    );

    private static final Set<Integer> PARADOX = Set.of(
            984, // Great Tusk
            985, // Scream Tail
            986, // Brute Bonnet
            987, // Flutter Mane
            988, // Slither Wing
            989, // Sandy Shocks
            990, // Roaring Moon
            1009, // Walking Wake
            1010, // Gouging Fire
            1011, // Raging Bolt
            991, // Iron Treads
            992, // Iron Bundle
            993, // Iron Hands
            994, // Iron Jugulis
            995, // Iron Moth
            996, // Iron Thorns
            997, // Iron Valiant
            1012, // Iron Leaves
            1020, // Iron Boulder
            1021  // Iron Crown
    );

    public static boolean isLegendary(int dexId) {
        return LEGENDARIES.contains(dexId);
    }

    public static boolean isMythical(int dexId) {
        return MYTHICALS.contains(dexId);
    }

    public static boolean isUltraBeast(int dexId) {
        return ULTRA_BEASTS.contains(dexId);
    }

    public static boolean isParadox(int dexId) {return PARADOX.contains(dexId);}
}
