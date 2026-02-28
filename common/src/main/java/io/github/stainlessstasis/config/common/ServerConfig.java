package io.github.stainlessstasis.config.common;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.RarityUtil;

import java.util.Set;

public record ServerConfig (
    String configVersion,
    String[] comment,
    boolean enableSpawnCommandAlerts,
    boolean alertShinies,
    boolean broadcastShiny,
    boolean alertLegendaries,
    boolean alertMythicals,
    boolean alertUltraBeasts,
    boolean alertParadox,
    boolean alertStarters,
    boolean alertHiddenAbility,
    Set<RarityUtil.Bucket> bucketsToAlert,
    boolean broadcastBucket,
    boolean broadcastIVs,
    boolean broadcastEVs,
    boolean broadcastNature,
    boolean broadcastAbility
) {
    public static ServerConfig createDefault() {
        return new ServerConfig(
                CobblemonSpawnAlerts.MOD_VERSION,
                new String[]{
                        "This config is only used if you are in singleplayer or hosting a server (including LAN).",
                        "It determines which Pokemon are sent to all players, sending info about those Pokemon to clients.",
                        "It does NOT determine how each individual client displays the data sent from the server. Each client is responsible for displaying its own alerts.",
                        "For details on using the config, please see the docs.",
                        "https://stainlessstasis.github.io/CSA-Docs/config/"
                },
                false, true, true, true, true, true, true,
                false, false, Set.of(RarityUtil.Bucket.ULTRA_RARE), true,
                true, true, true, true
        );
    }
}
