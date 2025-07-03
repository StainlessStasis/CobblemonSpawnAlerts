package io.github.stainlessstasis.config;

public record ServerConfig (
    boolean alertShinies,
    boolean alertLegendaries,
    boolean alertMythicals,
    boolean alertUltraBeasts,
    boolean alertParadox,
    boolean broadcastIVs,
    boolean broadcastEVs,
    boolean broadcastNature
) {
    public static ServerConfig createDefault() {
        return new ServerConfig(true, true, true, true, true, true, true, true);
    }
}
