package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import io.github.stainlessstasis.config.client.MessageTemplates;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.config.common.ServerMessageTemplates;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.HiddenAbilityUtil;
import io.github.stainlessstasis.util.PokemonNameUtil;
import io.github.stainlessstasis.util.RarityUtil;
import io.github.stainlessstasis.util.StringUtil;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Map;

public class DynamicReplacements {
    private static final String[] TAGS = {"level", "bucket", "legendary", "ivs", "evs", "nature", "ability", "gender", "coordinates", "biome", "nearestPlayer"};

    public static String applyDynamicReplacements(String message, PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        return process(message, config, alertData, hoverBuilder);
    }

    private static String process(String message, @Nullable PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        final boolean isClient = config != null;
        MessageTemplates messageTemplates = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        Map<String, StatDisplayMode> displayModes = config.statDisplayModes();

        // Name
        String pokemonName = getPokemonName(alertData.spawnData().pokemonName(), isClient);
        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        // Shiny
        if (alertData.rarity().isShiny() && (isClient && config.alertShiny())) {
            message = message.replace("{shiny}", getSideAwareString("shiny", isClient));
        }

        // HA
        if (config.alertHiddenAbility() && HiddenAbilityUtil.hasHiddenAbility(alertData.spawnData().dexId(), alertData.traits().formID(), alertData.traits().abilityID())) {
            message = message.replace("{HA}", Component.translatable(messageTemplates.hidden_ability()).getString());
        }

        // Level
        int level = alertData.stats().level();
        message = processStat(message, hoverBuilder, displayModes.get("level"), getTemplateByTag(messageTemplates, "level", level));

        // Bucket
        RarityUtil.Bucket bucket = alertData.spawnData().bucket();
        if (config.showBucket()) {
            StatTemplate raw = getTemplateByTag(messageTemplates, "bucket");

            String bucketKey = switch (bucket) {
                case COMMON -> messageTemplates.common();
                case UNCOMMON -> messageTemplates.uncommon();
                case RARE -> messageTemplates.rare();
                case ULTRA_RARE -> messageTemplates.ultra_rare();
                case NONE -> messageTemplates.bucket_none();
            };

            String translatedBucket = Component.translatable(bucketKey).getString();
            String serializedName = StringUtil.capitalizeEachWord(bucket.getSerializedName());

            StatTemplate finalBucket = new StatTemplate(
                    "bucket",
                    Component.translatable(raw.main(), translatedBucket).getString(),
                    raw.hover() != null ? Component.translatable(raw.hover(), translatedBucket).getString() : null,
                    Component.translatable(raw.unformatted(), serializedName).getString()
            );

            message = processStat(message, hoverBuilder, StatDisplayMode.MAIN_MESSAGE, finalBucket);
        }

        // Legendary/Rarity
        if (config.showLegendary()) {
            int dexId = alertData.spawnData().dexId();
            String label =
                    RarityUtil.isLegendary(dexId) ? "legendary" :
                            RarityUtil.isMythical(dexId) ? "mythical" :
                                    RarityUtil.isUltraBeast(dexId) ? "ultrabeast" :
                                            RarityUtil.isParadox(dexId) ? "paradox" : null;

            if (label != null) {
                StatTemplate StatTemplate = getTemplateByTag(messageTemplates, label);
                StatTemplate finalLabel = new StatTemplate("legendary",
                        Component.translatable(StatTemplate.main()).getString(), null,
                        Component.translatable(StatTemplate.unformatted()).getString());
                message = processStat(message, hoverBuilder, StatDisplayMode.MAIN_MESSAGE, finalLabel);
            }
        }

        // IVs
        Object[] ivArgs = Services.PLATFORM.doesServerHaveMod() ?
                new Object[]{alertData.stats().ivs().get(Stats.HP), alertData.stats().ivs().get(Stats.ATTACK), alertData.stats().ivs().get(Stats.DEFENCE),
                        alertData.stats().ivs().get(Stats.SPECIAL_ATTACK), alertData.stats().ivs().get(Stats.SPECIAL_DEFENCE), alertData.stats().ivs().get(Stats.SPEED)}
                : new Object[]{"-", "-", "-", "-", "-", "-"};
        message = processStat(message, hoverBuilder, displayModes.get("ivs"), getTemplateByTag(messageTemplates, "ivs", ivArgs));

        // EVs
        Object[] evArgs = new Object[]{alertData.stats().evYield().get(Stats.HP), alertData.stats().evYield().get(Stats.ATTACK), alertData.stats().evYield().get(Stats.DEFENCE),
                alertData.stats().evYield().get(Stats.SPECIAL_ATTACK), alertData.stats().evYield().get(Stats.SPECIAL_DEFENCE), alertData.stats().evYield().get(Stats.SPEED)};
        message = processStat(message, hoverBuilder, displayModes.get("evs"), getTemplateByTag(messageTemplates, "evs", evArgs));

        // Nature
        Nature nature = Natures.getNature(alertData.traits().natureID());
        String natureName = AlertUtils.replaceIfNotAvailable(nature != null ? StringUtil.capitalize(MiscUtilsKt.asTranslated(nature.getDisplayName()).getString()) : "N/A");
        message = processStat(message, hoverBuilder, displayModes.get("nature"), getTemplateByTag(messageTemplates, "nature", natureName));

        // Ability
        AbilityTemplate ability = Abilities.get(alertData.traits().abilityID());
        String abilityName = AlertUtils.replaceIfNotAvailable(ability != null ? StringUtil.capitalize(MiscUtilsKt.asTranslated(ability.getDisplayName()).getString()) : "N/A");
        message = processStat(message, hoverBuilder, displayModes.get("ability"), getTemplateByTag(messageTemplates, "ability", abilityName));

        // Gender
        Gender gender = Gender.valueOf(alertData.traits().genderID());
        StatTemplate template = getTemplateByTag(messageTemplates, "gender");
        String symbolKey = switch (gender) {
            case MALE -> messageTemplates.male();
            case FEMALE -> messageTemplates.female();
            case GENDERLESS -> messageTemplates.genderless();
        };
        String genderName = StringUtil.capitalize(gender.toString().toLowerCase());
        String genderSymbol = Component.translatable(symbolKey, genderName).getString();
        StatTemplate finalGender = new StatTemplate("gender",
                Component.translatable(template.main(), genderSymbol).getString(),
                Component.translatable(template.hover(), genderSymbol).getString(),
                Component.translatable(template.unformatted(), genderName).getString()
        );
        message = processStat(message, hoverBuilder, displayModes.get("gender"), finalGender);

        // Coordinates
        Vector3f coords = alertData.spawnData().position();
        String x = coords.x > Integer.MIN_VALUE ? String.valueOf((int)coords.x) : "N/A";
        String y = coords.y > Integer.MIN_VALUE ? String.valueOf((int)coords.y) : "N/A";
        String z = coords.z > Integer.MIN_VALUE ? String.valueOf((int)coords.z) : "N/A";
        message = processStat(message, hoverBuilder, displayModes.get("coordinates"), getTemplateByTag(messageTemplates, "coords", x, y, z));

        message = message.replace("{x}", Component.translatable(messageTemplates.coords_x(), x).getString())
                .replace("{y}", Component.translatable(messageTemplates.coords_y(), y).getString())
                .replace("{z}", Component.translatable(messageTemplates.coords_z(), z).getString());

        // Biome
        String biomeName = StringUtil.makeBeautiful(Component.translatable(alertData.spawnData().biomeKey()).getString());
        message = processStat(message, hoverBuilder, displayModes.get("biome"), getTemplateByTag(messageTemplates, "biome", biomeName));

        // Nearest Player
        message = processStat(message, hoverBuilder, displayModes.get("nearestPlayer"), getTemplateByTag(messageTemplates, "nearest_player", alertData.spawnData().nearestPlayerName()));

        return cleanupDynamicReplacements(message);
    }

