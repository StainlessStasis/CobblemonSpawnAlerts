package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

import java.util.Map;

public class EvsUtil {
    public static EVs getEVsFromYield(Map<Stat, Integer> evYield) {
        EVs finalEvYield = CobblemonStatProvider.INSTANCE.createEmptyEVs();
        for (Stat stat : evYield.keySet()) {
            finalEvYield.add(stat, evYield.get(stat));
        }
        return finalEvYield;
    }

    public static void print(EVs evs) {
        evs.iterator().forEachRemaining(ev -> {
            CobblemonSpawnAlerts.LOGGER.info(ev.getKey().getDisplayName()+" | "+ev.getValue());
        });
    }
}
