package io.github.stainlessstasis.config;

import java.util.HashMap;
import java.util.Map;

public record PokemonConfig (Map<String, PokemonSpecificConfig> pokemonConfigs){

    public record PokemonSpecificConfig (
            boolean enabled,
            boolean alwaysAlert,
            boolean alertShiny,
            boolean showLegendary,
            boolean showLevel,
            boolean showIVs,
            boolean showNature,
            boolean showGender,
            boolean showCoordinates,
            boolean showInfoAsHover,
            String customAlertMessage
    ) {
        public static PokemonSpecificConfig createDefault() {
            return new PokemonSpecificConfig(true, true, true, true, true,
                    false, false, false, false, false, "");
        }
    }

    public static PokemonConfig createDefault() {
        Map<String, PokemonSpecificConfig> defaults = new HashMap<>();
        defaults.put("default (You can modify anything BELOW this, but dont delete it!)", PokemonSpecificConfig.createDefault());
        defaults.put("bidoof", new PokemonSpecificConfig(
                false, true, true, true, true,
                false, false, false, false, false,
                ""
                ));
        return new PokemonConfig(defaults);
    }
}