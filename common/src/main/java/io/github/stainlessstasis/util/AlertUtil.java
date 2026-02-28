package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.config.common.ServerConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.platform.Platform;
import io.github.stainlessstasis.platform.Services;
import net.minecraft.network.chat.ClickEvent;

import java.util.Locale;

public class AlertUtil {
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
