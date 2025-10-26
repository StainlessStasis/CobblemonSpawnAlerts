package io.github.stainlessstasis.config;

public record MainConfig (
    String configVersion,
    boolean multiplayerWarning,
    boolean alertAllShinies,
    boolean alertAllLegendaries,
    boolean alertAllMythicals,
    boolean alertAllUltraBeasts,
    boolean alertAllParadox,
    boolean alertAllStarter,
    boolean alertAllNotInDex,
    boolean alertAllUncaught,
    boolean alertEverything,
    IVHunting ivHunting,
    EVHunting evHunting,
    LevelFilter levelFilter
) {
    public static MainConfig createDefault() {
        return new MainConfig("1.9", true, true, true, true,
                true, true,false, false, false, false,
                IVHunting.createDefault(), EVHunting.createDefault(), LevelFilter.createDefault()
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

    public record LevelFilter(boolean enabled, int minLevel, int maxLevel) {
        public static LevelFilter createDefault() {return new LevelFilter(false, 1, 100);}
    }
}
