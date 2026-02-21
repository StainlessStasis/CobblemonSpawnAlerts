package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokedex.FormDexRecord;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.mojang.datafixers.util.Pair;
import io.github.stainlessstasis.compat.JourneymapCompat;
import io.github.stainlessstasis.config.MainConfig;
import io.github.stainlessstasis.config.MessageTemplates;
import io.github.stainlessstasis.config.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.network.PokemonStats;
import io.github.stainlessstasis.platform.Platform;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertHandler {
    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();

    public static void clearCache() {
        alreadyAlerted.clear();
    }

    public static void alertClientside(PokemonEntity pokemonEntity) {
        EVs defaultEVYield = EvsUtil.getYield(pokemonEntity.getPokemon().getSpecies().getNationalPokedexNumber());
        alertClientside(pokemonEntity, defaultEVYield);
    }

    public static void alertClientside(PokemonEntity pokemonEntity, EVs evYield) {
        if (pokemonEntity.getOwnerUUID() != null) {
            return;
        }

        String nearestPlayerName = "N/A";
        if (Minecraft.getInstance().player instanceof Player player) {
            nearestPlayerName = player.getName().getString();
        }

        Pokemon pokemon = pokemonEntity.getPokemon();
        String pokemonName = PokemonNameUtil.getTranslatedName(pokemon);
        int dexId = pokemon.getSpecies().getNationalPokedexNumber();

        alert(new AlertDataPacket(
                new PokemonSpawnData(
                        pokemonName,
                        pokemon.getUuid(),
                        pokemonEntity.position().toVector3f(),
                        pokemon.getSpecies().getNationalPokedexNumber(),
                        nearestPlayerName,
                        BiomeUtil.getBiomeKey(pokemonEntity.level(), pokemonEntity.position()),
                        DimensionUtil.getDimensionKey(pokemonEntity)),
                new PokemonStats(
                        pokemon.getLevel(),
                        pokemon.getIvs(),
                        evYield
                ),
                new PokemonRarity(
                        pokemon.getShiny(),
                        RarityUtil.isLegendary(dexId),
                        RarityUtil.isMythical(dexId),
                        RarityUtil.isUltraBeast(dexId),
                        RarityUtil.isParadox(dexId),
                        RarityUtil.isStarter(dexId)),
                new PokemonTraits(
                        pokemon.getNature().getName().getPath(),
                        pokemon.getAbility().getName(),
                        pokemon.getGender().name(),
                        pokemon.getForm().getName()
                )
        ));
    }

    public static void alert(AlertDataPacket alertData) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.isReloading()) {
            return;
        }
        if (alreadyAlerted.contains(alertData.spawnData().pokemonUUID())) {
            return;
        }

        MainConfig mainConfig = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().translatedPokemonName());
        Pair<Boolean, PokemonConfig.PokemonSpecificConfig> result = getConfigForPokemon(pokemonName, alertData.spawnData().dexId());
        boolean isInConfig = result.getFirst();
        PokemonConfig.PokemonSpecificConfig pokemonConfig = result.getSecond();

        if (!pokemonConfig.enabled()) {
            return;
        }

        boolean isShiny = alertData.rarity().isShiny();
        boolean isLegend = alertData.rarity().isLegendary();
        boolean isMythical = alertData.rarity().isMythical();
        boolean isUltra = alertData.rarity().isUltraBeast();
        boolean isParadox = alertData.rarity().isParadox();
        boolean isStarter = alertData.rarity().isStarter();
        boolean isInDex = false;
        boolean isCaught = false;

        // Check if should alert for rarity/shiny
        boolean shouldAlertShiny =
                isInConfig ?
                        isShiny && (pokemonConfig.alertShiny() || mainConfig.alertAllShinies())
                        :
                        isShiny && mainConfig.alertAllShinies();
        boolean shouldAlertLegend = isLegend && mainConfig.alertAllLegendaries();
        boolean shouldAlertMythical = isMythical && mainConfig.alertAllMythicals();
        boolean shouldAlertUltra = isUltra && mainConfig.alertAllUltraBeasts();
        boolean shouldAlertParadox = isParadox && mainConfig.alertAllParadox();
        boolean shouldAlertStarter = isStarter && mainConfig.alertAllStarter();

        // Check if should alert for dex
        boolean shouldAlertNotInDex = mainConfig.alertAllNotInDex();
        boolean shouldAlertUncaught = mainConfig.alertAllUncaught();
        Species species = PokemonSpecies.getByPokedexNumber(alertData.spawnData().dexId(), Cobblemon.MODID);
        SpeciesDexRecord record = dex.getSpeciesRecord(species.resourceIdentifier);
        if (record != null) {
            shouldAlertNotInDex = false;
            isInDex = true;
            if (record.hasAtLeast(PokedexEntryProgress.CAUGHT)) {
                shouldAlertUncaught = false;
                isCaught = true;
            }
        }

        // Check if should alert for HA
        boolean shouldAlertHA =
                HiddenAbilityUtil.hasHiddenAbility(alertData.spawnData().dexId(), alertData.traits().formID(), alertData.traits().abilityID())
        && (pokemonConfig.alertHiddenAbility() || mainConfig.alertAllHA());

        // Check if should alert for IV and EV hunting
        final MainConfig.IVHunting ivHunting = mainConfig.ivHunting();
        final MainConfig.EVHunting evHunting = mainConfig.evHunting();

        boolean shouldAlertIVs = false;
        if (ivHunting.enabled()) {
            final IVs ivs = alertData.stats().ivs();

            boolean meetsMinReqs = false;
            if (ivHunting.requireAllMinimumsMet()) {
                if (
                    (ivHunting.minHp() <= 0 || ivs.get(Stats.HP) >= ivHunting.minHp())
                    && (ivHunting.minAtk() <= 0 || ivs.get(Stats.ATTACK) >= ivHunting.minAtk())
                    && (ivHunting.minDef() <= 0 || ivs.get(Stats.DEFENCE) >= ivHunting.minDef())
                    && (ivHunting.minSpAtk() <= 0 || ivs.get(Stats.SPECIAL_ATTACK) >= ivHunting.minSpAtk())
                    && (ivHunting.minSpDef() <= 0 || ivs.get(Stats.SPECIAL_DEFENCE) >= ivHunting.minSpDef())
                    && (ivHunting.minSpeed() <= 0 || ivs.get(Stats.SPEED) >= ivHunting.minSpeed())
                ) {
                    meetsMinReqs = true;
                }
            } else {
                if (
                    (ivHunting.minHp() > 0 && ivs.get(Stats.HP) >= ivHunting.minHp())
                    || (ivHunting.minAtk() > 0 && ivs.get(Stats.ATTACK) >= ivHunting.minAtk())
                    || (ivHunting.minDef() > 0 && ivs.get(Stats.DEFENCE) >= ivHunting.minDef())
                    || (ivHunting.minSpAtk() > 0 && ivs.get(Stats.SPECIAL_ATTACK) >= ivHunting.minSpAtk())
                    || (ivHunting.minSpDef() > 0 && ivs.get(Stats.SPECIAL_DEFENCE) >= ivHunting.minSpDef())
                    || (ivHunting.minSpeed() > 0 && ivs.get(Stats.SPEED) >= ivHunting.minSpeed())
                ) {
                    meetsMinReqs = true;
                }
            }

            AtomicInteger numPerfect = new AtomicInteger();
            ivs.forEach(iv -> {
                if (iv.getValue() >= IVs.MAX_VALUE) numPerfect.getAndIncrement();
            });
            shouldAlertIVs = numPerfect.get() >= ivHunting.minPerfectIVs() && meetsMinReqs;
        }

        boolean shouldAlertEVs = false;
        if (evHunting.enabled()) {
            final EVs evs = alertData.stats().evYield();

            shouldAlertEVs =
                (evHunting.minHp() > 0 && evs.get(Stats.HP) >= evHunting.minHp())
                || (evHunting.minAtk() > 0 && evs.get(Stats.ATTACK) >= evHunting.minAtk())
                || (evHunting.minDef() > 0 && evs.get(Stats.DEFENCE) >= evHunting.minDef())
                || (evHunting.minSpAtk() > 0 && evs.get(Stats.SPECIAL_ATTACK) >= evHunting.minSpAtk())
                || (evHunting.minSpDef() > 0 && evs.get(Stats.SPECIAL_DEFENCE) >= evHunting.minSpDef())
                || (evHunting.minSpeed() > 0 && evs.get(Stats.SPEED) >= evHunting.minSpeed());
        }

        // Check level filter
        final MainConfig.LevelFilter levelFilter = mainConfig.levelFilter();
        boolean passesLevelFilter = true;

        if (levelFilter.enabled()) {
            int level = alertData.stats().level();
            if (level < levelFilter.minLevel() || level > levelFilter.maxLevel()) {
                passesLevelFilter = false;
            }
        }

        // Finalize alert check
        boolean shouldAlertInConfig = pokemonConfig.alwaysAlert() || shouldAlertShiny || shouldAlertHA;
        boolean shouldAlertNotInConfig =
                passesLevelFilter &&
                        (
                            shouldAlertShiny
                            || shouldAlertLegend
                            || shouldAlertMythical
                            || shouldAlertUltra
                            || shouldAlertParadox
                            || shouldAlertStarter
                            || shouldAlertNotInDex
                            || shouldAlertUncaught
                            || mainConfig.alertEverything()
                            || shouldAlertIVs
                            || shouldAlertEVs
                            || shouldAlertHA
                        );

        // Debug
        if (mainConfig.debug()) {
            DebugAlertCondition alertCondition = DebugAlertCondition.NONE;
            if (mainConfig.alertEverything()) alertCondition = DebugAlertCondition.ALERT_EVERYTHING;
            if (pokemonConfig.alwaysAlert()) alertCondition = DebugAlertCondition.ALWAYS_ALERT;
            if (shouldAlertShiny) {
                if (mainConfig.alertAllShinies()) {
                    alertCondition = DebugAlertCondition.ALERT_ALL_SHINY;
                } else {
                    alertCondition = DebugAlertCondition.ALERT_SHINY;
                }
            }
            if (shouldAlertHA) {
                if (mainConfig.alertAllHA()) {
                    alertCondition = DebugAlertCondition.ALERT_ALL_HIDDEN_ABILITY;
                } else {
                    alertCondition = DebugAlertCondition.ALERT_HIDDEN_ABILITY;
                }
            }
            if (shouldAlertLegend) alertCondition = DebugAlertCondition.ALERT_ALL_LEGENDARY;
            if (shouldAlertMythical) alertCondition = DebugAlertCondition.ALERT_ALL_MYTHICAL;
            if (shouldAlertUltra) alertCondition = DebugAlertCondition.ALERT_ALL_ULTRA_BEAST;
            if (shouldAlertParadox) alertCondition = DebugAlertCondition.ALERT_ALL_PARADOX;
            if (shouldAlertStarter) alertCondition = DebugAlertCondition.ALERT_ALL_STARTER;
            if (shouldAlertIVs) alertCondition = DebugAlertCondition.IV_HUNTING;
            if (shouldAlertEVs) alertCondition = DebugAlertCondition.EV_HUNTING;
            if (shouldAlertUncaught) alertCondition = DebugAlertCondition.ALERT_ALL_UNCAUGHT;
            if (shouldAlertNotInDex) alertCondition = DebugAlertCondition.ALERT_ALL_NOT_IN_DEX;

            String message = MessageUtils.getTranslated("cobblemon-spawn-alerts.debug_alert_condition", alertCondition.name());
            StringBuilder debugHoverBuilder = new StringBuilder();
            message = applyDynamicReplacements(message, pokemonConfig, alertData, debugHoverBuilder);
            Component messageComponent = ComponentUtil.parseMarkup(message);
            String debugHoverMarkup = debugHoverBuilder.toString() + "<color value=#55FF55>Click to toggle glow</color>";
            Component hoverComponent = ComponentUtil.parseMarkup(debugHoverMarkup);
            ClickEvent.Action debugClickAction = Services.PLATFORM.getPlatform() == Platform.FABRIC
                    ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
            messageComponent = messageComponent.copy().withStyle(Style.EMPTY
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
                    .withClickEvent(new ClickEvent(debugClickAction, "/csa glow " + alertData.spawnData().pokemonUUID())));
            player.sendSystemMessage(messageComponent);
        }

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

        // play custom alert sound if one exists
        if (!(Objects.equals(pokemonConfig.customAlertSound(), ""))) {
            String[] split = StringUtil.splitIdentifier(pokemonConfig.customAlertSound());
            if (!split[0].equals("NO NAMESPACE")) {
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
                SoundEvent sound = SoundEvent.createFixedRangeEvent(resourceLocation, -1f);
                player.playNotifySound(sound, SoundSource.MASTER, 1f, 1f);
            } else {
                player.sendSystemMessage(ComponentUtil.parseMarkup(MessageUtils.getTranslated("cobblemon-spawn-alerts.outdated_sound")));
            }
        }

        // play alert sounds if they exist
        else {
            HashMap<String, Boolean> traits = new HashMap<>();
            traits.put("shiny", isShiny);
            traits.put("legendary", isLegend);
            traits.put("mythical", isMythical);
            traits.put("ultrabeast", isUltra);
            traits.put("paradox", isParadox);
            traits.put("starter", isStarter);
            traits.put("unregistered", !isInDex);
            traits.put("uncaught", !isCaught);
            // TODO: change this if i ever add individual iv/ev hunting
            traits.put("ivs", shouldAlertIVs);
            traits.put("evs", shouldAlertEVs);

            for (String soundTrait : pokemonConfig.sounds().keySet()) {
                String soundID = pokemonConfig.sounds().get(soundTrait);
                if (traits.get(soundTrait) && !soundID.isEmpty()) {
                    String[] split = StringUtil.splitIdentifier(soundID);
                    if (!split[0].equals("NO NAMESPACE")) {
                        ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
                        SoundEvent sound = SoundEvent.createFixedRangeEvent(resourceLocation, -1f);
                        player.playNotifySound(sound, SoundSource.MASTER, 1f, 1f);
                    } else {
                        player.sendSystemMessage(ComponentUtil.parseMarkup(MessageUtils.getTranslated("cobblemon-spawn-alerts.outdated_sound")));
                    }
                }
            }
        }

        // Autoglow
        if (pokemonConfig.autoGlow()) {
            CobblemonSpawnAlertsClient.glowing.add(alertData.spawnData().pokemonUUID());
        }

        // send the custom alert if one exits
        String message;
        StringBuilder hoverBuilder = new StringBuilder();
        if (!Objects.equals(pokemonConfig.customAlertMessage(), "")) {
            message = applyDynamicReplacements(pokemonConfig.customAlertMessage(), pokemonConfig, alertData, hoverBuilder);
        } else {
            // use the default message if no custom one is provided
            message = MessageUtils.getTranslated(CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates().fullSpawnMessage());
            message = applyDynamicReplacements(message, pokemonConfig, alertData, hoverBuilder);
        }
        Component spawnComponent = ComponentUtil.parseMarkup(message);
        String spawnHoverMarkup = hoverBuilder.toString() + "<color value=#55FF55>Click to toggle glow</color>";
        Component spawnHoverComponent = ComponentUtil.parseMarkup(spawnHoverMarkup);
        ClickEvent.Action spawnClickAction = Services.PLATFORM.getPlatform() == Platform.FABRIC
                ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        spawnComponent = spawnComponent.copy().withStyle(Style.EMPTY
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, spawnHoverComponent))
                .withClickEvent(new ClickEvent(spawnClickAction, "/csa glow " + alertData.spawnData().pokemonUUID())));
        player.sendSystemMessage(spawnComponent);

        // journeymap compat
        PokemonConfig.JourneymapConfig jmConfig = pokemonConfig.journeyMap();
        if (Services.PLATFORM.isModLoaded("journeymap") && jmConfig.enableWaypoint()) {
            Vector3f pos = alertData.spawnData().position();
            BlockPos blockPos = new BlockPos((int)pos.x, (int)pos.y, (int)pos.z);
            JourneymapCompat.createWaypoint(blockPos, alertData, jmConfig);
        }
    }

    public static void alertDespawned(DespawnDataPacket despawnData) {
        if (!(Minecraft.getInstance().player instanceof Player player)) {
            return;
        }
        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.isReloading()) {
            return;
        }

        CobblemonSpawnAlertsClient.glowing.remove(despawnData.spawnData().pokemonUUID());
        if (Services.PLATFORM.isModLoaded("journeymap")) {
            JourneymapCompat.removeWaypoint(despawnData.spawnData().pokemonUUID());
        }

        PokemonConfig.PokemonSpecificConfig pokemonConfig = getConfigForPokemon(despawnData.spawnData().translatedPokemonName(), despawnData.spawnData().dexId()).getSecond();
        if (!pokemonConfig.alertDespawned()) {
            return;
        }

        MessageTemplates messageTemplates = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        String message = MessageUtils.getTranslated(CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates().despawnMessage());

        message = switch (DespawnReason.valueOf(despawnData.despawnReason())) {
            case CAPTURED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Captured(), despawnData.playerName()).getString());
            case DESPAWNED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Despawned()).getString());
            case FAINTED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Fainted(), despawnData.playerName()).getString());
        };

        AlertDataPacket despawnAlertData = new AlertDataPacket(
                despawnData.spawnData(),
                new PokemonStats(-1, IVs.createRandomIVs(0), EVs.createEmpty()),
                despawnData.rarity(),
                new PokemonTraits(
                        Natures.NAUGHTY.getName().getPath(),
                        Abilities.get("levitate").create(false, Priority.LOWEST).getName(),
                        Gender.GENDERLESS.name(),
                        "Normal"
                )
        );
        StringBuilder despawnHoverBuilder = new StringBuilder();
        message = applyDynamicReplacements(message, pokemonConfig, despawnAlertData, despawnHoverBuilder);
        Component despawnComponent = ComponentUtil.parseMarkup(message);
        String despawnHoverMarkup = despawnHoverBuilder.toString() + "<color value=#55FF55>Click to toggle glow</color>";
        Component despawnHoverComponent = ComponentUtil.parseMarkup(despawnHoverMarkup);
        ClickEvent.Action despawnClickAction = Services.PLATFORM.getPlatform() == Platform.FABRIC
                ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND;
        despawnComponent = despawnComponent.copy().withStyle(Style.EMPTY
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, despawnHoverComponent))
                .withClickEvent(new ClickEvent(despawnClickAction, "/csa glow " + despawnAlertData.spawnData().pokemonUUID())));
        player.sendSystemMessage(despawnComponent);
    }

    public static Pair<Boolean, PokemonConfig.PokemonSpecificConfig> getConfigForPokemon(String pokemonName, int dexID) {
        Set<String> pokemonNames = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().keySet();
        String fixedPokemonName = PokemonNameUtil.fixName(pokemonName);

        for (String name : pokemonNames) {
            if (name.startsWith("default")) {
                continue;
            }

            String fixedName = name.toLowerCase().replaceAll("[ _-]", "");
            if (fixedName.contains(fixedPokemonName) || fixedName.contains(String.valueOf(dexID))) {
                if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(name)
                        instanceof PokemonConfig.PokemonSpecificConfig _config) {
                    return Pair.of(true, _config);
                }
            }
        }

        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getPokemonConfig().pokemonConfigs().get(CobblemonSpawnAlerts.DEFAULT_POKEMON_CONFIG_NAME)
                instanceof PokemonConfig.PokemonSpecificConfig _config) {
            return Pair.of(false, _config);
        } else {
            CobblemonSpawnAlerts.LOGGER.warn("No default config found in `pokemon.json`, creating a new one.");
            return Pair.of(false, PokemonConfig.PokemonSpecificConfig.createDefault());
        }

    }

    public static String applyDynamicReplacements(String message, PokemonConfig.PokemonSpecificConfig config, AlertDataPacket alertData, StringBuilder hoverBuilder) {
        MessageTemplates messageTemplates = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates();

        int level = alertData.stats().level();
        IVs ivs = alertData.stats().ivs();
        EVs evYield = alertData.stats().evYield();
        Nature nature = Natures.getNature(alertData.traits().natureID());
        AbilityTemplate ability = Abilities.get(alertData.traits().abilityID());
        Gender gender = Gender.valueOf(alertData.traits().genderID());
        String nearestPlayer = alertData.spawnData().nearestPlayerName();

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().translatedPokemonName());

        message = message.replace("{name}", pokemonName);
        message = message.replace("{name_lower}", pokemonName.toLowerCase());
        message = message.replace("{name_upper}", pokemonName.toUpperCase());

        Map<String, StatDisplayMode> displayModes = config.statDisplayModes();
        StatDisplayMode levelDisplayMode = displayModes.get("level");
        StatDisplayMode ivsDisplayMode = displayModes.get("ivs");
        StatDisplayMode evsDisplayMode = displayModes.get("evs");
        StatDisplayMode natureDisplayMode = displayModes.get("nature");
        StatDisplayMode abilityDisplayMode = displayModes.get("ability");
        StatDisplayMode genderDisplayMode = displayModes.get("gender");
        StatDisplayMode coordinatesDisplayMode = displayModes.get("coordinates");
        StatDisplayMode biomeDisplayMode = displayModes.get("biome");
        StatDisplayMode nearestPlayerDisplayMode = displayModes.get("nearestPlayer");

        // Shiny
        boolean shouldAlertShiny = config.alertShiny() && alertData.rarity().isShiny();
        if (shouldAlertShiny) {
            message = message.replace("{shiny}", Component.translatable(messageTemplates.shiny()).getString());
            message = message.replace("{shiny_unformatted}", Component.translatable(messageTemplates.shiny_unformatted()).getString());
        }
        message = message.replace("{shiny}", "");
        message = message.replace("{shiny_unformatted}", "");

        // Legendary/Mythical/Ultra Beast/Paradox
        if (config.showLegendary()) {
            int dexId = alertData.spawnData().dexId();
            if (RarityUtil.isLegendary(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.legendary()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.legendary_unformatted()).getString());
            } else if (RarityUtil.isMythical(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.mythical()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.mythical_unformatted()).getString());
            } else if (RarityUtil.isUltraBeast(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.ultrabeast()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.ultrabeast_unformatted()).getString());
            } else if (RarityUtil.isParadox(dexId)) {
                message = message.replace("{legendary}", Component.translatable(messageTemplates.paradox()).getString());
                message = message.replace("{legendary_unformatted}", Component.translatable(messageTemplates.paradox_unformatted()).getString());
            }
        }
        message = message.replace("{legendary}", "");
        message = message.replace("{legendary_unformatted}", "");

        // Level
        if (levelDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = levelDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.level_hover() : messageTemplates.level();
            String levelMessage = Component.translatable(configMessage, level).getString();

            if (isHoverEnabled) {
                hoverBuilder.append(levelMessage).append("\n");
            } else {
                message = message.replace("{level}", levelMessage);
            }
            message = message.replace("{level_unformatted}", Component.translatable(messageTemplates.level_unformatted(), level).getString());
        }
        message = message.replace("{level}", "");
        message = message.replace("{level_unformatted}", "");

        // IVs
        if (ivsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = ivsDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.ivs_hover() : messageTemplates.ivs();
            String ivsMessage = Services.PLATFORM.doesServerHaveMod() ?
                    Component.translatable(configMessage,
                            ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                            ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)).getString()
                    :
                    Component.translatable(configMessage,
                            "-", "-", "-", "-", "-", "-").getString();
            if (isHoverEnabled) {
                hoverBuilder.append(ivsMessage).append("\n");
            } else {
                message = message.replace("{ivs}", ivsMessage);
            }
            String ivsUnformatted = Services.PLATFORM.doesServerHaveMod() ?
                    Component.translatable(messageTemplates.ivs_unformatted(),
                            ivs.get(Stats.HP), ivs.get(Stats.ATTACK), ivs.get(Stats.DEFENCE),
                            ivs.get(Stats.SPECIAL_ATTACK), ivs.get(Stats.SPECIAL_DEFENCE), ivs.get(Stats.SPEED)).getString()
                    :
                    Component.translatable(messageTemplates.evs_unformatted(),
                            "-", "-", "-", "-", "-", "-").getString();
            message = message.replace("{ivs_unformatted}", ivsUnformatted);
        }
        message = message.replace("{ivs}", "");
        message = message.replace("{ivs_unformatted}", "");

        // EVs
        if (evsDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = evsDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.evs_hover() : messageTemplates.evs();
            String evsMessage = Component.translatable(configMessage,
                            evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                            evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED)).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(evsMessage).append("\n");
            } else {
                message = message.replace("{evs}", evsMessage);
            }
            String evsUnformatted = Component.translatable(messageTemplates.evs_unformatted(),
                            evYield.get(Stats.HP), evYield.get(Stats.ATTACK), evYield.get(Stats.DEFENCE),
                            evYield.get(Stats.SPECIAL_ATTACK), evYield.get(Stats.SPECIAL_DEFENCE), evYield.get(Stats.SPEED)).getString();
            message = message.replace("{evs_unformatted}", evsUnformatted);
        }
        message = message.replace("{evs}", "");
        message = message.replace("{evs_unformatted}", "");

        // Nature
        if (natureDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = natureDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.nature_hover() : messageTemplates.nature();
            String natureString = nature != null ? MiscUtilsKt.asTranslated(nature.getDisplayName()).getString() : "N/A";
            natureString = StringUtil.capitalize(natureString);
            natureString = replaceIfNotAvailable(natureString);
            String natureMessage = Component.translatable(configMessage, natureString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(natureMessage).append("\n");
            } else {
                message = message.replace("{nature}", natureMessage);
            }
            String natureUnformatted = replaceIfNotAvailable(Component.translatable(messageTemplates.nature_unformatted(), natureString).getString());
            message = message.replace("{nature_unformatted}", natureUnformatted);
        }
        message = message.replace("{nature}", "");
        message = message.replace("{nature_unformatted}", "");

        // Ability
        if (abilityDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = abilityDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.ability_hover() : messageTemplates.ability();
            String abilityString = ability != null ? StringUtil.capitalize(MiscUtilsKt.asTranslated(ability.getDisplayName()).getString()) : "N/A";
            abilityString = replaceIfNotAvailable(abilityString);
            String abilityMessage = Component.translatable(configMessage, abilityString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(abilityMessage).append("\n");
            } else {
                message = message.replace("{ability}", abilityMessage);
            }
            String abilityUnformatted = replaceIfNotAvailable(Component.translatable(messageTemplates.ability_unformatted(), abilityString).getString());
            message = message.replace("{ability_unformatted}", abilityUnformatted);
        }
        message = message.replace("{ability}", "");
        message = message.replace("{ability_unformatted}", "");

        // Hidden Ability
        boolean shouldAlertHA = config.alertHiddenAbility() &&
                HiddenAbilityUtil.hasHiddenAbility(alertData.spawnData().dexId(), alertData.traits().formID(), alertData.traits().abilityID());
        if (shouldAlertHA) {
            message = message.replace("{HA}", Component.translatable(messageTemplates.hidden_ability()).getString());
            message = message.replace("{HA_unformatted}", Component.translatable(messageTemplates.hidden_ability_unformatted()).getString());
        }
        message = message.replace("{HA}", "");
        message = message.replace("{HA_unformatted}", "");

        // Gender
        if (genderDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = genderDisplayMode == StatDisplayMode.HOVER;
            String genderSymbol = switch (gender) {
                case MALE -> messageTemplates.male();
                case FEMALE -> messageTemplates.female();
                case GENDERLESS -> messageTemplates.genderless();
            };
            String genderName = StringUtil.capitalize(gender.toString().toLowerCase());
            String genderString = Component.translatable(genderSymbol, genderName).getString();
            String configMessage = isHoverEnabled ? messageTemplates.gender_hover() : messageTemplates.gender();
            String genderMessage = Component.translatable(configMessage, genderString).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(genderMessage).append("\n");
            } else {
                message = message.replace("{gender}", genderMessage);
            }
            message = message.replace("{gender_unformatted}",
                    Component.translatable(messageTemplates.gender_unformatted(), genderName).getString());
        }
        message = message.replace("{gender}", "");
        message = message.replace("{gender_unformatted}", "");

        // Coordinates
        Vector3f coords = alertData.spawnData().position();
        if (coordinatesDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = coordinatesDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.coords_hover() : messageTemplates.coords();
            String coordsMessage = Component.translatable(configMessage, (int)coords.x, (int)coords.y, (int)coords.z).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(coordsMessage).append("\n");
            } else {
                message = message.replace("{coords}", coordsMessage);
            }
            message = message.replace("{coords_unformatted}",Component.translatable(messageTemplates.coords_unformatted(), (int)coords.x, (int)coords.y, (int)coords.z).getString());
        }
        message = message.replace("{coords}", "");
        message = message.replace("{coords_unformatted}", "");

        // Biome
        if (biomeDisplayMode != StatDisplayMode.DISABLED && Minecraft.getInstance().level != null) {
            boolean isHoverEnabled = biomeDisplayMode == StatDisplayMode.HOVER;
            String biomeName = Component.translatable(alertData.spawnData().biomeKey()).getString();
            biomeName = StringUtil.makeBeautiful(biomeName);

            String configMessage = isHoverEnabled ? messageTemplates.biome_hover() : messageTemplates.biome();
            String biomeMessage = Component.translatable(configMessage, biomeName).getString();
            if (isHoverEnabled) {
                hoverBuilder.append(biomeMessage).append("\n");
            } else {
                message = message.replace("{biome}", biomeMessage);
            }
            message = message.replace("{biome_unformatted}", Component.translatable(messageTemplates.biome_unformatted(), biomeName).getString());
        }
        message = message.replace("{biome}", "");
        message = message.replace("{biome_unformatted}", "");

        // Nearest Player
        if (nearestPlayerDisplayMode != StatDisplayMode.DISABLED) {
            boolean isHoverEnabled = nearestPlayerDisplayMode == StatDisplayMode.HOVER;
            String configMessage = isHoverEnabled ? messageTemplates.nearest_player_hover() : messageTemplates.nearest_player();
            String nearestPlayerMessage = Component.translatable(configMessage, nearestPlayer).getString();

            if (isHoverEnabled) {
                hoverBuilder.append(nearestPlayerMessage).append("\n");
            } else {
                message = message.replace("{nearest_player}", nearestPlayerMessage);
            }
            message = message.replace("{nearest_player_unformatted}", Component.translatable(messageTemplates.nearest_player_unformatted(), nearestPlayer).getString());
        }
        message = message.replace("{nearest_player}", "");
        message = message.replace("{nearest_player_unformatted}", "");

        return message;
    }

    private static String replaceIfNotAvailable(String string) {
        if (!Services.PLATFORM.doesServerHaveMod()) {
            return "N/A";
        }
        return string;
    }
}
