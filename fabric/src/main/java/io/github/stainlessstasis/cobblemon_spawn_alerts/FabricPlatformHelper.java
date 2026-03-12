package io.github.stainlessstasis.cobblemon_spawn_alerts;

import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.cobblemon_spawn_alerts.alert.DespawnReason;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.AlertDataPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.IPlatformHelper;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Platform;
import io.github.stainlessstasis.cobblemon_spawn_alerts.alert.AlertUtils;
import io.github.stainlessstasis.cobblemon_spawn_alerts.util.RarityUtil;
import kotlin.Unit;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.tysontheember.emberstextapi.immersivemessages.api.MarkupParser;
import net.tysontheember.emberstextapi.immersivemessages.api.TextSpan;
import net.tysontheember.emberstextapi.util.StyleUtil;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {

        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void onPokemonSpawned(PokemonEntity pokemonEntity, RarityUtil.Bucket bucket) {
        new ScheduledTask.Builder().delay(0.5f).execute(task -> {
            Set<UUID> alreadyAlerted = new HashSet<>();

            // Send EVERY PokemonEntity to clients that have the entity loaded for IV/EV hunting, etc.
            for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
                ServerPlayNetworking.send(player, CobblemonSpawnAlerts.createPokemonData(pokemonEntity, bucket));
                alreadyAlerted.add(player.getUUID());
            }

            // Only send RARE Pokemon (e.g. legendaries) to all clients, so we dont kill the network
            if (!AlertUtils.shouldGlobalAlert(pokemonEntity, bucket)) {
                return Unit.INSTANCE;
            } else {
                CobblemonSpawnAlerts.globallyAlerted.add(pokemonEntity.getPokemon().getUuid());
            }

            AlertDataPacket alertData = CobblemonSpawnAlerts.createAlertData(pokemonEntity, bucket);
            if (CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig().sendWebhook()) {
                CobblemonSpawnAlerts.getWebhookService().sendWebhook(alertData, null);
            }

            if (pokemonEntity.level() instanceof ServerLevel level) {
                for (ServerPlayer player : level.players()) {
                    if (alreadyAlerted.contains(player.getUUID())) {
                        continue;
                    }

                    ServerPlayNetworking.send(player, alertData);
                }
            }

            return Unit.INSTANCE;
        }).build();
    }

    @Override
    public void onPokemonDespawned(Level _level, Pokemon pokemon, String playerName, DespawnReason despawnReason) {
        IPlatformHelper.super.onPokemonDespawned(_level, pokemon, playerName, despawnReason);

        if (_level instanceof ServerLevel level) {
            for (ServerPlayer player : level.players()) {
                ServerPlayNetworking.send(player, CobblemonSpawnAlerts.createDespawnData(level, pokemon, playerName, despawnReason));
            }
        }
    }

    @Override
    public boolean doesServerHaveMod() {
        return CSAFabricClient.doesServerHaveMod;
    }

    @Override
    public MutableComponent parseMarkup(String markup) {
        List<TextSpan> spans = MarkupParser.parse(markup);
        MutableComponent result = Component.empty();
        for (TextSpan span : spans) {
            // applyTextSpanFormatting handles bold/italic/effects but intentionally skips color
            Style style = StyleUtil.applyTextSpanFormatting(Style.EMPTY, span);
            if (span.getColor() != null) {
                style = style.withColor(span.getColor());
            }
            result.append(Component.literal(span.getContent()).withStyle(style));
        }
        return result;
    }
}