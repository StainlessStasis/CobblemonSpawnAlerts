package io.github.stainlessstasis.util;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.network.GlowEffectPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;

/**
 * Manages the glow effect for spawned Pokémon that can be highlighted by clicking chat messages
 */
public class GlowEffectManager {
    private static final Map<UUID, Long> glowingPokemon = new ConcurrentHashMap<>();
    private static final int GLOW_DURATION_SECONDS = 30;
    
    /**
     * Registers a Pokémon as available for glowing when spawned
     */
    public static void registerSpawnedPokemon(UUID pokemonUUID) {
        // Don't add glow effect yet, just track that this Pokémon exists
        // The glow will be applied when the player clicks the chat message
    }
    
    /**
     * Applies the glow effect to a Pokémon by sending a packet to the server
     */
    public static boolean applyGlowEffect(UUID pokemonUUID) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return false;
        }
        
        // Check debug setting from config
        boolean debugEnabled = false;
        try {
            debugEnabled = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput();
        } catch (Exception e) {
            // Config might not be loaded yet, default to false
        }
        
        if (debugEnabled) {
            System.out.println("[CSA CLIENT] Sending glow effect packet for UUID: " + pokemonUUID);
        }
        
        // Check if the Pokemon exists in the world first
        boolean pokemonFound = false;
        for (Entity entity : level.entitiesForRendering()) {
            if (entity instanceof PokemonEntity pokemonEntity && 
                pokemonEntity.getUUID().equals(pokemonUUID)) {
                pokemonFound = true;
                if (debugEnabled) {
                    System.out.println("[CSA CLIENT] Found Pokemon in client world: " + pokemonEntity.getPokemon().getDisplayName().getString());
                    System.out.println("[CSA CLIENT] Pokemon entity UUID: " + pokemonEntity.getUUID());
                    System.out.println("[CSA CLIENT] Pokemon entity ID: " + pokemonEntity.getId());
                }
                break;
            }
        }
        
        if (!pokemonFound) {
            if (debugEnabled) {
                System.out.println("[CSA CLIENT] Pokemon not found in client world entities");
            }
            return false;
        }
        
        // Send packet to server to apply the glow effect
        try {
            GlowEffectPacket packet = new GlowEffectPacket(pokemonUUID, GLOW_DURATION_SECONDS);
            // The packet sending will be handled in the Fabric client where we have access to networking
            sendGlowPacket(packet);
            
            // Track locally for UI purposes (even though the actual effect is server-side)
            glowingPokemon.put(pokemonUUID, System.currentTimeMillis() + (GLOW_DURATION_SECONDS * 1000L));
            
            if (debugEnabled) {
                System.out.println("[CSA CLIENT] Glow effect packet sent successfully");
            }
            return true;
        } catch (Exception e) {
            if (debugEnabled) {
                System.out.println("[CSA CLIENT] Failed to send glow packet: " + e.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * This method will be implemented in the platform-specific code to actually send the packet
     */
    private static void sendGlowPacket(GlowEffectPacket packet) {
        // Use platform services to send the packet instead of reflection
        try {
            io.github.stainlessstasis.platform.Services.PLATFORM.sendGlowEffectPacket(packet);
        } catch (Exception e) {
            // Check debug setting from config
            boolean debugEnabled = false;
            try {
                debugEnabled = CobblemonSpawnAlerts.CLIENT_CONFIG_MANAGER.getMainConfig().enableDebugOutput();
            } catch (Exception configE) {
                // Config might not be loaded yet, default to false
            }
            
            if (debugEnabled) {
                System.out.println("[CSA] Failed to send glow packet via platform service: " + e.getMessage());
            }
            throw new UnsupportedOperationException("Packet sending failed", e);
        }
    }
    
    /**
     * Updates glow effects - just manages our local tracking now since server handles the actual effects
     * Should be called periodically (e.g., every tick)
     */
    public static void updateGlowEffects() {
        if (glowingPokemon.isEmpty()) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        glowingPokemon.entrySet().removeIf(entry -> {
            UUID pokemonUUID = entry.getKey();
            long expirationTime = entry.getValue();
            
            if (currentTime >= expirationTime) {
                // Just remove from our tracking - server will handle effect expiration
                System.out.println("[CSA] Local glow tracking expired for Pokemon: " + pokemonUUID);
                return true; // Remove from map
            }
            return false; // Keep in map
        });
    }
    
    /**
     * Clears all glow effects (useful when changing worlds or disconnecting)
     */
    public static void clearAllGlowEffects() {
        // Just clear our local tracking - server effects will expire naturally
        glowingPokemon.clear();
        System.out.println("[CSA] Cleared all local glow tracking");
    }    /**
     * Checks if a Pokémon is currently glowing
     */
    public static boolean isGlowing(UUID pokemonUUID) {
        return glowingPokemon.containsKey(pokemonUUID);
    }
}
