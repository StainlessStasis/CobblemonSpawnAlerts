package io.github.stainlessstasis.config;

import java.util.HashMap;

public class Config extends HashMap<String, Config.PokemonSpecificConfig> {
    public static final PokemonSpecificConfig DEFAULT_POKEMON_CONFIG_INSTANCE = new PokemonSpecificConfig(true, true, true, "");

    public Config(boolean isFresh) {
        if (isFresh) {
            put("arceus", DEFAULT_POKEMON_CONFIG_INSTANCE);
        }
    }

    public Config() {}

    public static class PokemonSpecificConfig {
        public boolean enabled = true;
        public boolean alwaysAlert = true;
        public boolean alertShiny = false;
        public String customAlertMessage = "";

        public PokemonSpecificConfig() {}

        public PokemonSpecificConfig(boolean enabled, boolean alwaysAlert, boolean alertShiny, String customAlertMessage) {
            this.enabled = enabled;
            this.alwaysAlert = alwaysAlert;
            this.alertShiny = alertShiny;
            this.customAlertMessage = customAlertMessage;
        }
    }
}