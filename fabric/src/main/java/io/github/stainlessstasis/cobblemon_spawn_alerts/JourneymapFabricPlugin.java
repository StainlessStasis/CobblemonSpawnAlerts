package io.github.stainlessstasis.cobblemon_spawn_alerts;

import io.github.stainlessstasis.cobblemon_spawn_alerts.compat.JourneymapCompat;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneymapFabricPlugin implements IClientPlugin {
    @Override
    public void initialize(IClientAPI api) {
        JourneymapCompat.initAPI(api);
    }

    @Override
    public String getModId() {
        return CobblemonSpawnAlerts.MOD_ID;
    }
}
