package io.github.stainlessstasis.cobblemon_spawn_alerts;

import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CommandRegistry;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.AlertDataPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.DespawnDataPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.ModLoadedPacket;
import io.github.stainlessstasis.cobblemon_spawn_alerts.network.PokemonDataPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class CSAFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CobblemonSpawnAlerts.initServer();

		CommandRegistrationCallback.EVENT.register(CommandRegistry::registerCommonCommands);
		ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(AlertDataPacket.ID, AlertDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(DespawnDataPacket.ID, DespawnDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ModLoadedPacket.ID, ModLoadedPacket.STREAM_CODEC);
	}

	private void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer minecraftServer) {
		packetSender.sendPacket(new ModLoadedPacket(true));
	}
}