package io.github.stainlessstasis.config;

import java.util.HashMap;

public class Config extends HashMap<String, Config.PokemonSpecificConfig> {
    public static final PokemonSpecificConfig DEFAULT_POKEMON_CONFIG_INSTANCE =
            new PokemonSpecificConfig(false, true, true, true, false, false, false, false, "");

    public Config(boolean isFresh) {
        if (isFresh) {
            put("bidoof", DEFAULT_POKEMON_CONFIG_INSTANCE);
            put("arceus",
                    new PokemonSpecificConfig(true, true, true, true, false, false, false, false,
    "<hover:show_text:\"<green>This is a hover message!\"><blue>This is an <rainbow>example</rainbow> of how you can use <gradient:blue:green>MiniMessage formatting</gradient> to make <u><b>custom alert messages</b></u>!</hover>"));
        }
    }

    public Config() {}

    public static class PokemonSpecificConfig {
        public boolean enabled;
        public boolean alwaysAlert;
        public boolean alertShiny;
        public boolean showLevel;
        public boolean showIVs;
        public boolean showNature;
        public boolean showCoordinates;
        public boolean showInfoAsHover;
        public String customAlertMessage;

        public PokemonSpecificConfig(boolean enabled, boolean alwaysAlert, boolean alertShiny, boolean showLevel, boolean showNature,
                                     boolean showIVs, boolean showCoordinates, boolean showInfoAsHover, String customAlertMessage) {
            this.enabled = enabled;
            this.alwaysAlert = alwaysAlert;
            this.alertShiny = alertShiny;
            this.showLevel = showLevel;
            this.showIVs = showIVs;
            this.showNature = showNature;
            this.showCoordinates = showCoordinates;
            this.showInfoAsHover = showInfoAsHover;
            this.customAlertMessage = customAlertMessage;
        }
    }
}