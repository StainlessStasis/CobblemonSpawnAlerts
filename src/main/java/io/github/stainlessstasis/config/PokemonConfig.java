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
            boolean showCoordinates,
            boolean showInfoAsHover,
            String customAlertMessage
    ) {
        public static PokemonSpecificConfig createDefault() {
            return new PokemonSpecificConfig(true, true, true, true, true,
                    false, false, false, false, "");
        }
    }

    public static PokemonConfig createDefault() {
        Map<String, PokemonSpecificConfig> defaults = new HashMap<>();
        defaults.put("bidoof", new PokemonSpecificConfig(
                false, true, true, true, true,
                false, false, false, false,
                "<hover:show_text:\"<green>This is a hover message!\"><blue>This is an <rainbow>example</rainbow> of how you can use <gradient:blue:green>MiniMessage formatting</gradient> to make <u><b>custom alert messages</b></u>!</hover>"
                ));
        return new PokemonConfig(defaults);
    }
}