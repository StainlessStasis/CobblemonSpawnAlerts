package io.github.stainlessstasis.cobblemon_spawn_alerts.alert;

import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.mojang.datafixers.util.Pair;
import io.github.stainlessstasis.cobblemon_spawn_alerts.config.client.PokemonConfig;
import io.github.stainlessstasis.cobblemon_spawn_alerts.config.common.ServerConfig;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.AlertDataPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Platform;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Services;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.HiddenAbilityUtil;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.PokemonNameUtil;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.RarityUtil;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

import java.util.Locale;
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

    public static Component applyMessageInteractions(
            Component component,
            String hoverText,
            PokemonConfig.PokemonSpecificConfig config,
            AlertDataPacket alertData
    ) {
        String customTooltip = config.customAlertTooltip();
        String finalHoverText;
        if (customTooltip != null && !customTooltip.isEmpty()) {
            finalHoverText = DynamicReplacements.applyDynamicReplacements(customTooltip, config, alertData, new StringBuilder(hoverText));
        } else {
            if (!hoverText.isEmpty() && !hoverText.endsWith("\n")) {
                hoverText += "\n";
            }
            finalHoverText = hoverText + "<color value=#55FF55>Click to toggle glow</color>";
        }

        ClickEvent clickEvent = AlertUtils.getDefaultGlowClickEvent(alertData);
        String customClickEvent = config.customAlertClickEvent();
        if (customClickEvent != null && !customClickEvent.isEmpty()) {
            String replacedClickEvent = DynamicReplacements.applyDynamicReplacements(customClickEvent, config, alertData, new StringBuilder(hoverText));
            ClickEvent parsedClickEvent = AlertUtils.parseClickEvent(replacedClickEvent);
            if (parsedClickEvent != null) {
                clickEvent = parsedClickEvent;
            } else {
                CobblemonSpawnAlerts.LOGGER.warn("Invalid customAlertClickEvent '{}'. Falling back to glow toggle click.", replacedClickEvent);
            }
        }

        MutableComponent output = component.copy();
        if (!finalHoverText.isEmpty()) {
            Component hoverComponent = Services.PLATFORM.parseMarkup(finalHoverText);
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
