package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.mojang.datafixers.util.Pair;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.MessageTemplates;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.network.PokemonStats;
import io.github.stainlessstasis.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;

public class AlertHandler {
    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();

    public static void clearCache() {
        alreadyAlerted.clear();
    }

    public static void alertClientside(PokemonEntity pokemonEntity) {
        alertClientside(pokemonEntity, EVs.createEmpty());
    }

    public static void alertClientside(PokemonEntity pokemonEntity, EVs evYield) {
        if (pokemonEntity.getOwnerUUID() != null) {
            return;
        }

        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = PokemonNameUtil.getTranslatedName(pokemon);
        int dexId = pokemon.getSpecies().getNationalPokedexNumber();

        alert(new AlertDataPacket(
                new PokemonSpawnData(
                        pokemonName,
                        pokemon.getUuid(),
                        pokemonEntity.position().toVector3f(),
                        pokemon.getSpecies().getNationalPokedexNumber()),
                new PokemonStats(
                        pokemon.getLevel(),
                        pokemon.getIvs(),
                        evYield),
                new PokemonTraits(
                        pokemon.getShiny(),
                        RarityUtil.isLegendary(dexId),
                        RarityUtil.isMythical(dexId),
                        RarityUtil.isUltraBeast(dexId),
                        RarityUtil.isParadox(dexId)),
                pokemon.getNature().getName().getPath(),
                pokemon.getGender().name()
        ));
    }

