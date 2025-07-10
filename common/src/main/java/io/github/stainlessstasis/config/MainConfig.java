package io.github.stainlessstasis.config;

public record MainConfig (
    String configVersion,
    boolean multiplayerWarning,
    boolean alertAllShinies,
    boolean alertAllLegendaries,
    boolean alertAllMythicals,
    boolean alertAllUltraBeasts,
    boolean alertAllParadox,
    boolean alertAllNotInDex,
    boolean alertAllUncaught,
    boolean alertEverything,
    IVHunting ivHunting,
    EVHunting evHunting
) {
    public static MainConfig createDefault() {
        return new MainConfig("1.8.1", true, true, true, true,
                true, true,false, false, false,
                IVHunting.createDefault(), EVHunting.createDefault()
                );
    }

    public record IVHunting(boolean enabled, boolean requireAllMinimumsMet, int minPerfectIVs, int minHp, int minAtk, int minDef, int minSpAtk, int minSpDef, int minSpeed) {
        public static IVHunting createDefault() {
            return new IVHunting(false, true, 0, 0, 0, 0, 0, 0, 0);
        }
    }

    public record EVHunting(boolean enabled, int minHp, int minAtk, int minDef, int minSpAtk, int minSpDef, int minSpeed) {
        public static EVHunting createDefault() {
            return new EVHunting(false, 0, 0, 0, 0, 0, 0);
        }
    }
}
