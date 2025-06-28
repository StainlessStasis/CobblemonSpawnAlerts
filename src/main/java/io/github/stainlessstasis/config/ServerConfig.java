package io.github.stainlessstasis.config;

public record ServerConfig (
    boolean broadcastIVs,
    boolean broadcastNature
) {
    public static ServerConfig createDefault() {
        return new ServerConfig(true, true);
    }
}
