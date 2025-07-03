package io.github.stainlessstasis;

import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.alert.DespawnReason;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.AlertDataPacket;
import io.github.stainlessstasis.network.DespawnDataPacket;
import io.github.stainlessstasis.network.ModLoadedPacket;
import io.github.stainlessstasis.network.PokemonDataPacket;
import io.github.stainlessstasis.platform.Services;
import kotlin.Unit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;

public class CSAFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CobblemonSpawnAlerts.initCommon();

		CommandRegistrationCallback.EVENT.register(CommandRegistry::registerServerCommands);
		ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);
		ServerEntityEvents.ENTITY_UNLOAD.register(this::onEntityUnload);

		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(AlertDataPacket.ID, AlertDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(DespawnDataPacket.ID, DespawnDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ModLoadedPacket.ID, ModLoadedPacket.STREAM_CODEC);
	}

	private void onEntityUnload(Entity entity, ServerLevel serverLevel) {
		if (entity instanceof PokemonEntity pokemonEntity && pokemonEntity.getOwnerUUID() == null
				&& CobblemonSpawnAlerts.globallyAlerted.contains(pokemonEntity.getPokemon().getUuid())) {
			new ScheduledTask.Builder().delay(5f).execute(task -> {
				if (CobblemonSpawnAlerts.despawned.contains(pokemonEntity.getPokemon().getUuid())) {
					CobblemonSpawnAlerts.despawned.remove(pokemonEntity.getPokemon().getUuid());
					return Unit.INSTANCE;
				}
				Services.PLATFORM.onPokemonDespawned(serverLevel, pokemonEntity.getPokemon(), "N/A", DespawnReason.DESPAWNED);
				return Unit.INSTANCE;
			}).build();
		}
	}

	private void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer minecraftServer) {
		packetSender.sendPacket(new ModLoadedPacket(true));
	}
}