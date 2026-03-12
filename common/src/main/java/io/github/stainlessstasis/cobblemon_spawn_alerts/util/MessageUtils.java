package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class MessageUtils {
    public static void sendTranslated(String translationKey, Object... args) {
        sendTranslated(translationKey, null, args);
    }

    public static void sendTranslated(String translationKey, @Nullable ClickEvent clickEvent, Object... args) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }

        String translated = Component.translatable(translationKey, args).getString();
        MutableComponent component = Services.PLATFORM.parseMarkup(translated);
        if (clickEvent != null) {
            component.withStyle(style -> style.withClickEvent(clickEvent));
        }
        player.sendSystemMessage(component);
    }

    public static String getTranslated(String translationKey, Object... args) {
        return Component.translatable(translationKey, args).getString();
    }
}
