package io.github.stainlessstasis.alert;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.abilities.Abilities;
import com.cobblemon.mod.common.api.abilities.AbilityTemplate;
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress;
import com.cobblemon.mod.common.api.pokedex.SpeciesDexRecord;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonSpecies;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.api.storage.player.client.ClientPokedexManager;
import com.cobblemon.mod.common.client.CobblemonClient;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.*;
import com.cobblemon.mod.common.util.MiscUtilsKt;
import com.mojang.datafixers.util.Pair;
import io.github.stainlessstasis.compat.JourneymapCompat;
import io.github.stainlessstasis.config.client.MainConfig;
import io.github.stainlessstasis.config.client.MessageTemplates;
import io.github.stainlessstasis.config.client.PokemonConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CobblemonSpawnAlertsClient;
import io.github.stainlessstasis.network.*;
import io.github.stainlessstasis.network.PokemonStats;
import io.github.stainlessstasis.platform.Services;
import io.github.stainlessstasis.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AlertHandler {
    private static final HashSet<UUID> alreadyAlerted = new HashSet<>();

    public static void clearCache() {
        alreadyAlerted.clear();
    }

    public static void alertClientside(PokemonEntity pokemonEntity) {
        if (!CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig().enableAlerts()) return;
        EVs defaultEVYield = EvsUtil.getYield(pokemonEntity.getPokemon().getSpecies().getNationalPokedexNumber());
        alertClientside(pokemonEntity, defaultEVYield, RarityUtil.Bucket.COMMON);
    }

    public static void alertClientside(PokemonEntity pokemonEntity, EVs evYield, RarityUtil.Bucket bucket) {
        if (pokemonEntity.getOwnerUUID() != null) return;
        if (!CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig().enableAlerts()) return;

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
                        DimensionUtil.getDimensionKey(pokemonEntity),
                        bucket
                ),
                new PokemonStats(
                        pokemon.getLevel(),
                        pokemon.getIvs(),
                        evYield
                ),
                new PokemonRarityData(
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
        if (!(Minecraft.getInstance().player instanceof Player player)) return;
        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.isReloading()) return;
        if (!CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig().enableAlerts()) return;
        if (alreadyAlerted.contains(alertData.spawnData().pokemonUUID())) return;

        MainConfig mainConfig = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
        ClientPokedexManager dex = CobblemonClient.INSTANCE.getClientPokedexData();
        Vector3f spawnPos = alertData.spawnData().position();

        String pokemonName = PokemonNameUtil.getTranslatedName(alertData.spawnData().translatedPokemonName());
        Pair<Boolean, PokemonConfig.PokemonSpecificConfig> result = AlertUtils.getConfigForPokemon(pokemonName, alertData.spawnData().dexId());
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
        boolean shouldAlertBucket = mainConfig.bucketsToAlert().contains(alertData.spawnData().bucket());

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
        MainConfig.IVHunting ivHunting = mainConfig.ivHunting();
        MainConfig.EVHunting evHunting = mainConfig.evHunting();

        boolean shouldAlertIVs = false;
        if (ivHunting.enabled()) {
            IVs ivs = alertData.stats().ivs();
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
            EVs evs = alertData.stats().evYield();
            shouldAlertEVs =
                (evHunting.minHp() > 0 && evs.get(Stats.HP) >= evHunting.minHp())
                || (evHunting.minAtk() > 0 && evs.get(Stats.ATTACK) >= evHunting.minAtk())
                || (evHunting.minDef() > 0 && evs.get(Stats.DEFENCE) >= evHunting.minDef())
                || (evHunting.minSpAtk() > 0 && evs.get(Stats.SPECIAL_ATTACK) >= evHunting.minSpAtk())
                || (evHunting.minSpDef() > 0 && evs.get(Stats.SPECIAL_DEFENCE) >= evHunting.minSpDef())
                || (evHunting.minSpeed() > 0 && evs.get(Stats.SPEED) >= evHunting.minSpeed());
        }

        // Check level filter
        MainConfig.LevelFilter levelFilter = mainConfig.levelFilter();
        boolean passesLevelFilter = true;
        if (levelFilter.enabled()) {
            int level = alertData.stats().level();
            if (level < levelFilter.minLevel() || level > levelFilter.maxLevel()) {
                passesLevelFilter = false;
            }
        }

        // Check distance filter
        MainConfig.DistanceFilter distanceFilter = mainConfig.distanceFilter();
        boolean passesDistanceFilter = true;
        if (distanceFilter.enabled()) {
            double distance = Math.sqrt(player.distanceToSqr(new Vec3(spawnPos)));
            if (distance < distanceFilter.minDistance() || distance > distanceFilter.maxDistance()) {
                passesDistanceFilter = false;
            }
        }

        // Finalize alert check
        boolean shouldAlertInConfig = pokemonConfig.alwaysAlert() || shouldAlertShiny || shouldAlertHA;
        boolean shouldAlertNotInConfig =
                (passesLevelFilter && passesDistanceFilter) &&
                        (
                            shouldAlertShiny
                            || shouldAlertLegend
                            || shouldAlertMythical
                            || shouldAlertUltra
                            || shouldAlertParadox
                            || shouldAlertStarter
                            || shouldAlertBucket
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
            if (shouldAlertBucket) alertCondition = DebugAlertCondition.ALERT_BUCKETS;
            if (shouldAlertIVs) alertCondition = DebugAlertCondition.IV_HUNTING;
            if (shouldAlertEVs) alertCondition = DebugAlertCondition.EV_HUNTING;
            if (shouldAlertUncaught) alertCondition = DebugAlertCondition.ALERT_ALL_UNCAUGHT;
            if (shouldAlertNotInDex) alertCondition = DebugAlertCondition.ALERT_ALL_NOT_IN_DEX;

            String message = MessageUtils.getTranslated("cobblemon-spawn-alerts.debug_alert_condition", alertCondition.name());
            StringBuilder debugHoverBuilder = new StringBuilder();
            message = AlertUtils.applyDynamicReplacements(message, pokemonConfig, alertData, debugHoverBuilder);
            Component messageComponent = MessageUtils.parseMarkup(message);
            messageComponent = AlertUtils.applyMessageInteractions(messageComponent, debugHoverBuilder.toString(), pokemonConfig, alertData);
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

        // sounds
        if (mainConfig.enableSounds()) {
            // play custom alert sound if one exists
            if (!(Objects.equals(pokemonConfig.customAlertSound(), ""))) {
                String[] split = StringUtil.splitIdentifier(pokemonConfig.customAlertSound());
                if (!split[0].equals("NO NAMESPACE")) {
                    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
                    SoundEvent sound = SoundEvent.createFixedRangeEvent(resourceLocation, -1f);
                    player.playNotifySound(sound, SoundSource.MASTER, 1f, 1f);
                } else {
                    player.sendSystemMessage(MessageUtils.parseMarkup(MessageUtils.getTranslated("cobblemon-spawn-alerts.outdated_sound")));
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
                traits.put("bucket", shouldAlertBucket);
                traits.put("unregistered", !isInDex);
                traits.put("uncaught", !isCaught);
                // TODO: change this if i ever add individual iv/ev hunting
                traits.put("ivs", shouldAlertIVs);
                traits.put("evs", shouldAlertEVs);
                traits.put("despawned", false);

                for (String soundTrait : pokemonConfig.sounds().keySet()) {
                    String soundID = pokemonConfig.sounds().get(soundTrait);
                    if (traits.get(soundTrait) && !soundID.isEmpty()) {
                        String[] split = StringUtil.splitIdentifier(soundID);
                        if (!split[0].equals("NO NAMESPACE")) {
                            ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
                            SoundEvent sound = SoundEvent.createFixedRangeEvent(resourceLocation, -1f);
                            player.playNotifySound(sound, SoundSource.MASTER, 1f, 1f);
                        } else {
                            player.sendSystemMessage(MessageUtils.parseMarkup(MessageUtils.getTranslated("cobblemon-spawn-alerts.outdated_sound")));
                        }
                    }
                }
            }
        }

        // Autoglow
        if (pokemonConfig.autoGlow()) {
            int color = GlowUtil.getGlowColor(pokemonConfig.glowColor());
            CobblemonSpawnAlertsClient.glowing.put(alertData.spawnData().pokemonUUID(), color);
        }

        // send the custom alert if one exits
        String message;
        StringBuilder hoverBuilder = new StringBuilder();
        if (!Objects.equals(pokemonConfig.customAlertMessage(), "")) {
            message = AlertUtils.applyDynamicReplacements(pokemonConfig.customAlertMessage(), pokemonConfig, alertData, hoverBuilder);
        } else {
            // use the default message if no custom one is provided
            message = MessageUtils.getTranslated(CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates().fullSpawnMessage());
            message = AlertUtils.applyDynamicReplacements(message, pokemonConfig, alertData, hoverBuilder);
        }
        Component spawnComponent = MessageUtils.parseMarkup(message);
        spawnComponent = AlertUtils.applyMessageInteractions(spawnComponent, hoverBuilder.toString(), pokemonConfig, alertData);
        player.sendSystemMessage(spawnComponent);

        // journeymap compat
        PokemonConfig.JourneymapConfig jmConfig = pokemonConfig.journeyMap();
        if (Services.PLATFORM.isModLoaded("journeymap") && jmConfig.enableWaypoint()) {
            BlockPos blockPos = new BlockPos((int)spawnPos.x, (int)spawnPos.y, (int)spawnPos.z);
            JourneymapCompat.createWaypoint(blockPos, alertData, pokemonConfig, jmConfig);
        }
    }

    public static void alertDespawned(DespawnDataPacket despawnData) {
        if (!(Minecraft.getInstance().player instanceof Player player)) return;
        if (CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.isReloading()) return;
        if (!CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig().enableDespawnAlerts()) return;

        final AlertDataPacket alertData = despawnData.alertData();
        final PokemonSpawnData spawnData = alertData.spawnData();

        if (!alreadyAlerted.contains(alertData.spawnData().pokemonUUID())) return;

        CobblemonSpawnAlertsClient.glowing.remove(spawnData.pokemonUUID());
        if (Services.PLATFORM.isModLoaded("journeymap")) {
            JourneymapCompat.removeWaypoint(spawnData.pokemonUUID());
        }

        PokemonConfig.PokemonSpecificConfig pokemonConfig = AlertUtils.getConfigForPokemon(spawnData.translatedPokemonName(), spawnData.dexId()).getSecond();
        if (!pokemonConfig.alertDespawned()) {
            return;
        }

        MessageTemplates messageTemplates = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates();
        MainConfig mainConfig = CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMainConfig();
        String message = MessageUtils.getTranslated(CobblemonSpawnAlertsClient.CLIENT_CONFIG_MANAGER.getMessageTemplates().despawnMessage());

        message = switch (DespawnReason.valueOf(despawnData.despawnReason())) {
            case CAPTURED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Captured(), spawnData.nearestPlayerName()).getString());
            case DESPAWNED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Despawned()).getString());
            case FAINTED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Fainted(), spawnData.nearestPlayerName()).getString());
            case DIED -> message.replace("{despawned}", Component.translatable(messageTemplates.despawnReason_Died()).getString());
        };

        StringBuilder despawnHoverBuilder = new StringBuilder();
        message = AlertUtils.applyDynamicReplacements(message, pokemonConfig, alertData, despawnHoverBuilder);
        Component despawnComponent = MessageUtils.parseMarkup(message);
        despawnComponent = AlertUtils.applyMessageInteractions(despawnComponent, despawnHoverBuilder.toString(), pokemonConfig, alertData);
        player.sendSystemMessage(despawnComponent);

        // play despawn sound if one exists
        String despawnSound = pokemonConfig.sounds().get("despawned");
        if (mainConfig.enableSounds() && !(Objects.equals(despawnSound, ""))) {
            String[] split = StringUtil.splitIdentifier(despawnSound);
            if (!split[0].equals("NO NAMESPACE")) {
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
                SoundEvent sound = SoundEvent.createFixedRangeEvent(resourceLocation, -1f);
                player.playNotifySound(sound, SoundSource.MASTER, 1f, 1f);
            } else {
                player.sendSystemMessage(MessageUtils.parseMarkup(MessageUtils.getTranslated("cobblemon-spawn-alerts.outdated_sound")));
            }
        }
    }


}
