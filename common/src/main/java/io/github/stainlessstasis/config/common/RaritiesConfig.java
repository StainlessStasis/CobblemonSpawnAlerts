package io.github.stainlessstasis.config.common;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public record RaritiesConfig(
        String configVersion,
        String[] comment,
        Set<Integer> legendaries,
        Set<Integer> mythicals,
        Set<Integer> ultra_beasts,
        Set<Integer> paradox,
        Set<Integer> starters
) {
    private static Set<Integer> orderedSet(Integer... values) {
        return new LinkedHashSet<>(Arrays.asList(values));
    }

    public static RaritiesConfig createDefault() {
        return new RaritiesConfig(
                CobblemonSpawnAlerts.MOD_VERSION,
                new String[]{
                        "This config is common between server and client. It determines which Pokemon are classified as legendary, starter, etc.",
                        "Servers only reference this config for STARTERS, since Cobblemon provides labels for all other groups.",
                        "Clients reference everything within this config, since labels don't exist clientside.",
                        "For details on using the config, please see the docs.",
                        "https://stainlessstasis.github.io/CSA-Docs/config/"
                },
                orderedSet(
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
                        485, // Heatran
                        486, // Regigigas
                        487, // Giratina
                        488, // Cresselia
                        638, // Cobalion
                        639, // Terrakion
                        640, // Virizion
                        641, // Tornadus
                        642, // Thundurus
                        643, // Reshiram
                        644, // Zekrom
                        645, // Landorus
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
                        800, // Necrozma
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
                        1014, // Okidogi
                        1015, // Munkidori
                        1016, // Fezandipiti
                        1017, // Ogerpon
                        1024  // Terapagos
                ),
                orderedSet(
                        151, // Mew
                        251, // Celebi
                        385, // Jirachi
                        386, // Deoxys
                        489, // Phione
                        490, // Manaphy
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
                ),
                orderedSet(
                        793, // Nihilego
                        794, // Buzzwole
                        795, // Pheromosa
                        796, // Xurkitree
                        797, // Celesteela
                        798, // Kartana
                        799, // Guzzlord
                        803, // Poipole
                        804, // Naganadel
                        805, // Stakataka
                        806  // Blacephalon
                ),
                orderedSet(
                        984, // Great Tusk
                        985, // Scream Tail
                        986, // Brute Bonnet
                        987, // Flutter Mane
                        988, // Slither Wing
                        989, // Sandy Shocks
                        1005, // Roaring Moon
                        1009, // Walking Wake
                        1020, // Gouging Fire
                        1021, // Raging Bolt
                        990, // Iron Treads
                        991, // Iron Bundle
                        992, // Iron Hands
                        993, // Iron Jugulis
                        994, // Iron Moth
                        995, // Iron Thorns
                        1006, // Iron Valiant
                        1010, // Iron Leaves
                        1022, // Iron Boulder
                        1023  // Iron Crown
                ),
                orderedSet(
                        // Generation I
                        1, 2, 3,    // Bulbasaur, Ivysaur, Venusaur
                        4, 5, 6,    // Charmander, Charmeleon, Charizard
                        7, 8, 9,    // Squirtle, Wartortle, Blastoise
                        // Generation II
                        152, 153, 154, // Chikorita, Bayleef, Meganium
                        155, 156, 157, // Cyndaquil, Quilava, Typhlosion
                        158, 159, 160, // Totodile, Croconaw, Feraligatr
                        // Generation III
                        252, 253, 254, // Treecko, Grovyle, Sceptile
                        255, 256, 257, // Torchic, Combusken, Blaziken
                        258, 259, 260, // Mudkip, Marshtomp, Swampert
                        // Generation IV
                        387, 388, 389, // Turtwig, Grotle, Torterra
                        390, 391, 392, // Chimchar, Monferno, Infernape
                        393, 394, 395, // Piplup, Prinplup, Empoleon
                        // Generation V
                        495, 496, 497, // Snivy, Servine, Serperior
                        498, 499, 500, // Tepig, Pignite, Emboar
                        501, 502, 503, // Oshawott, Dewott, Samurott
                        // Generation VI
                        650, 651, 652, // Chespin, Quilladin, Chesnaught
                        653, 654, 655, // Fennekin, Braixen, Delphox
                        656, 657, 658, // Froakie, Frogadier, Greninja
                        // Generation VII
                        722, 723, 724, // Rowlet, Dartrix, Decidueye
                        725, 726, 727, // Litten, Torracat, Incineroar
                        728, 729, 730, // Popplio, Brionne, Primarina
                        // Generation VIII
                        810, 811, 812, // Grookey, Thwackey, Rillaboom
                        813, 814, 815, // Scorbunny, Raboot, Cinderace
                        816, 817, 818, // Sobble, Drizzile, Inteleon
                        // Generation IX (Scarlet & Violet)
                        906, 907, 908, // Sprigatito, Floragato, Meowscarada
                        909, 910, 911, // Fuecoco, Crocalor, Skeledirge
                        912, 913, 914  // Quaxly, Quaxwell, Quaquaval
                )
        );
    }
}
