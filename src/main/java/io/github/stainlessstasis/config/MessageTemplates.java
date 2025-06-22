package io.github.stainlessstasis.config;

import java.util.HashMap;

public class MessageTemplates extends HashMap<String, String> {
    public MessageTemplates(boolean isFresh) {
        if (isFresh) {
            put("fullSpawnMessage", "cobblemon-spawn-alerts.default_spawn_message");
            put("shiny", "cobblemon-spawn-alerts.shiny");
            put("level", "cobblemon-spawn-alerts.level");
            put("level_hover", "cobblemon-spawn-alerts.level_hover");
            put("ivs", "cobblemon-spawn-alerts.ivs");
            put("ivs_hover", "cobblemon-spawn-alerts.ivs_hover");
            put("nature", "cobblemon-spawn-alerts.nature");
            put("nature_hover", "cobblemon-spawn-alerts.nature_hover");
            put("coords", "cobblemon-spawn-alerts.coords");
            put("coords_hover", "cobblemon-spawn-alerts.coords_hover");
        }
    }

    public MessageTemplates() {}
}
