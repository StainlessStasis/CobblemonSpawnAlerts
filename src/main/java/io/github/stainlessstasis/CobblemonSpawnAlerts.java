package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.github.stainlessstasis.config.ClientConfigManager;
import io.github.stainlessstasis.config.CommonConfigManager;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.util.MessageUtils;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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
				dispatcher.register(Commands.literal("cobblemonspawnalerts")
                	.then(Commands.literal("reload-server")
							.executes(context -> {
								if (!context.getSource().hasPermission(3)) {
									context.getSource().sendFailure(MessageUtils.getMiniMessageTranslated("cobblemon-spawn-alerts.no_permission"));
									return 0;
								}

								configManager.reload();
								return 1;
        }))));

		// Packets
		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
	}

	private void onPokemonSpawn(PokemonEntity pokemonEntity) {
		Pokemon pokemon = pokemonEntity.getPokemon();

		System.out.println("POKEMON SPAWNED: "+pokemon.getDisplayName());
		ScheduledTask _task = new ScheduledTask.Builder().delay(0.05f).execute(task -> {
			for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
				System.out.println("SENDING PACKET TO PLAYER: "+player.getName());

				ServerPlayNetworking.send(player, new PokemonDataPacket(pokemonEntity.getId(), pokemon.getIvs(), pokemon.getNature()));
			}
            return Unit.INSTANCE;
        }).build();

	}
}