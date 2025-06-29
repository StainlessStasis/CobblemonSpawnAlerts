package io.github.stainlessstasis.util;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.config.MessageTemplates;
import io.github.stainlessstasis.config.PokemonConfig;
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

    // this will eventually be used on server & client but rn just client so yeah thats why this is here
    public static String applyDynamicReplacements(String message, PokemonEntity pokemonEntity, PokemonConfig.PokemonSpecificConfig config) {
        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = pokemon.getSpecies().getName();
        MessageTemplates messageTemplates = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        int level = pokemon.getLevel();
        IVs ivs = pokemon.getIvs();
        Nature nature = pokemon.getNature();
        Gender gender = pokemon.getGender();

        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        boolean isHoverEnabled = config.showInfoAsHover();
        String hoverText = "";

        // Shiny
        boolean shouldAlertShiny = config.alertShiny() && pokemon.getShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", I18n.get(messageTemplates.shiny()));
            message = message.replace("{shiny_unformatted}", I18n.get(messageTemplates.shiny_unformatted()));
        }
        message = message.replace("{shiny}", "");
        message = message.replace("{shiny_unformatted}", "");

        // Legendary/Mythical/Ultra Beast
        if (config.showLegendary()) {
            String nameLower = pokemonName.toLowerCase();
            if (RarityUtil.isLegendary(nameLower)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.legendary()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.legendary_unformatted()));
            } else if (RarityUtil.isMythical(nameLower)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.mythical()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.mythical_unformatted()));
            } else if (RarityUtil.isUltraBeast(nameLower)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.ultrabeast()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.ultrabeast_unformatted()));
            } else if (RarityUtil.isParadox(pokemonName.toLowerCase())) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.paradox()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.paradox_unformatted()));
            }
        }
        message = message.replace("{legendary}", "");
        message = message.replace("{legendary_unformatted}", "");

        // Level
        if (config.showLevel()) {
            String configMessage = isHoverEnabled ? messageTemplates.level_hover() : messageTemplates.level();
            String levelMessage = I18n.get(configMessage, level);

            if (isHoverEnabled) {
                hoverText += levelMessage + "\n";
            } else {
                message = message.replace("{level}", levelMessage);
            }
            message = message.replace("{level_unformatted}", I18n.get(messageTemplates.level_unformatted(), level));
        }
        message = message.replace("{level}", "");
        message = message.replace("{level_unformatted}", "");

        // IVs
        if (config.showIVs()) {
            String configMessage = isHoverEnabled ? messageTemplates.ivs_hover() : messageTemplates.ivs();
            String ivsMessage =
                    I18n.get(configMessage,
                    ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                    ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED));
            if (isHoverEnabled) {
                hoverText += ivsMessage + "\n";
            } else {
                message = message.replace("{ivs}", ivsMessage);
            }
            message = message.replace("{ivs_unformatted}", I18n.get(messageTemplates.ivs_unformatted(),
                    ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                    ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)));
        }
        message = message.replace("{ivs}", "");
        message = message.replace("{ivs_unformatted}", "");

        // Nature
        if (config.showNature()) {
            String configMessage = isHoverEnabled ? messageTemplates.nature_hover() : messageTemplates.nature();
            String natureString = nature.getDisplayName().replace("cobblemon.nature.", "");
            natureString = natureString.substring(0, 1).toUpperCase() + natureString.substring(1);
            String natureMessage = I18n.get(configMessage, natureString);
            if (isHoverEnabled) {
                hoverText += natureMessage + "\n";
            } else {
                message = message.replace("{nature}", natureMessage);
            }
            message = message.replace("{nature_unformatted}", I18n.get(messageTemplates.nature_unformatted(), natureString));
        }
        message = message.replace("{nature}", "");
        message = message.replace("{nature_unformatted}", "");

        // Gender
        if (config.showGender()) {
            String genderString = switch (gender) {
                case MALE -> messageTemplates.male();
                case FEMALE -> messageTemplates.female();
                case GENDERLESS -> messageTemplates.genderless();
            };
            genderString = I18n.get(genderString);
            String configMessage = isHoverEnabled ? messageTemplates.gender_hover() : messageTemplates.gender();
            String genderMessage = I18n.get(configMessage, genderString);
            if (isHoverEnabled) {
                hoverText += genderMessage + "\n";
            } else {
                message = message.replace("{gender}", genderMessage);
            }
            message = message.replace("{gender_unformatted}",
                    I18n.get(messageTemplates.gender_unformatted(), gender.toString().charAt(0) + gender.toString().toLowerCase().substring(1)));
        }
        message = message.replace("{gender}", "");
        message = message.replace("{gender_unformatted}", "");

        // Coordinates
        if (config.showCoordinates()) {
            String configMessage = isHoverEnabled ? messageTemplates.coords_hover() : messageTemplates.coords();
            String coordsMessage = I18n.get(configMessage,
                    (int)pokemonEntity.getX(), (int)pokemonEntity.getY(), (int)pokemonEntity.getZ());
            if (isHoverEnabled) {
                hoverText += coordsMessage + "\n";
            } else {
                message = message.replace("{coords}", coordsMessage);
            }
            message = message.replace("{coords_unformatted}", I18n.get(messageTemplates.coords_unformatted(),
                    (int)pokemonEntity.getX(), (int)pokemonEntity.getY(), (int)pokemonEntity.getZ()));
        }
        message = message.replace("{coords}", "");
        message = message.replace("{coords_unformatted}", "");

        // Hover
        if (isHoverEnabled) {
            message = "<hover:show_text:\"" + hoverText+"\">" + message + "</hover>";
        }

        return message;
    }
}
