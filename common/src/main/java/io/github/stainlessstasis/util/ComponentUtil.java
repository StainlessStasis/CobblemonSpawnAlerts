package io.github.stainlessstasis.util;

import io.github.stainlessstasis.platform.Services;
import net.minecraft.network.chat.Component;

public class ComponentUtil {
    public static Component parseMarkup(String markup) {
        return Services.PLATFORM.parseMarkup(markup);
    }
}
