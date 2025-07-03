package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.config.MessageTemplates;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.alert.StatDisplayMode;
import io.github.stainlessstasis.network.AlertDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.Map;

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
}
