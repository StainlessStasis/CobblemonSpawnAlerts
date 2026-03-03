package io.github.stainlessstasis.core;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.compat.JourneymapCompat;
import io.github.stainlessstasis.config.client.MainConfig;
import io.github.stainlessstasis.config.manager.ClientConfigManager;
import io.github.stainlessstasis.config.manager.VersionMatcher;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.MessageUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CobblemonSpawnAlertsClient {
    public static final String MOD_ID = "cobblemon_spawn_alerts";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ClientConfigManager CLIENT_CONFIG_MANAGER = new ClientConfigManager();
    public static HashMap<UUID, Integer> glowing = new HashMap<>();
    public static Map<UUID, String> waypoints = new HashMap<>(); // Maps entity uuids to waypoint guids

    public static void initClient() {
        LOGGER.info("CobblemonSpawnAlerts client initializing...");
        CLIENT_CONFIG_MANAGER.loadConfig();
    }

    public static void onUnload(Entity entity, Level level) {
        if (Services.PLATFORM.doesServerHaveMod()) return;
        if (!(entity instanceof PokemonEntity pokemonEntity)) return;

        UUID uuid = pokemonEntity.getPokemon().getUuid();
        glowing.remove(uuid);
        if (Services.PLATFORM.isModLoaded("journeymap")) {
            JourneymapCompat.removeWaypoint(uuid);
        }
    }

    public static ClickEvent getChangelogClickEvent() {
        return new ClickEvent(
                ClickEvent.Action.OPEN_URL,
                "https://stainlessstasis.github.io/CSA-Docs/other/changelog"
        );
    }

    public static void sendMultiplayerWarning() {
        final MainConfig mainConfig = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
        if (mainConfig.multiplayerWarning() && !Minecraft.getInstance().isSingleplayer()) {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.multiplayer_warning");
        }

        if (mainConfig.versionChangeWarning() && VersionMatcher.hasVersionChanged()) {
            MessageUtils.sendTranslated("cobblemon-spawn-alerts.version_change_detected", getChangelogClickEvent());

            List<String> changelog = VersionMatcher.getMajorChanges();
            for (String message : changelog) {
                MessageUtils.sendTranslated(message, getChangelogClickEvent());
            }
        }
    }
}
