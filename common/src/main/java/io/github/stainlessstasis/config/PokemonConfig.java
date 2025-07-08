package io.github.stainlessstasis.config;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.alert.StatDisplayMode;

import java.util.LinkedHashMap;
import java.util.Map;

public record PokemonConfig (float configVersion, Map<String, PokemonSpecificConfig> pokemonConfigs){
    public record PokemonSpecificConfig (
            boolean enabled,
            boolean alwaysAlert,
            boolean alertShiny,
            boolean showLegendary,
            Map<String, StatDisplayMode> statDisplayModes,
            String customAlertMessage,
            Map<String, String> sounds,
            String customAlertSound
    ) {
        public static PokemonSpecificConfig createDefault() {
            Map<String, StatDisplayMode> statDisplayModes = new LinkedHashMap<>();
            statDisplayModes.put("level", StatDisplayMode.MAIN_MESSAGE);
            statDisplayModes.put("ivs", StatDisplayMode.DISABLED);
            statDisplayModes.put("evs", StatDisplayMode.DISABLED);
            statDisplayModes.put("nature", StatDisplayMode.DISABLED);
            statDisplayModes.put("ability", StatDisplayMode.DISABLED);
            statDisplayModes.put("gender", StatDisplayMode.HOVER);
            statDisplayModes.put("coordinates", StatDisplayMode.HOVER);
            statDisplayModes.put("biome", StatDisplayMode.MAIN_MESSAGE);

            Map<String, String> sounds = new LinkedHashMap<>();
            sounds.put("shiny", "");
            sounds.put("legendary", "");
            sounds.put("mythical", "");
            sounds.put("ultrabeast", "");
            sounds.put("paradox", "");
            sounds.put("unregistered", "");
            sounds.put("uncaught", "");

            return new PokemonSpecificConfig(true, true, true, true, statDisplayModes, "", sounds, "");
        }
    }

    public static PokemonConfig createDefault() {
        Map<String, PokemonSpecificConfig> defaults = new LinkedHashMap<>();
        defaults.put(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME, PokemonSpecificConfig.createDefault());
        return new PokemonConfig(1.7f, defaults);
    }
}