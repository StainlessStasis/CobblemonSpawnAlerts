package io.github.stainlessstasis.core;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.config.CommonConfigManager;
import io.github.stainlessstasis.platform.Services;
import kotlin.Unit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonSpawnAlerts {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommonConfigManager COMMON_CONFIG_MANAGER = new CommonConfigManager();
    public static final ClientConfigManager CLIENT_CONFIG_MANAGER = new ClientConfigManager();

    public static void initCommon() {
        LOGGER.info("CobblemonSpawnAlerts server initializing...");
        COMMON_CONFIG_MANAGER.loadConfig();

        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, evt -> {
            Services.PLATFORM.onPokemonSpawned(evt.getEntity());
            return Unit.INSTANCE;
        });
    }

    public static void initClient() {
        LOGGER.info("CobblemonSpawnAlerts client initializing...");
        CLIENT_CONFIG_MANAGER.loadConfig();
    }
}
