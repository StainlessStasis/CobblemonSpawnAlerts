package io.github.stainlessstasis.config;

public record ServerConfig (
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
        return new ServerConfig(true, true, true, true, true, false, false, true, true, true, true);
    }
}
