package io.github.stainlessstasis;

import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.config.CommonConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonSpawnAlerts {
    public static final String MOD_ID = "cobblemon-spawn-alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final CommonConfigManager COMMON_CONFIG_MANAGER = new CommonConfigManager();
    public static final ClientConfigManager CLIENT_CONFIG_MANAGER = new ClientConfigManager();
}
