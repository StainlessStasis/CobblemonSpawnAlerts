package io.github.stainlessstasis.network;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import net.minecraft.resources.ResourceLocation;

public class ModPackets {
    public static final ResourceLocation POKEMON_DATA = ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "pokemon-data-packet");
    public static final ResourceLocation ALERT_DATA = ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "alert-data-packet");
    public static final ResourceLocation DESPAWN_DATA = ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "despawn-data-packet");
    public static final ResourceLocation MOD_LOADED = ResourceLocation.fromNamespaceAndPath(CobblemonSpawnAlerts.MOD_ID, "mod-loaded-packet");
}
