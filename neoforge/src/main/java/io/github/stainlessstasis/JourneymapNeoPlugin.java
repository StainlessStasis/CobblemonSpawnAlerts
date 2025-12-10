package io.github.stainlessstasis;

import io.github.stainlessstasis.compat.JourneymapCompat;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class JourneymapNeoPlugin implements IClientPlugin {
    @Override
    public void initialize(IClientAPI api) {
        JourneymapCompat.initAPI(api);
    }

    @Override
    public String getModId() {
        return CobblemonSpawnAlerts.MOD_ID;
    }
}
