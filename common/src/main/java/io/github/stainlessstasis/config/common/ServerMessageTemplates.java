package io.github.stainlessstasis.config.common;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

public record ServerMessageTemplates (
        String configVersion,
        String[] comment,
        String shiny,
        String level,
        String ivs,
        String evs,
        String nature,
        String ability,
        String gender,
        String male,
        String female,
        String genderless,
        String coords,
        String coords_x,
        String coords_y,
        String coords_z,
        String biome,
        String nearest_player,
        String legendary,
        String mythical,
        String ultrabeast,
        String paradox,
        String hidden_ability,
        String bucket,
        String common,
        String uncommon,
        String rare,
        String ultra_rare,
        String bucket_none
) {
    public static ServerMessageTemplates createDefault() {
        return new ServerMessageTemplates(
                CobblemonSpawnAlerts.MOD_VERSION,
                new String[]{
                        "This config is common between server and client. It is used for serverside dynamic replacements.",
                        "For details on using the config, please see the docs.",
                        "https://stainlessstasis.github.io/CSA-Docs/config/"
                },
                "Shiny",
                "%s",
                "(%s, %s, %s, %s, %s, %s)",
                "(%s, %s, %s, %s, %s, %s)",
                "%s",
                "%s",
                "%s",
                "♂ Male",
                "♀ Female",
                "Genderless",
                "(%s, %s, %s)",
                "%s",
                "%s",
                "%s",
                "%s",
                "%s",
                "Legendary",
                "Mythical",
                "Ultra Beast",
                "Paradox",
                "Hidden Ability",
                "%s",
                "Common",
                "Uncommon",
                "Rare",
                "Ultra Rare",
                ""
        );
    }
}
