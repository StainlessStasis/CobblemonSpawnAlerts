package io.github.stainlessstasis.util;

import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
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

    public static String applyDynamicReplacements(String message, String pokemonName, boolean shouldAlertShiny) {
        message = message.replace("{name}", pokemonName);

        if (shouldAlertShiny) {
            message = message.replace("{shiny}", I18n.get(CobblemonSpawnAlertsClient.MOD_ID +".shiny"));
        } else {
            message = message.replace("{shiny}", "");
        }

        return message;
    }
}
