package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;

public class GlowUtil {
    public static int getGlowColor(String hex) {
        int color = 0xffffffff;
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            color = (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            CobblemonSpawnAlerts.LOGGER.error("Cannot parse glow color from string `"+hex+"`: NumberFormatException");
        }
        return color;
    }
}
