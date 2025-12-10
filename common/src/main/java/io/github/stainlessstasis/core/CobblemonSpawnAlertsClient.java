package io.github.stainlessstasis.core;

import io.github.stainlessstasis.config.ClientConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CobblemonSpawnAlertsClient {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ClientConfigManager CLIENT_CONFIG_MANAGER = new ClientConfigManager();
    public static Set<UUID> glowing = new HashSet<>();
    public static Map<UUID, String> waypoints = new HashMap<>(); // Maps entity uuids to waypoint guids

    public static void initClient() {
        LOGGER.info("CobblemonSpawnAlerts client initializing...");
        CLIENT_CONFIG_MANAGER.loadConfig();
    }
}
