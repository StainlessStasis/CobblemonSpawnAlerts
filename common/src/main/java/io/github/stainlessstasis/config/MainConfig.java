package io.github.stainlessstasis.config;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.RarityUtil;

import java.util.Set;

public record MainConfig (
    String configVersion,
    String[] comment,
    boolean debug,
    boolean multiplayerWarning,
    boolean alertAllShinies,
    boolean alertAllHA,
    boolean alertAllLegendaries,
    boolean alertAllMythicals,
    boolean alertAllUltraBeasts,
    boolean alertAllParadox,
    boolean alertAllStarter,
    Set<RarityUtil.Bucket> bucketsToAlert,
    boolean alertAllNotInDex,
    boolean alertAllUncaught,
    boolean alertEverything,
    IVHunting ivHunting,
    EVHunting evHunting,
    LevelFilter levelFilter
) {
    public static MainConfig createDefault() {
        return new MainConfig(CobblemonSpawnAlerts.MOD_VERSION,
                new String[]{
                        "This config is only on your client. It determines which *groups* of Pokemon are alerted. For individually alerting Pokemon, see pokemon.json.",
                        "For documentation on using the config, please see the Modrinth or GitHub for the mod.",
                        "https://modrinth.com/mod/cobblemon-spawn-alerts",
                        "https://github.com/StainlessStasis/CobblemonSpawnAlerts"
                },
                false, true, true, true, true, true,
                true, true,false, Set.of(RarityUtil.Bucket.ULTRA_RARE), false, false, false,
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
