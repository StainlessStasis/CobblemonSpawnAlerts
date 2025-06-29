package io.github.stainlessstasis.config;

public record MainConfig (
    boolean alertAllShinies,
    boolean alertAllLegendaries,
    boolean alertAllMythicals,
    boolean alertAllUltraBeasts,
    boolean alertAllParadox,
    boolean alertAllNotInDex,
    boolean alertAllUncaught
) {
    public static MainConfig createDefault() {
        return new MainConfig(true, true, true, true, true,false, false);
    }
}
