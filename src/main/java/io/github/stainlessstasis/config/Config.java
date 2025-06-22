package io.github.stainlessstasis.config;

import java.util.HashMap;

public class Config extends HashMap<String, Config.PokemonSpecificConfig> {
    public static final PokemonSpecificConfig DEFAULT_POKEMON_CONFIG_INSTANCE = new PokemonSpecificConfig(true, true, true, false, "");

    public Config(boolean isFresh) {
        if (isFresh) {
            put("arceus", DEFAULT_POKEMON_CONFIG_INSTANCE);
        }
    }

    public Config() {}

    public static class PokemonSpecificConfig {
        public boolean enabled;
        public boolean alwaysAlert;
        public boolean alertShiny;
        public boolean showLevel;
        public String customAlertMessage;

        public PokemonSpecificConfig(boolean enabled, boolean alwaysAlert, boolean alertShiny, boolean showLevel, String customAlertMessage) {
            this.enabled = enabled;
            this.alwaysAlert = alwaysAlert;
            this.alertShiny = alertShiny;
            this.showLevel = showLevel;
            this.customAlertMessage = customAlertMessage;
        }
    }
}