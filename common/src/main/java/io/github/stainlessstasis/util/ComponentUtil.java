package io.github.stainlessstasis.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;

public class ComponentUtil {
    public static Component convertFromAdventureComponent(net.kyori.adventure.text.Component adventureComponent) {
        String json = JSONComponentSerializer.json().serialize(adventureComponent);
        return Component.Serializer.fromJson(json, RegistryAccess.EMPTY);
    }

    public static Component convertFromAdventure(String string) {
        return convertFromAdventureComponent(MiniMessage.miniMessage().deserialize(string));
    }
}