    private static String processStat(String message, StringBuilder hover, StatDisplayMode mode, StatTemplate template) {
        if (mode == StatDisplayMode.DISABLED) return message;

        if (mode == StatDisplayMode.HOVER || mode == StatDisplayMode.BOTH) {
            hover.append(template.hover()).append("\n");
        }

        if (mode == StatDisplayMode.MAIN_MESSAGE || mode == StatDisplayMode.BOTH) {
            message = message.replace("{" + template.tag() + "}", template.main());
        }

        return message.replace("{" + template.tag() + "_unformatted}", template.unformatted());
    }

    public static String cleanupDynamicReplacements(String message) {
        return message.replaceAll("\\{[^}]*}", "");
    }

    private static StatTemplate getTemplateByTag(MessageTemplates templates, String tag, Object... args) {
        StatTemplate raw = getTemplateByTag(templates, tag);

        return new StatTemplate(
                tag,
                Component.translatable(raw.main(), args).getString(),
                Component.translatable(raw.hover(), args).getString(),
                Component.translatable(raw.unformatted(), args).getString()
        );
    }

    private static StatTemplate getTemplateByTag(MessageTemplates templates, String tag) {
        try {
            String main = tryGetMethod(templates, tag);
            String hover = tryGetMethod(templates, tag + "_hover");
            String unformatted = tryGetMethod(templates, tag + "_unformatted");

            return new StatTemplate(tag, main, hover, unformatted);
        } catch (Exception e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to find templates for tag: {}", tag, e);
            return new StatTemplate(tag, "U/D", "U/D", "U/D");
        }
    }

    private static String tryGetMethod(Object templates, String name) {
        try { return (String) templates.getClass().getMethod(name).invoke(templates); }
        catch (Exception ignored) {return "U/D";}
    }

    private record StatTemplate(String tag, String main, String hover, String unformatted) {}

    private static String getSideAwareString(String tag, boolean isClient, Object... args) {
        if (isClient) {
            String message = tryGetMethod(CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates(), tag);
            return Component.translatable(message, args).getString();
        }
        return tryGetMethod(CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerMessageTemplates(), tag);
    }

    private static String getPokemonName(String name, boolean isClient) {
        if (isClient) {
            return PokemonNameUtil.getTranslatedName(name);
        }
        return StringUtil.capitalize(name);
    }
}
