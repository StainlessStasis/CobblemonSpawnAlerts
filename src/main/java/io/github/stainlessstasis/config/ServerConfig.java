package io.github.stainlessstasis.config;

public record ServerConfig (
    boolean broadcastIVs,
    boolean broadcastNature,
    boolean broadcastAbility
) {
    public static ServerConfig createDefault() {
        return new ServerConfig(true, true, true);
    }
}
