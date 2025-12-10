package io.github.stainlessstasis.compat;

import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.util.DimensionUtil;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.awt.*;

public class JourneymapCompat {
    private static IClientAPI api;

    public static IClientAPI getAPI() {
        return api;
    }

    public static void initAPI(IClientAPI api) {
        JourneymapCompat.api = api;
    }

    public static void createWaypoint(BlockPos pos, String name, String dimensionKey, PokemonConfig.JourneymapConfig jmConfig) {
        ResourceKey<Level> dimension = DimensionUtil.getDimension(dimensionKey);
        String waypointName = jmConfig.waypointName().isEmpty() ? name : jmConfig.waypointName();

        Waypoint waypoint = WaypointFactory.createClientWaypoint(CobblemonSpawnAlerts.MOD_ID, pos, waypointName, dimension, jmConfig.persistent());

        if (!jmConfig.waypointHexColor().isEmpty()) {
            Color color = Color.decode(jmConfig.waypointHexColor());
            waypoint.setColor(color.getRGB());
        }

        api.addWaypoint(CobblemonSpawnAlerts.MOD_ID, waypoint);
    }
}
