package io.github.stainlessstasis;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.core.CommandRegistry;
import io.github.stainlessstasis.network.ModLoadedPacket;
import io.github.stainlessstasis.network.PokemonDataPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class CSAFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		CobblemonSpawnAlerts.initCommon();

		CommandRegistrationCallback.EVENT.register(CommandRegistry::registerServerCommands);
		ServerPlayConnectionEvents.JOIN.register(this::onPlayerJoin);

		PayloadTypeRegistry.playS2C().register(PokemonDataPacket.ID, PokemonDataPacket.STREAM_CODEC);
		PayloadTypeRegistry.playS2C().register(ModLoadedPacket.ID, ModLoadedPacket.STREAM_CODEC);
	}

	private void onPlayerJoin(ServerGamePacketListenerImpl serverGamePacketListener, PacketSender packetSender, MinecraftServer minecraftServer) {
		packetSender.sendPacket(new ModLoadedPacket(true));
	}
}