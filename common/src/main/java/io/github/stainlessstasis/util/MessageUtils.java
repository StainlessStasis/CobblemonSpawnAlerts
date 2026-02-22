package io.github.stainlessstasis.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class MessageUtils {
    public static void sendTranslated(String translationKey, Object... args) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }

        String translated = Component.translatable(translationKey, args).getString();
        Component component = ComponentUtil.parseMarkup(translated);
        player.sendSystemMessage(component);
    }

    public static String getTranslated(String translationKey, Object... args) {
        return Component.translatable(translationKey, args).getString();
    }
}
