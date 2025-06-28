package io.github.stainlessstasis;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.config.CommonConfigManager;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.util.ComponentUtil;
import io.github.stainlessstasis.util.MessageUtils;
import kotlin.Unit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;
import static com.mojang.brigadier.builder.RequiredArgumentBuilder.argument;

public class CobblemonSpawnAlerts implements ModInitializer {
	public static final String MOD_ID = "cobblemon-spawn-alerts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CommonConfigManager configManager = new CommonConfigManager();

	@Override
	public void onInitialize() {
		CobblemonSpawnAlerts.LOGGER.info("CobblemonSpawnAlerts server initializing");
		configManager.loadConfig();

		CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {
			onPokemonSpawn(event.getEntity());
			return Unit.INSTANCE;
		});

		// Commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
				dispatcher.register(Commands.literal("cobblemonspawnalerts-server")
                	.then(Commands.literal("reload")
							.executes(context -> {
								if (!context.getSource().hasPermission(3)) {
									if (context.getSource().getPlayer() != null) {
										context.getSource().sendFailure(
												ComponentUtil.convertFromAdventure("<red>You do not have permission to use this command!</red>"));
									}

									return 0;
								}

								context.getSource().sendSystemMessage(
										ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><white>Server config reloading...</white>"));
								if (configManager.loadConfig()) {
									context.getSource().sendSystemMessage(
											ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><white>Server config reloaded!</white>"));
								} else {
									context.getSource().sendSystemMessage(
											ComponentUtil.convertFromAdventure("<green>[CobblemonSpawnAlerts] </green><red>Server config reload failed.</red>"));
								}
								return 1;
        }))));

		// Packets
		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
	}

	private void onPokemonSpawn(PokemonEntity pokemonEntity) {
		Pokemon pokemon = pokemonEntity.getPokemon();

		ScheduledTask _task = new ScheduledTask.Builder().delay(0.05f).execute(task -> {
			for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
				ServerConfig config = configManager.getServerConfig();
				IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
				Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.INSTANCE.getNAUGHTY();
				ServerPlayNetworking.send(player, new PokemonDataPacket(pokemonEntity.getId(), ivs, nature));
			}
            return Unit.INSTANCE;
        }).build();

	}
}