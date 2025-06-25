package io.github.stainlessstasis.config;

public record MainConfig (
    boolean alertAllShinies,
    boolean alertAllLegendaries,
    boolean alertAllMythicals,
    boolean alertAllUltraBeasts
) {
    public static MainConfig createDefault() {
        return new MainConfig(true, true, true, true);
    }
}
