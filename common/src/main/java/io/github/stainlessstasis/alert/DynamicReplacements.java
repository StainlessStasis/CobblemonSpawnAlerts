package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.network.PokemonRarityData;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.HiddenAbilityUtil;
import io.github.stainlessstasis.util.PokemonNameUtil;
import io.github.stainlessstasis.util.RarityUtil;
import io.github.stainlessstasis.util.StringUtil;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class DynamicReplacements {
    private static final String[] TAGS = {"dex", "level", "bucket", "legendary", "ivs", "evs", "nature", "ability", "gender", "coordinates", "biome", "nearestPlayer"};

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

        // Shiny
        if (alertData.rarity().isShiny() && (isClient && config.alertShiny())) {
            message = message.replace("{shiny}", getSideAwareString("shiny", isClient));
        }

        // HA
        if (HiddenAbilityUtil.hasHiddenAbility(alertData.spawnData().dexId(), alertData.traits().formID(), alertData.traits().abilityID())
            && (isClient && config.alertHiddenAbility())) {
            message = message.replace("{HA}", getSideAwareString("hidden_ability", isClient));
        }

        for (String tag : TAGS) {
            StatDisplayMode mode = (config != null) ?
                    config.statDisplayModes().getOrDefault(tag, StatDisplayMode.MAIN_MESSAGE) :
                    StatDisplayMode.MAIN_MESSAGE;

            if (mode == StatDisplayMode.DISABLED) continue;

            StatTemplate template = createTemplate(tag, isClient, alertData, config);
            message = processStat(message, hoverBuilder, mode, template);
        }

        return cleanupDynamicReplacements(message);
    }

    private static String processStat(String message, StringBuilder hoverBuilder, StatDisplayMode mode, StatTemplate template) {
        if (hoverBuilder != null && (mode == StatDisplayMode.HOVER || mode == StatDisplayMode.BOTH)) {
            if (template.hover() != null) hoverBuilder.append(template.hover()).append("\n");
        }

        if (mode == StatDisplayMode.MAIN_MESSAGE || mode == StatDisplayMode.BOTH) {
            message = message.replace("{" + template.tag() + "}", template.main());
        }

        message = message.replace("{" + template.tag() + "_unformatted}", template.unformatted());
        return message;
    }


    public static String cleanupDynamicReplacements(String message) {
        return message.replaceAll("\\{[^}]*}", "");
    }

    private static StatTemplate createTemplate(String tag, boolean isClient, AlertDataPacket data, PokemonConfig.PokemonSpecificConfig config) {
        return switch (tag) {
            case "level" -> getSideAwareTemplate("level", isClient, data.stats().level());
            case "dex" -> getSideAwareTemplate("dex", isClient, data.spawnData().dexId());
            case "nature" -> {
                var nature = Natures.getNature(data.traits().natureID());
                String name = (nature != null) ? nature.getDisplayName() : "N/A";
                name = getNatureAbilityName(name, isClient);
                yield getSideAwareTemplate("nature", isClient, name);
            }
            case "ability" -> {
                var ability = Abilities.get(data.traits().abilityID());
                String name = (ability != null) ? ability.getDisplayName() : "N/A";
                name = getNatureAbilityName(name, isClient);
                yield getSideAwareTemplate("ability", isClient, name);
            }
            case "ivs" -> {
                Object[] ivs = getIVs(data.stats().ivs(), isClient);
                yield getSideAwareTemplate("ivs", isClient, ivs);
            }
            case "evs" -> {
                Object[] evs = getEVs(data.stats().evYield());
                yield getSideAwareTemplate("evs", isClient, evs);
            }
            case "bucket" -> {
                String bucket = getBucket(data.spawnData().bucket(), isClient, config);
                yield getSideAwareTemplate("bucket", isClient, bucket);
            }
            case "legendary" -> {
                String rarity = getLegendary(data.rarity(), data.spawnData().dexId(), isClient, config);
                yield getSideAwareTemplate("legendary", rarity, isClient);
            }
            case "coordinates" -> {
                var pos = data.spawnData().position();
                yield getSideAwareTemplate("coords", isClient, isCoordinateValid(pos.x()), isCoordinateValid(pos.y()), isCoordinateValid(pos.z()));
            }
            case "biome" -> {
                String biome = getBiome(data.spawnData().biomeKey(), isClient);
                yield getSideAwareTemplate("biome", isClient, biome);
            }
            case "nearest_player" -> getSideAwareTemplate("nearest_player", isClient, data.spawnData().nearestPlayerName());
            case "gender" -> {
                Gender gender = Gender.valueOf(data.traits().genderID());
                String genderString = getGender(gender, isClient);
                yield getSideAwareTemplate("gender", isClient, genderString);
            }
            default -> getSideAwareTemplate(tag, isClient);
        };
    }

    private static StatTemplate getSideAwareTemplate(String tag, boolean isClient, Object... args) {
        return getSideAwareTemplate(tag, tag, isClient, args);
    }

    private static StatTemplate getSideAwareTemplate(String originalTag, String secondaryTag, boolean isClient, Object... args) {
        Object templatesConfig = getTemplatesConfig(isClient);
        StatTemplate template = getTemplateByTag(templatesConfig, secondaryTag, isClient);
        if (isClient) {
            return new StatTemplate(
                    originalTag,
                    Component.translatable(template.main(), args).getString(),
                    Component.translatable(template.hover(), args).getString(),
                    Component.translatable(template.unformatted(), args).getString()
            );
        }
        return new StatTemplate(
                originalTag,
                tryFormat(template.main(), args),
                tryFormat(template.hover(), args),
                tryFormat(template.unformatted(), args)
        );
    }

    private static String tryFormat(String message, Object... args) {
        String result = "N/A";
        try {
            result = String.format(message, args);
        } catch (Exception ignored) {}

        return result;
    }

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

    private static String tryGetMethod(Object templates, String name) {
        try { return (String) templates.getClass().getMethod(name).invoke(templates); }
        catch (Exception ignored) {return "";}
    }

    private record StatTemplate(String tag, String main, String hover, String unformatted) {}

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

    private static Object isCoordinateValid(float coord) {
        return (int) coord != Integer.MIN_VALUE ? (int)coord : "N/A";
    }
}
