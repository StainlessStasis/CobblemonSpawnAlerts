package io.github.stainlessstasis.cobblemon_spawn_alerts.alert;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import io.github.stainlessstasis.cobblemon_spawn_alerts.config.client.PokemonConfig;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.AlertDataPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.PokemonRarityData;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Services;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.HiddenAbilityUtil;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.PokemonNameUtil;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.RarityUtil;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.StringUtil;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DynamicReplacements {


    public static String applyDynamicReplacements(String message, PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        return process(message, config, alertData, hoverBuilder);
    }

    private static String process(String message, @Nullable PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        final boolean isClient = config != null;

        // Name
        String pokemonName = getPokemonName(alertData.spawnData().pokemonName(), isClient);
        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        // x,y,z
        Vector3f spawnPos = alertData.spawnData().position();
        message = message.replace("{x}", getSideAwareString("coords_x", isClient, isCoordinateValid(spawnPos.x())))
        .replace("{y}", getSideAwareString("coords_y", isClient, isCoordinateValid(spawnPos.y())))
        .replace("{z}", getSideAwareString("coords_z", isClient, isCoordinateValid(spawnPos.z())));

        // Timestamp
        message = message.replace("{timestamp}", String.valueOf(System.currentTimeMillis()/1000));

        // match anything inside {curly braces}
        Pattern pattern = Pattern.compile("\\{([^}]+)}");
        Matcher matcher = pattern.matcher(message);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            String foundTag = matcher.group(1);
            Tag tag = Tag.fromString(foundTag);

            if (tag != null) {

                StatDisplayMode mode = StatDisplayMode.MAIN_MESSAGE;
                if (config != null) {
                    mode = config.statDisplayModes().getOrDefault(tag.getKey(), StatDisplayMode.MAIN_MESSAGE);
                }

                if (mode == StatDisplayMode.DISABLED) {
                    matcher.appendReplacement(sb, "");
                    continue;
                }

                StatTemplate template = createTemplate(tag, isClient, alertData, config);
                String replacement = getReplacementForTag(tag, foundTag, mode, template, hoverBuilder);
                matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
            }
        }
        matcher.appendTail(sb);
        message = sb.toString();

        return cleanupDynamicReplacements(message);
    }

    private static String getReplacementForTag(Tag tag, String fullTagContent, StatDisplayMode mode, StatTemplate template, StringBuilder hoverBuilder) {
        if (fullTagContent.endsWith("_unformatted")) {
            return template.unformatted();
        }

        if (hoverBuilder != null && (mode == StatDisplayMode.HOVER || mode == StatDisplayMode.BOTH)) {
            if (template.hover() != null) hoverBuilder.append(template.hover()).append("\n");
        }

        if (mode == StatDisplayMode.MAIN_MESSAGE || mode == StatDisplayMode.BOTH) {
            return template.main();
        }

        return "";
    }

    /**
     * Replaces anything within {curly braces} with an empty String.
     */
    public static String cleanupDynamicReplacements(String message) {
        return message.replaceAll("\\{[^}]*}", "");
    }

    /**
     * Creates a side-aware StatTemplate for the given tag.
     */
    private static StatTemplate createTemplate(Tag tag, boolean isClient, AlertDataPacket data, PokemonConfig.PokemonSpecificConfig config) {
        return switch (tag) {
            case LEVEL -> getSideAwareTemplate(tag, isClient, data.stats().level());
            case DEX -> getSideAwareTemplate(tag, isClient, data.spawnData().dexId());
            case NATURE -> {
                var nature = Natures.getNature(data.traits().natureID());
                String name = getNatureAbilityName(nature != null ? nature.getDisplayName() : "N/A", isClient);
                yield getSideAwareTemplate(tag, isClient, name);
            }
            case ABILITY -> {
                var ability = Abilities.get(data.traits().abilityID());
                String name = getNatureAbilityName(ability != null ? ability.getDisplayName() : "N/A", isClient);
                yield getSideAwareTemplate(tag, isClient, name);
            }
            case IVS -> getSideAwareTemplate(tag, isClient, getIVs(data.stats().ivs(), isClient));
            case EVS -> getSideAwareTemplate(tag, isClient, getEVs(data.stats().evYield()));
            case BUCKET -> getSideAwareTemplate(tag, isClient, getBucket(data.spawnData().bucket(), isClient, config));
            case LEGENDARY -> {
                String rarity = getLegendary(data.rarity(), data.spawnData().dexId(), isClient, config);
                yield getSideAwareTemplate(tag, rarity, isClient);
            }
            case SHINY -> {
                boolean shouldAlert = data.rarity().isShiny() && (!isClient || config.alertShiny());
                String methodLookup = shouldAlert ? tag.getKey() : "";
                yield getSideAwareTemplate(tag, methodLookup, isClient);
            }
            case HIDDEN_ABILITY -> {
                boolean shouldAlert =
                        HiddenAbilityUtil.hasHiddenAbility(data.spawnData().dexId(), data.traits().formID(), data.traits().abilityID())
                        &&
                        (!isClient || config.alertHiddenAbility());
                String methodLookup = shouldAlert ? tag.getKey() : "";
                yield getSideAwareTemplate(tag, methodLookup, isClient);
            }
            case COORDINATES -> {
                var pos = data.spawnData().position();
                yield getSideAwareTemplate(tag, isClient, isCoordinateValid(pos.x()), isCoordinateValid(pos.y()), isCoordinateValid(pos.z()));
            }
            case BIOME -> getSideAwareTemplate(tag, isClient, getBiome(data.spawnData().biomeKey(), isClient));
            case NEAREST_PLAYER -> getSideAwareTemplate(tag, isClient, data.spawnData().nearestPlayerName());
            case GENDER -> getSideAwareTemplate(tag, isClient, getGender(Gender.valueOf(data.traits().genderID()), isClient));
        };
    }


    /**
     * Gets a side-aware StatTemplate for the given tag and method lookup.
     * The tag's key is used here as the method lookup.
     */
    private static StatTemplate getSideAwareTemplate(Tag tag, boolean isClient, Object... args) {
        return getSideAwareTemplate(tag, tag.getKey(), isClient, args);
    }

    /**
     * Gets a side-aware StatTemplate for the given tag and method lookup.
     * @param methodLookup The name of the method to lookup in MessageTemplates/ServerMessageTemplates
     */
    private static StatTemplate getSideAwareTemplate(Tag tag, String methodLookup, boolean isClient, Object... args) {
        Object templatesConfig = getTemplatesConfig(isClient);
        StatTemplate template = getTemplateByTag(templatesConfig, methodLookup, isClient);

        String key = tag.getKey();
        if (isClient) {
            return new StatTemplate(
                    key,
                    Component.translatable(template.main(), args).getString(),
                    Component.translatable(template.hover(), args).getString(),
                    Component.translatable(template.unformatted(), args).getString()
            );
        }
        return new StatTemplate(
                key,
                tryFormat(template.main(), args),
                tryFormat(template.hover(), args),
                tryFormat(template.unformatted(), args)
        );
    }

    /**
     * Attempts to format a message with String#format, as a replacement for Component#translatable only being available on clients.
     */
    private static String tryFormat(String message, Object... args) {
        String result = "N/A";
        try {
            result = String.format(message, args);
        } catch (Exception ignored) {}

        return result;
    }

    /**
     * Attempts to invoke methods for MessageTemplates/ServerMessageTemplates to get their main message, hover, and unformatted fields.
     * The server only uses the main message since that is all that ServerMessageTemplates contains.
     */
    private static StatTemplate getTemplateByTag(Object templates, String tag, boolean isClient) {

        try {
            String main = tryGetMethod(templates, tag);
            if (isClient) {
                String hover = tryGetMethod(templates, tag + "_hover");
                String unformatted = tryGetMethod(templates, tag + "_unformatted");
                return new StatTemplate(tag, main, hover, unformatted);
            }

            // The server_message_templates.json config only has main messages, not unformatted or hover
            return new StatTemplate(tag, main, main, main);

        } catch (Exception e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to find templates for tag: {}", tag, e);
            return new StatTemplate(tag, "N/A", "N/A", "N/A");
        }
    }

    /**
     * Attempts to invoke a method for MessageTemplates/ServerMessageTemplates.
     */
    private static String tryGetMethod(Object templates, String name) {
        try { return (String) templates.getClass().getMethod(name).invoke(templates); }
        catch (Exception ignored) {return "";}
    }

    private record StatTemplate(String tag, String main, String hover, String unformatted) {}

    /**
     * Takes a tag and attempts to invoke its method via reflection.
     * Then the message is translated based on the client status.
     */
    private static String getSideAwareString(String tag, boolean isClient, Object... args) {
        Object templatesConfig = getTemplatesConfig(isClient);
        if (isClient) {
            String message = tryGetMethod(templatesConfig, tag);
            return Component.translatable(message, args).getString();
        }

        String message = tryGetMethod(templatesConfig, tag);
        message = tryFormat(message, args);
        return message;
    }

    /**
     * Gets either the MessageTemplates or ServerMessageTemplates config based on the client status
     */
    private static Object getTemplatesConfig(boolean isClient) {
        return isClient ? CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates()
                :
                CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerMessageTemplates();
    }

    private static String getGender(Gender gender, boolean isClient) {
        Object templates = getTemplatesConfig(isClient);
        String key = switch (gender) {
            case MALE -> tryGetMethod(templates, "male");
            case FEMALE -> tryGetMethod(templates, "female");
            default -> tryGetMethod(templates, "genderless");
        };

        if (isClient) {
            return Component.translatable(key, StringUtil.capitalize(gender.getSerializedName().toLowerCase())).getString();
        }

        return key;
    }

    private static String getBiome(String biomeKey, boolean isClient) {
        if (isClient) {
            return StringUtil.makeBeautiful(Component.translatable(biomeKey).getString());
        }

        List<String> split = StringUtil.splitTranslationKey(biomeKey);
        return StringUtil.makeBeautiful(split.getLast());
    }

    private static String getLegendary(PokemonRarityData data, int dexID, boolean isClient, PokemonConfig.PokemonSpecificConfig config) {
            if (isClient && !config.showLegendary()) return "";

            if (isClient) {
                return  RarityUtil.isLegendary(dexID) ? "legendary" :
                        RarityUtil.isMythical(dexID) ? "mythical" :
                        RarityUtil.isUltraBeast(dexID) ? "ultrabeast" :
                        RarityUtil.isParadox(dexID) ? "paradox" : "";
            }

            return  data.isLegendary() ? "legendary" :
                    data.isMythical() ? "mythical" :
                    data.isUltraBeast() ? "ultrabeast" :
                    data.isParadox() ? "paradox" : "";
    }

    private static String getBucket(RarityUtil.Bucket bucket, boolean isClient, PokemonConfig.PokemonSpecificConfig config) {
        if (isClient && !config.showBucket()) return "";

        Object templates = getTemplatesConfig(isClient);
        String bucketKey = switch (bucket) {
            case COMMON -> tryGetMethod(templates, "common");
            case UNCOMMON -> tryGetMethod(templates, "uncommon");
            case RARE -> tryGetMethod(templates, "rare");
            case ULTRA_RARE -> tryGetMethod(templates, "ultra_rare");
            case NONE -> tryGetMethod(templates, "none");
        };
        if (!isClient) {
            return bucketKey;
        }

        return Component.translatable(bucketKey).getString();
    }

    private static Object[] getEVs(EVs evs) {
        return new Object[]{evs.get(Stats.HP), evs.get(Stats.ATTACK), evs.get(Stats.DEFENCE),
                evs.get(Stats.SPECIAL_ATTACK), evs.get(Stats.SPECIAL_DEFENCE), evs.get(Stats.SPEED)};
    }

    private static Object[] getIVs(IVs ivs, boolean isClient) {
        return (!isClient || Services.PLATFORM.doesServerHaveMod()) ?
        new Object[]{ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)}
        : new Object[]{"?", "?", "?", "?", "?", "?"};
    }

    private static String getNatureAbilityName(String name, boolean isClient) {
        if (isClient) {
            return Component.translatable(name).getString();
        }

        List<String> list = StringUtil.splitTranslationKey(name);
        // nature/ability translation keys are cobblemon.nature.naturename, so we want to get the last index
        return StringUtil.makeBeautiful(list.getLast());
    }

    private static String getPokemonName(String name, boolean isClient) {
        if (isClient) {
            return PokemonNameUtil.getTranslatedName(name);
        }

        List<String> list = StringUtil.splitTranslationKey(name);
        // name translation keys are cobblemon.species.speciesname.name, so we want to get the 2nd to last index
        return StringUtil.makeBeautiful(list.get(list.size()-2));
    }

    /**
     * Checks whether a coordinate is valid.
     * @return If the coordinate is valid, an int is returned. Otherwise, a String of "N/A" is.
     */
    private static Object isCoordinateValid(float coord) {
        return (int) coord != Integer.MIN_VALUE ? (int)coord : "N/A";
    }
}
