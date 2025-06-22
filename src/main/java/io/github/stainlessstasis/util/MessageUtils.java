package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.config.Config;
import io.github.stainlessstasis.config.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MessageUtils {
    public static void sendTranslated(String translationKey, Object... args) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }

        String translated = I18n.get(translationKey, args);
        Component component = ComponentUtil.convertFromAdventure(translated);
        player.sendSystemMessage(component);
    }

    public static String getTranslated(String translationKey, Object... args) {
        return I18n.get(translationKey, args);
    }

    public static String applyDynamicReplacements(String message, PokemonEntity pokemonEntity, Config.PokemonSpecificConfig config) {
        String name = pokemonEntity.getName().getString();
        Pokemon pokemon = pokemonEntity.getPokemon();
        int level = pokemon.getLevel();
        IVs ivs = pokemon.getIvs();
        Nature nature = pokemon.getNature();

        message = message.replace("{name}", name);

        boolean isHoverEnabled = config.showInfoAsHover;
        String hoverText = "";

        // Shiny
        boolean shouldAlertShiny = config.alertShiny && pokemonEntity.getPokemon().getShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", I18n.get(ConfigManager.getShinyMessage()));
        } else {
            message = message.replace("{shiny}", "");
        }

        // Level
        if (config.showLevel) {
            String configMessage = isHoverEnabled ? ConfigManager.getLevelMessageHover() : ConfigManager.getLevelMessage();
            String levelMessage = I18n.get(configMessage, level);
            if (isHoverEnabled) {
                hoverText += levelMessage + "\n";
            } else {
                message = message.replace("{level}", levelMessage);
            }
        }
        message = message.replace("{level}", "");

        // IVs
        if (config.showIVs) {
            String configMessage = isHoverEnabled ? ConfigManager.getIVsMessageHover() : ConfigManager.getIVsMessage();
            String ivsMessage =
                    I18n.get(configMessage,
                    ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                    ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED));
            if (isHoverEnabled) {
                hoverText += ivsMessage + "\n";
            } else {
                message = message.replace("{ivs}", ivsMessage);
            }
        }
        message = message.replace("{ivs}", "");

        // Nature
        if (config.showNature) {
            String configMessage = isHoverEnabled ? ConfigManager.getNatureMessageHover() : ConfigManager.getNatureMessage();
            String natureString = nature.getDisplayName().replace("cobblemon.nature.", "");
            natureString = natureString.substring(0, 1).toUpperCase() + natureString.substring(1);
            String natureMessage = I18n.get(configMessage, natureString);
            if (isHoverEnabled) {
                hoverText += natureMessage + "\n";
            } else {
                message = message.replace("{nature}", natureMessage);
            }
        }
        message = message.replace("{nature}", "");

        // Coordinates
        if (config.showCoordinates) {
            String configMessage = isHoverEnabled ? ConfigManager.getCoordsMessageHover() : ConfigManager.getCoordsMessage();
            String coordsMessage = I18n.get(configMessage,
                    (int)pokemonEntity.getX(), (int)pokemonEntity.getY(), (int)pokemonEntity.getZ());
            if (isHoverEnabled) {
                hoverText += coordsMessage + "\n";
            } else {
                message = message.replace("{coords}", coordsMessage);
            }
        }
        message = message.replace("{coords}", "");

        // Hover
        if (isHoverEnabled) {
            message = "<hover:show_text:\"" + hoverText+"\">" + message + "</hover>";
        }

        return message;
    }
}
