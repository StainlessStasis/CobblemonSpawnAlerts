package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.network.PokemonDataPacket;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CobblemonSpawnAlerts implements ModInitializer {
	public static final String MOD_ID = "cobblemon-spawn-alerts";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitialize() {
		// TODO: THIS DOESNT WORK WITH COMMANDS???
		CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, event -> {
			System.out.println("SHOULD HANDLE SPAWN EVENT");
			onPokemonSpawn(event.getEntity());
			return Unit.INSTANCE;
		});

		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
	}

	private void onPokemonSpawn(PokemonEntity pokemonEntity) {
		Pokemon pokemon = pokemonEntity.getPokemon();

		System.out.println("POKEMON SPAWNED: "+pokemon.getDisplayName());
		ScheduledTask _task = new ScheduledTask.Builder().delay(0.05f).execute(task -> {
			for (ServerPlayer player : PlayerLookup.tracking((pokemonEntity))) {
				System.out.println("SENDING PACKET TO PLAYER: "+player.getName());
				ServerPlayNetworking.send(player, new PokemonDataPacket(pokemonEntity.getUUID(), pokemon.getIvs(), pokemon.getNature()));
			}
            return Unit.INSTANCE;
        }).build();

	}
}