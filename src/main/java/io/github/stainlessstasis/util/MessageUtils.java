package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.config.Config;
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

        message = message.replace("{name}", name);

        boolean shouldAlertShiny = config.alertShiny && pokemonEntity.getPokemon().getShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", I18n.get(CobblemonSpawnAlertsClient.MOD_ID +".shiny"));
        } else {
            message = message.replace("{shiny}", "");
        }

        if (config.showLevel) {
            message = message.replace("{level}", I18n.get(CobblemonSpawnAlertsClient.MOD_ID +".level", level));
        } else {
            message = message.replace("{level}", "");
        }

        return message;
    }
}
