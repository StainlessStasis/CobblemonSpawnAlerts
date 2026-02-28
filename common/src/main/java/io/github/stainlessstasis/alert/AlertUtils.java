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

        int level = alertData.stats().level();
        IVs ivs = alertData.stats().ivs();
        EVs evYield = alertData.stats().evYield();
        Nature nature = Natures.getNature(alertData.traits().natureID());
        AbilityTemplate ability = Abilities.get(alertData.traits().abilityID());
        Gender gender = Gender.valueOf(alertData.traits().genderID());
        String nearestPlayer = alertData.spawnData().nearestPlayerName();
        RarityUtil.Bucket bucket = alertData.spawnData().bucket();

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().translatedPokemonName());

        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        Map<String, StatDisplayMode> displayModes = config.statDisplayModes();
        StatDisplayMode levelDisplayMode = displayModes.get("level");
        StatDisplayMode ivsDisplayMode = displayModes.get("ivs");
        StatDisplayMode evsDisplayMode = displayModes.get("evs");
        StatDisplayMode natureDisplayMode = displayModes.get("nature");
        StatDisplayMode abilityDisplayMode = displayModes.get("ability");
        StatDisplayMode genderDisplayMode = displayModes.get("gender");
        StatDisplayMode coordinatesDisplayMode = displayModes.get("coordinates");
        StatDisplayMode biomeDisplayMode = displayModes.get("biome");
        StatDisplayMode nearestPlayerDisplayMode = displayModes.get("nearestPlayer");

        // Shiny
        boolean shouldAlertShiny = config.alertShiny() && alertData.rarity().isShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", Component.translatable(messageTemplates.shiny()).getString());
            message = message.replace("{shiny_unformatted}", Component.translatable(messageTemplates.shiny_unformatted()).getString());
        }
        message = message.replace("{shiny}", "");
        message = message.replace("{shiny_unformatted}", "");

        // Bucket
        if (config.showBucket()) {
            String bucketMessage = switch (bucket) {
                case COMMON -> messageTemplates.common();
                case UNCOMMON -> messageTemplates.uncommon();
                case RARE -> messageTemplates.rare();
                case ULTRA_RARE -> messageTemplates.ultra_rare();
                case NONE -> messageTemplates.bucket_none();
            };

            bucketMessage = Component.translatable(bucketMessage).getString();
            message = message.replace("{bucket}", Component.translatable(messageTemplates.bucket(), bucketMessage).getString());
            message = message.replace("{bucket_unformatted}",
                    Component.translatable(messageTemplates.bucket_unformatted(), StringUtil.capitalizeEachWord(bucket.getSerializedName())).getString());
        }
        message = message.replace("{bucket}", "");
        message = message.replace("{bucket_unformatted}", "");

        // Legendary/Mythical/Ultra Beast/Paradox
        if (config.showLegendary()) {
            int dexId = alertData.spawnData().dexId();
            if (RarityUtil.isLegendary(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.legendary()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.legendary_unformatted()).getString());
            } else if (RarityUtil.isMythical(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.mythical()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.mythical_unformatted()).getString());
            } else if (RarityUtil.isUltraBeast(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.ultrabeast()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.ultrabeast_unformatted()).getString());
            } else if (RarityUtil.isParadox(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.paradox()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.paradox_unformatted()).getString());
            }
        }
        message = message.replace("{legendary}", "");
        message = message.replace("{legendary_unformatted}", "");

        // Level
        if (levelDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = levelDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.level_hover() : messageTemplates.level();
            String levelMessage = Component.translatable(configMessage, level).getString();

            if (isHoverEnabled) {
                hoverBuilder.append(levelMessage).append("\n");
            } else {
                message = message.replace("{level}", levelMessage);
            }
            message = message.replace("{level_unformatted}", Component.translatable(messageTemplates.level_unformatted(), level).getString());
        }
        message = message.replace("{level}", "");
        message = message.replace("{level_unformatted}", "");

        // IVs
        if (ivsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = ivsDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.ivs_hover() : messageTemplates.ivs();
            String ivsMessage = Services.PLATFORM.doesServerHaveMod() ?
                    Component.translatable(configMessage,
                            ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                            ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)).getString()
                    :
                    Component.translatable(configMessage,
                            "-", "-", "-", "-", "-", "-").getString();
            if (isHoverEnabled) {
                hoverBuilder.append(ivsMessage).append("\n");
            } else {
                message = message.replace("{ivs}", ivsMessage);
            }
            String ivsUnformatted = Services.PLATFORM.doesServerHaveMod() ?
                    Component.translatable(messageTemplates.ivs_unformatted(),
                            ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                            ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)).getString()
                    :
                    Component.translatable(messageTemplates.evs_unformatted(),
                            "-", "-", "-", "-", "-", "-").getString();
            message = message.replace("{ivs_unformatted}", ivsUnformatted);
        }
        message = message.replace("{ivs}", "");
        message = message.replace("{ivs_unformatted}", "");

        // EVs
        if (evsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = evsDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.evs_hover() : messageTemplates.evs();
            String evsMessage = Component.translatable(configMessage,
                    evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                    evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED)).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(evsMessage).append("\n");
            } else {
                message = message.replace("{evs}", evsMessage);
            }
            String evsUnformatted = Component.translatable(messageTemplates.evs_unformatted(),
                    evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                    evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED)).getString();
            message = message.replace("{evs_unformatted}", evsUnformatted);
        }
        message = message.replace("{evs}", "");
        message = message.replace("{evs_unformatted}", "");

        // Nature
        if (natureDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = natureDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.nature_hover() : messageTemplates.nature();
            String natureString = nature != null ? MiscUtilsKt.asTranslated(nature.getDisplayName()).getString() : "N/A";
            natureString = StringUtil.capitalize(natureString);
            natureString = AlertUtils.replaceIfNotAvailable(natureString);
            String natureMessage = Component.translatable(configMessage, natureString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(natureMessage).append("\n");
            } else {
                message = message.replace("{nature}", natureMessage);
            }
            String natureUnformatted = AlertUtils.replaceIfNotAvailable(Component.translatable(messageTemplates.nature_unformatted(), natureString).getString());
            message = message.replace("{nature_unformatted}", natureUnformatted);
        }
        message = message.replace("{nature}", "");
        message = message.replace("{nature_unformatted}", "");

        // Ability
        if (abilityDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = abilityDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.ability_hover() : messageTemplates.ability();
            String abilityString = ability != null ? StringUtil.capitalize(MiscUtilsKt.asTranslated(ability.getDisplayName()).getString()) : "N/A";
            abilityString = AlertUtils.replaceIfNotAvailable(abilityString);
            String abilityMessage = Component.translatable(configMessage, abilityString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(abilityMessage).append("\n");
            } else {
                message = message.replace("{ability}", abilityMessage);
            }
            String abilityUnformatted = AlertUtils.replaceIfNotAvailable(Component.translatable(messageTemplates.ability_unformatted(), abilityString).getString());
            message = message.replace("{ability_unformatted}", abilityUnformatted);
        }
        message = message.replace("{ability}", "");
        message = message.replace("{ability_unformatted}", "");

        // Hidden Ability
        boolean shouldAlertHA = config.alertHiddenAbility() &&
                HiddenAbilityUtil.hasHiddenAbility(alertData.spawnData().dexId(), alertData.traits().formID(), alertData.traits().abilityID());
        if (shouldAlertHA) {
            message = message.replace("{HA}", Component.translatable(messageTemplates.hidden_ability()).getString());
            message = message.replace("{HA_unformatted}", Component.translatable(messageTemplates.hidden_ability_unformatted()).getString());
        }
        message = message.replace("{HA}", "");
        message = message.replace("{HA_unformatted}", "");

        // Gender
        if (genderDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = genderDisplayMode == StatDisplayMode.HOVER;
            String genderSymbol = switch (gender) {
                case MALE -> messageTemplates.male();
                case FEMALE -> messageTemplates.female();
                case GENDERLESS -> messageTemplates.genderless();
            };
            String genderName = StringUtil.capitalize(gender.toString().toLowerCase());
            String genderString = Component.translatable(genderSymbol, genderName).getString();
            String configMessage = isHoverEnabled ? messageTemplates.gender_hover() : messageTemplates.gender();
            String genderMessage = Component.translatable(configMessage, genderString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(genderMessage).append("\n");
            } else {
                message = message.replace("{gender}", genderMessage);
                message = message.replace("{gender_unformatted}",
                        Component.translatable(messageTemplates.gender_unformatted(), genderName).getString());
            }
        }
        message = message.replace("{gender}", "");
        message = message.replace("{gender_unformatted}", "");

        // Coordinates
        Vector3f coords = alertData.spawnData().position();
        if (coordinatesDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = coordinatesDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.coords_hover() : messageTemplates.coords();

            String x = coords.x > Integer.MIN_VALUE ? String.valueOf((int)coords.x) : "N/A";
            String y = coords.y > Integer.MIN_VALUE ? String.valueOf((int)coords.y) : "N/A";
            String z = coords.z > Integer.MIN_VALUE ? String.valueOf((int)coords.z) : "N/A";

            String coordsMessage = Component.translatable(configMessage, x, y, z).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(coordsMessage).append("\n");
            } else {
                message = message.replace("{coords}", coordsMessage);
            }

            message = message.replace("{coords_unformatted}",Component.translatable(messageTemplates.coords_unformatted(), x, y, z).getString());
            message = message.replace("{x}", Component.translatable(messageTemplates.coords_x(), x).getString());
            message = message.replace("{y}", Component.translatable(messageTemplates.coords_y(), y).getString());
            message = message.replace("{z}", Component.translatable(messageTemplates.coords_z(), z).getString());
        }
        message = message.replace("{coords}", "");
        message = message.replace("{coords_unformatted}", "");
        message = message.replace("{x}", "");
        message = message.replace("{y}", "");
        message = message.replace("{z}", "");

        // Biome
        if (biomeDisplayMode != StatDisplayMode.DISABLED && Minecraft.getInstance().level != null) {
            boolean isHoverEnabled = biomeDisplayMode == StatDisplayMode.HOVER;
            String biomeName = Component.translatable(alertData.spawnData().biomeKey()).getString();
            biomeName = StringUtil.makeBeautiful(biomeName);

            String configMessage = isHoverEnabled ? messageTemplates.biome_hover() : messageTemplates.biome();
            String biomeMessage = Component.translatable(configMessage, biomeName).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(biomeMessage).append("\n");
            } else {
                message = message.replace("{biome}", biomeMessage);
            }
            message = message.replace("{biome_unformatted}", Component.translatable(messageTemplates.biome_unformatted(), biomeName).getString());
        }
        message = message.replace("{biome}", "");
        message = message.replace("{biome_unformatted}", "");

        // Nearest Player
        if (nearestPlayerDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = nearestPlayerDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.nearest_player_hover() : messageTemplates.nearest_player();
            String nearestPlayerMessage = Component.translatable(configMessage, nearestPlayer).getString();

            if (isHoverEnabled) {
                hoverBuilder.append(nearestPlayerMessage).append("\n");
            } else {
                message = message.replace("{nearest_player}", nearestPlayerMessage);
            }
            message = message.replace("{nearest_player_unformatted}", Component.translatable(messageTemplates.nearest_player_unformatted(), nearestPlayer).getString());
        }
        message = message.replace("{nearest_player}", "");
        message = message.replace("{nearest_player_unformatted}", "");

        return message;
    }

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
