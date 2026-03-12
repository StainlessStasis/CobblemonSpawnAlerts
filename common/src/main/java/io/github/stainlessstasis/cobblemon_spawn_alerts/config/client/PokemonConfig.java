package io.github.stainlessstasis.cobblemon_spawn_alerts.config.client;

import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.alert.StatDisplayMode;

import java.util.LinkedHashMap;
import java.util.Map;

public record PokemonConfig (String configVersion, String[] comment, Map<String, PokemonSpecificConfig> pokemonConfigs) {
    public record PokemonSpecificConfig (
            boolean enabled,
            boolean alwaysAlert,
            boolean alertShiny,
            boolean alertHiddenAbility,
            boolean alertDespawned,
            boolean showLegendary,
            boolean showBucket,
            Map<String, StatDisplayMode> statDisplayModes,
            String customAlertMessage,
            String customAlertTooltip,
            String customAlertClickEvent,
            Map<String, String> sounds,
            String customAlertSound,
            boolean autoGlow,
            String glowColor,
            JourneymapConfig journeyMap,
            boolean sendWebhook
    ) {
        public static PokemonSpecificConfig createDefault() {
            Map<String, StatDisplayMode> statDisplayModes = new LinkedHashMap<>();
            statDisplayModes.put("level", StatDisplayMode.MAIN_MESSAGE);
            statDisplayModes.put("dex", StatDisplayMode.HOVER);
            statDisplayModes.put("ivs", StatDisplayMode.DISABLED);
            statDisplayModes.put("evs", StatDisplayMode.HOVER);
            statDisplayModes.put("nature", StatDisplayMode.DISABLED);
            statDisplayModes.put("ability", StatDisplayMode.DISABLED);
            statDisplayModes.put("gender", StatDisplayMode.HOVER);
            statDisplayModes.put("coords", StatDisplayMode.HOVER);
            statDisplayModes.put("biome", StatDisplayMode.BOTH);
            statDisplayModes.put("nearest_player", StatDisplayMode.DISABLED);

            Map<String, String> sounds = new LinkedHashMap<>();
            sounds.put("shiny", "");
            sounds.put("legendary", "");
            sounds.put("mythical", "");
            sounds.put("ultrabeast", "");
            sounds.put("paradox", "");
            sounds.put("starter", "");
            sounds.put("bucket", "");
            sounds.put("unregistered", "");
            sounds.put("uncaught", "");
            sounds.put("ivs", "");
            sounds.put("evs", "");
            sounds.put("despawned", "");

            return new PokemonSpecificConfig(
                    true, true, true, true, true, true, true,
                    statDisplayModes, "", "", "", sounds, "", false, "#FFFFFFFF",
                    new JourneymapConfig(false, "", "#FFFFFF", false),
                    false
            );
        }
    }

    public static PokemonConfig createDefault() {
        Map<String, PokemonSpecificConfig> defaults = new LinkedHashMap<>();
        defaults.put(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME, PokemonSpecificConfig.createDefault());
        return new PokemonConfig(
                CobblemonSpawnAlerts.MOD_VERSION,
                new String[]{
                        "This config is only on your client. It determines which Pokemon are alerted, and how that alert is displayed.",
                        "For details on using the config, please see the docs.",
                        "https://stainlessstasis.github.io/CSA-Docs/config/"
                },
                defaults
        );
    }

    public record JourneymapConfig(boolean enableWaypoint, String waypointName, String waypointHexColor, boolean persistent) {}
}