package io.github.stainlessstasis.util;

import io.github.stainlessstasis.CobblemonSpawnAlertsClient;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class TranslatedMessageUtil {
    public static void send(String translationKey, Object... args) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }

        String translated = I18n.get(translationKey, args);
        Component component = ComponentUtil.convertFromAdventure(translated);
        player.sendSystemMessage(component);
    }
}
