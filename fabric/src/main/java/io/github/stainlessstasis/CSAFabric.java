package io.github.stainlessstasis;

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
		CobblemonSpawnAlerts.initServer();

		CommandRegistrationCallback.EVENT.register(CommandRegistry::registerServerCommands);
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