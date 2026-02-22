package io.github.stainlessstasis.config;

public record ServerConfig (
    String[] comment,
    boolean enableSpawnCommandAlerts,
    boolean alertShinies,
    boolean alertLegendaries,
    boolean alertMythicals,
    boolean alertUltraBeasts,
    boolean alertParadox,
    boolean alertStarters,
    boolean alertHiddenAbility,
    boolean broadcastIVs,
    boolean broadcastEVs,
    boolean broadcastNature,
    boolean broadcastAbility
) {
    public static ServerConfig createDefault() {
        return new ServerConfig(
                new String[]{
                        "This config is only used if you are in singleplayer or hosting a server (including LAN).",
                        "It determines which Pokemon are sent to all players, sending info about those Pokemon to clients.",
                        "It does NOT determine how each individual client displays the data sent from the server. Each client is responsible for displaying its own alerts.",
                        "For documentation on using the config, please see the Modrinth or GitHub for the mod.",
                        "https://modrinth.com/mod/cobblemon-spawn-alerts",
                        "https://github.com/StainlessStasis/CobblemonSpawnAlerts"
                },
                false, true, true, true, true, true,
                false, false, true, true, true, true
        );
    }
}
