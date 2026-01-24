package io.github.stainlessstasis.compat;

import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.util.DimensionUtil;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.awt.*;
import java.util.UUID;

public class JourneymapCompat {
    private static IClientAPI api;

    public static IClientAPI getAPI() {
        return api;
    }

    public static void initAPI(IClientAPI api) {
        JourneymapCompat.api = api;
    }

    public static void createWaypoint(BlockPos pos, AlertDataPacket alertData, PokemonConfig.JourneymapConfig jmConfig) {
        ResourceKey<Level> dimension = DimensionUtil.getDimension(alertData.spawnData().dimensionKey());
        String waypointName = jmConfig.waypointName().isEmpty() ? alertData.spawnData().translatedPokemonName() : jmConfig.waypointName();

        Waypoint waypoint = WaypointFactory.createClientWaypoint(CobblemonSpawnAlerts.MOD_ID, pos, waypointName, dimension, jmConfig.persistent());

        if (!jmConfig.waypointHexColor().isEmpty()) {
            Color color = Color.WHITE;
            try {
                color = Color.decode(jmConfig.waypointHexColor());
            } catch (NumberFormatException e) {
                CobblemonSpawnAlerts.LOGGER.error("Cannot create color for waypoint. Check that your hex color is properly formatted (must begin with `#`).");
                e.printStackTrace();
            }
            waypoint.setColor(color.getRGB());
        }

        CobblemonSpawnAlertsClient.waypoints.put(alertData.spawnData().pokemonUUID(), waypoint.getGuid());

        api.addWaypoint(CobblemonSpawnAlerts.MOD_ID, waypoint);
    }

    public static void removeWaypoint(UUID uuid) {
        Waypoint waypoint = api.getWaypoint(CobblemonSpawnAlerts.MOD_ID, CobblemonSpawnAlertsClient.waypoints.get(uuid));
        if (waypoint == null) {
            CobblemonSpawnAlertsClient.waypoints.remove(uuid);
            return;
        }
        api.removeWaypoint(CobblemonSpawnAlerts.MOD_ID, waypoint);
        CobblemonSpawnAlertsClient.waypoints.remove(uuid);
    }
}
