package io.github.stainlessstasis.alert;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The key should match the value in the message templates configs and statDisplayModes
 * Aliases are only used for dynamic replacements
 */
public enum Tag {
    DEX("dex"),
    LEVEL("level"),
    BUCKET("bucket", "rarity"),
    SHINY("shiny"),
    HIDDEN_ABILITY("hidden_ability", "HA"),
    LEGENDARY("legendary", "mythical", "ultrabeast", "paradox"),
    IVS("ivs"),
    EVS("evs"),
    NATURE("nature"),
    ABILITY("ability"),
    GENDER("gender"),
    COORDINATES("coords", "coordinates"),
    BIOME("biome"),
    NEAREST_PLAYER("nearest_player", "player");

    private final String key;
    private final List<String> aliases;

    Tag(String key, String... aliases) {
        this.key = key;
        this.aliases = List.of(aliases);
    }
    public String getKey() { return key; }

    public static @Nullable Tag fromString(String input) {
        String cleanTag = input.replace("_unformatted", "").replace("_hover", "");

        for (Tag type : values()) {
            if (type.key.equals(cleanTag) || type.aliases.contains(cleanTag)) {
                return type;
            }
        }
        return null;
    }
}
