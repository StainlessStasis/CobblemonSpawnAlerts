package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.mojang.datafixers.util.Pair;
import io.github.stainlessstasis.config.client.MessageTemplates;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.config.common.ServerConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.platform.Platform;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import org.joml.Vector3f;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class AlertUtils {
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean shouldGlobalAlert(PokemonEntity pokemonEntity, RarityUtil.Bucket bucket) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();

        boolean shouldAlertShiny = pokemon.getShiny() && config.alertShinies();
        boolean shouldAlertLegend = pokemon.isLegendary() && config.alertLegendaries();
        boolean shouldAlertMythical = pokemon.isMythical() && config.alertMythicals();
        boolean shouldAlertUltra = pokemon.isUltraBeast() && config.alertUltraBeasts();
        boolean shouldAlertParadox = pokemon.hasLabels(CobblemonPokemonLabels.PARADOX) && config.alertParadox();
        boolean shouldAlertStarter = RarityUtil.isStarter(pokemon.getSpecies().getNationalPokedexNumber()) && config.alertStarters();
        boolean shouldAlertHA = HiddenAbilityUtil.hasHiddenAbility(pokemon.getForm(), pokemon.getAbility().getName()) && config.alertHiddenAbility();
        boolean shouldAlertBucket = config.bucketsToAlert().contains(bucket);
        return shouldAlertShiny || shouldAlertLegend || shouldAlertMythical || shouldAlertUltra
                || shouldAlertParadox || shouldAlertStarter || shouldAlertHA || shouldAlertBucket;
    }

    public static Pair<Boolean, PokemonConfig.PokemonSpecificConfig> getConfigForPokemon(String pokemonName, int dexID) {
        Set<String> pokemonNames = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().keySet();
        String fixedPokemonName = PokemonNameUtil.fixName(pokemonName);

        for (String name : pokemonNames) {
            if (name.startsWith("default")) {
                continue;
            }

            String fixedName = name.toLowerCase().replaceAll("[ _-]", "");
            if (fixedName.contains(fixedPokemonName) || fixedName.contains(String.valueOf(dexID))) {
                if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(name)
                        instanceof PokemonConfig.PokemonSpecificConfig _config) {
                    return Pair.of(true, _config);
                }
            }
        }

        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            return Pair.of(false, _config);
        } else {
            CobblemonSpawnAlerts.LOGGER.warn("No default config found in `pokemon.json`, creating a new one.");
            return Pair.of(false, PokemonConfig.PokemonSpecificConfig.createDefault());
        }

    }

    public static String applyDynamicReplacements(String message, PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        MessageTemplates messageTemplates = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        Map<String, StatDisplayMode> displayModes = config.statDisplayModes();

        // Name
        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().translatedPokemonName());
        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        // Shiny
        if (config.alertShiny() && alertData.rarity().isShiny()) {
            message = message.replace("{shiny}", Component.translatable(messageTemplates.shiny()).getString());
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
            String main = (String) MessageTemplates.class.getMethod(tag).invoke(templates);
            String hover = tryGetMethod(templates, tag + "_hover");
            String unformatted = tryGetMethod(templates, tag + "_unformatted");

            return new StatTemplate(tag, main, hover, unformatted);
        } catch (Exception e) {
            CobblemonSpawnAlerts.LOGGER.error("Failed to find templates for tag: {}", tag, e);
            return new StatTemplate(tag, "U/D", "U/D", "U/D");
        }
    }
    
    private static String tryGetMethod(MessageTemplates templates, String name) {
        try { return (String) MessageTemplates.class.getMethod(name).invoke(templates); }
        catch (Exception ignored) {return "U/D";}
    }

    private record StatTemplate(String tag, String main, String hover, String unformatted) {}

    public static Component applyMessageInteractions(
            Component component,
            String hoverText,
            PokemonConfig.PokemonSpecificConfig config,
            AlertDataPacket alertData
    ) {
        String customTooltip = config.customAlertTooltip();
        String finalHoverText;
        if (customTooltip != null && !customTooltip.isEmpty()) {
            finalHoverText = applyDynamicReplacements(customTooltip, config, alertData, new StringBuilder(hoverText));
        } else {
            if (!hoverText.isEmpty() && !hoverText.endsWith("\n")) {
                hoverText += "\n";
            }
            finalHoverText = hoverText + "<color value=#55FF55>Click to toggle glow</color>";
        }

        ClickEvent clickEvent = AlertUtils.getDefaultGlowClickEvent(alertData);
        String customClickEvent = config.customAlertClickEvent();
        if (customClickEvent != null && !customClickEvent.isEmpty()) {
            String replacedClickEvent = applyDynamicReplacements(customClickEvent, config, alertData, new StringBuilder(hoverText));
            ClickEvent parsedClickEvent = AlertUtils.parseClickEvent(replacedClickEvent);
            if (parsedClickEvent != null) {
                clickEvent = parsedClickEvent;
            } else {
                CobblemonSpawnAlerts.LOGGER.warn("Invalid customAlertClickEvent '{}'. Falling back to glow toggle click.", replacedClickEvent);
            }
        }

        MutableComponent output = component.copy();
        if (!finalHoverText.isEmpty()) {
            Component hoverComponent = MessageUtils.parseMarkup(finalHoverText);
            output = output.withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent)));
        }

        ClickEvent finalClickEvent = clickEvent;
        return output.withStyle(style -> style.withClickEvent(finalClickEvent));
    }

    public static ClickEvent getDefaultGlowClickEvent(AlertDataPacket alertData) {
        String glowCommand = "/csa glow " + alertData.spawnData().pokemonUUID();
        if (Services.PLATFORM.getPlatform() == Platform.FABRIC) {
            return new ClickEvent(ClickEvent.Action.RUN_COMMAND, glowCommand);
        }

        // Neo specifically does not run this command from RUN_COMMAND reliably.
        return new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, glowCommand);
    }

    public static ClickEvent parseClickEvent(String eventDefinition) {
        String[] split = eventDefinition.split(":", 2);
        if (split.length != 2 || split[1].isEmpty()) {
            return null;
        }

        String action = split[0].trim().toLowerCase(Locale.ROOT);
        String value = split[1];
        ClickEvent.Action clickAction = switch (action) {
            case "open_url" -> ClickEvent.Action.OPEN_URL;
            case "open_file" -> ClickEvent.Action.OPEN_FILE;
            case "run_command" -> ClickEvent.Action.RUN_COMMAND;
            case "suggest_command" -> ClickEvent.Action.SUGGEST_COMMAND;
            case "change_page" -> ClickEvent.Action.CHANGE_PAGE;
            case "copy_to_clipboard" -> ClickEvent.Action.COPY_TO_CLIPBOARD;
            default -> null;
        };

        if (clickAction == null) {
            return null;
        }
        return new ClickEvent(clickAction, value);
    }

    public static String replaceIfNotAvailable(String string) {
        if (!Services.PLATFORM.doesServerHaveMod()) {
            return "N/A";
        }
        return string;
    }
}