    public static void alert(AlertDataPacket alertData) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.isReloading()) {
            return;
        }
        if (alreadyAlerted.contains(alertData.spawnData().pokemonUUID())) {
            return;
        }

        MainConfig config = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().pokemonTranslationKey());
        Pair<Boolean, PokemonConfig.PokemonSpecificConfig> result = getConfigForPokemon(pokemonName);
        boolean isInConfig = result.getFirst();
        PokemonConfig.PokemonSpecificConfig pokemonConfig = result.getSecond();

        if (!pokemonConfig.enabled()) {
            return;
        }

        boolean shouldAlertLegend = alertData.traits().isLegendary() && config.alertAllLegendaries();
        boolean shouldAlertMythical = alertData.traits().isMythical() && config.alertAllMythicals();
        boolean shouldAlertUltra = alertData.traits().isUltraBeast() && config.alertAllUltraBeasts();
        boolean shouldAlertParadox = alertData.traits().isParadox() && config.alertAllParadox();

        boolean shouldAlertNotInDex = config.alertAllNotInDex();
        boolean shouldAlertUncaught = config.alertAllUncaught();
        Species species = PokemonSpecies.INSTANCE.getByPokedexNumber(alertData.spawnData().dexId(), Cobblemon.MODID);
        SpeciesDexRecord record = dex.getSpeciesRecord(species.resourceIdentifier);
        if (record != null) {
            shouldAlertNotInDex = false;
            if (record.hasAtLeast(PokedexEntryProgress.CAUGHT)) {
                shouldAlertUncaught = false;
            }
        }

        boolean shouldAlertShiny =
                isInConfig ?
                        alertData.traits().isShiny() && pokemonConfig.alertShiny() || config.alertAllShinies()
                        :
                        alertData.traits().isShiny() && config.alertAllShinies();
        boolean shouldAlertInConfig = pokemonConfig.alwaysAlert() || shouldAlertShiny;
        boolean shouldAlertNotInConfig =
                shouldAlertShiny
                        || shouldAlertLegend
                        || shouldAlertMythical
                        || shouldAlertUltra
                        || shouldAlertParadox
                        || shouldAlertNotInDex
                        || shouldAlertUncaught;

        if (isInConfig) {
            if (!shouldAlertInConfig) {
                return;
            }
        } else {
            if (!shouldAlertNotInConfig) {
                return;
            }
        }

        alreadyAlerted.add(alertData.spawnData().pokemonUUID());

        // send the custom alert if one exits
        String message;
        if (!Objects.equals(pokemonConfig.customAlertMessage(), "")) {
            message = applyDynamicReplacements(pokemonConfig.customAlertMessage(), pokemonConfig, alertData);
            MessageUtils.sendTranslated(message);
            return;
        }

        // use the default message if no custom one is provided
        message = MessageUtils.getTranslated(CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates().fullSpawnMessage());
        message = applyDynamicReplacements(message, pokemonConfig, alertData);
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }

    public static void alertDespawned(DespawnDataPacket despawnData) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.isReloading()) {
            return;
        }

        MessageTemplates messageTemplates = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        String message = MessageUtils.getTranslated(CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates().despawnMessage());

        message = switch (DespawnReason.valueOf(despawnData.despawnReason())) {
            case CAPTURED -> message.replace("{despawned}", I18n.get(messageTemplates.despawnReason_Captured(), despawnData.playerName()));
            case DESPAWNED -> message.replace("{despawned}", I18n.get(messageTemplates.despawnReason_Despawned()));
            case FAINTED -> message.replace("{despawned}", I18n.get(messageTemplates.despawnReason_Fainted(), despawnData.playerName()));
        };

        message = applyDynamicReplacements(message, getConfigForPokemon(despawnData.spawnData().pokemonTranslationKey()).getSecond(),
                new AlertDataPacket(
                        despawnData.spawnData(),
                        new PokemonStats(-1, IVs.createRandomIVs(0), EVs.createEmpty()),
                        despawnData.traits(),
                        Natures.INSTANCE.getNAUGHTY().getName().getPath(),
                        Gender.GENDERLESS.name()

                ));
        Component component = ComponentUtil.convertFromAdventure(message);
        player.sendSystemMessage(component);
    }

    public static Pair<Boolean, PokemonConfig.PokemonSpecificConfig> getConfigForPokemon(String pokemonName) {
        Set<String> pokemonNames = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().keySet();
        String fixedPokemonName = PokemonNameUtil.fixName(pokemonName);

        for (String name : pokemonNames) {
            if (name.startsWith("default")) {
                continue;
            }

            String fixedName = name.toLowerCase().replaceAll("[ _-]", "");
            if (fixedName.contains(fixedPokemonName)) {
                if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(name)
                        instanceof PokemonConfig.PokemonSpecificConfig _config) {
                    return Pair.of(true, _config);
                }
            }
        }

        if (CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            return Pair.of(false, _config);
        } else {
            CobblemonSpawnAlerts.LOGGER.warn("No default config found in `pokemon.json`, creating a new one.");
            return Pair.of(false, PokemonConfig.PokemonSpecificConfig.createDefault());
        }

    }

    public static String applyDynamicReplacements(String message, PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData) {
        MessageTemplates messageTemplates = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMessageTemplates();

        int level = alertData.stats().level();
        IVs ivs = alertData.stats().ivs();
        EVs evYield = alertData.stats().evYield();
        Nature nature = Natures.INSTANCE.getNature(alertData.natureID());
        Gender gender = Gender.valueOf(alertData.genderID());

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().pokemonTranslationKey());

        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        String hoverText = "";
        Map<String, StatDisplayMode> displayModes = config.statDisplayModes();
        StatDisplayMode levelDisplayMode = displayModes.get("level");
        StatDisplayMode ivsDisplayMode = displayModes.get("ivs");
        StatDisplayMode evsDisplayMode = displayModes.get("evs");
        StatDisplayMode natureDisplayMode = displayModes.get("nature");
        StatDisplayMode genderDisplayMode = displayModes.get("gender");
        StatDisplayMode coordinatesDisplayMode = displayModes.get("coordinates");
        StatDisplayMode biomeDisplayMode = displayModes.get("biome");

        // Shiny
        boolean shouldAlertShiny = config.alertShiny() && alertData.traits().isShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", I18n.get(messageTemplates.shiny()));
            message = message.replace("{shiny_unformatted}", I18n.get(messageTemplates.shiny_unformatted()));
        }
        message = message.replace("{shiny}", "");
        message = message.replace("{shiny_unformatted}", "");

        // Legendary/Mythical/Ultra Beast/Paradox
        if (config.showLegendary()) {
            int dexId = alertData.spawnData().dexId();
            if (RarityUtil.isLegendary(dexId)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.legendary()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.legendary_unformatted()));
            } else if (RarityUtil.isMythical(dexId)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.mythical()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.mythical_unformatted()));
            } else if (RarityUtil.isUltraBeast(dexId)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.ultrabeast()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.ultrabeast_unformatted()));
            } else if (RarityUtil.isParadox(dexId)) {
                message = message.replace("{legendary}", I18n.get(messageTemplates.paradox()));
                message = message.replace("{legendary_unformatted}", I18n.get(messageTemplates.paradox_unformatted()));
            }
        }
        message = message.replace("{legendary}", "");
        message = message.replace("{legendary_unformatted}", "");

        // Level
        if (levelDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = levelDisplayMode == StatDisplayMode.HOVER;
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
        if (ivsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = ivsDisplayMode == StatDisplayMode.HOVER;
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

        // EVs
        if (evsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = evsDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.evs_hover() : messageTemplates.evs();
            String evsMessage =
                    I18n.get(configMessage,
                            evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                            evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED));
            if (isHoverEnabled) {
                hoverText += evsMessage + "\n";
            } else {
                message = message.replace("{evs}", evsMessage);
            }
            message = message.replace("{evs_unformatted}", I18n.get(messageTemplates.evs_unformatted(),
                    evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                    evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED)));
        }
        message = message.replace("{evs}", "");
        message = message.replace("{evs_unformatted}", "");

        // Nature
        if (natureDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = natureDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.nature_hover() : messageTemplates.nature();
            String natureString = nature.getDisplayName().replace("cobblemon.nature.", "");
            natureString = StringUtil.capitalize(natureString);
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
        if (genderDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = genderDisplayMode == StatDisplayMode.HOVER;
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

        Vector3f coords = new Vector3f(alertData.spawnData().position().x, alertData.spawnData().position().y, alertData.spawnData().position().z);
        // Coordinates
        if (coordinatesDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = coordinatesDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.coords_hover() : messageTemplates.coords();
            String coordsMessage = I18n.get(configMessage, (int)coords.x, (int)coords.y, (int)coords.z);
            if (isHoverEnabled) {
                hoverText += coordsMessage + "\n";
            } else {
                message = message.replace("{coords}", coordsMessage);
            }
            message = message.replace("{coords_unformatted}", I18n.get(messageTemplates.coords_unformatted(), (int)coords.x, (int)coords.y, (int)coords.z));
        }
        message = message.replace("{coords}", "");
        message = message.replace("{coords_unformatted}", "");

        // Biome
        if (biomeDisplayMode != StatDisplayMode.DISABLED && Minecraft.getInstance().level != null) {
            boolean isHoverEnabled = biomeDisplayMode == StatDisplayMode.HOVER;
            BlockPos blockPos = BlockPos.containing(new Vec3(coords));
            Holder<Biome> biomeHolder = Minecraft.getInstance().level.getBiome(blockPos);
            String biomeName = I18n.get(biomeHolder.unwrapKey().get().location().toShortLanguageKey());
            biomeName = StringUtil.makeBeautiful(biomeName);

            String configMessage = isHoverEnabled ? messageTemplates.biome_hover() : messageTemplates.biome();
            String biomeMessage = I18n.get(configMessage, biomeName);
            if (isHoverEnabled) {
                hoverText += biomeMessage + "\n";
            } else {
                message = message.replace("{biome}", biomeMessage);
            }
            message = message.replace("{biome_unformatted}", I18n.get(messageTemplates.biome_unformatted(), biomeName));
        }
        message = message.replace("{biome}", "");
        message = message.replace("{biome_unformatted}", "");

        // Hover
        if (!hoverText.isEmpty()) {
            message = "<hover:show_text:\"" + hoverText+"\">" + message + "</hover>";
        }

        return message;
    }
}
