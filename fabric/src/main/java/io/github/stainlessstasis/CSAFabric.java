package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.cobblemon.mod.common.pokemon.stat.CobblemonStatProvider;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.util.ComponentUtil;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public class CSAFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CobblemonSpawnAlerts.LOGGER.info("CobblemonSpawnAlerts server initializing");
		CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.loadConfig();

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
								if (CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.loadConfig()) {
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
				ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
				IVs ivs = config.broadcastIVs() ? pokemon.getIvs() : CobblemonStatProvider.INSTANCE.createEmptyIVs(0);
				Nature nature = config.broadcastNature() ? pokemon.getNature() : Natures.INSTANCE.getNAUGHTY();
				ServerPlayNetworking.send(player, new PokemonDataPacket(pokemonEntity.getId(), ivs, nature));
			}
            return Unit.INSTANCE;
        }).build();

	}
}