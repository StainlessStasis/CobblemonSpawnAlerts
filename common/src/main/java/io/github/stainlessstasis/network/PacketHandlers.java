package io.github.stainlessstasis.network;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.config.ServerConfig;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;

import java.util.UUID;

public class PacketHandlers {
    
    /**
     * Server-side handler for glow effect requests
     */
    public static void handleGlowEffectPacket(GlowEffectPacket payload, ServerPlayer player) {
        UUID pokemonUUID = payload.pokemonUUID();
        int durationSeconds = payload.durationSeconds();
        
        // Only show debug output if enabled in config
        ServerConfig config = CobblemonSpawnAlerts.COMMON_CONFIG_MANAGER.getServerConfig();
        boolean debugEnabled = false; // Server doesn't have this config yet, but we'll leave the structure
        
        if (debugEnabled) {
            System.out.println("[CSA SERVER] Received glow effect packet from player: " + player.getName().getString());
            System.out.println("[CSA SERVER] Target Pokemon UUID: " + pokemonUUID);
            System.out.println("[CSA SERVER] Duration: " + durationSeconds + " seconds");
        }
        
        // Find the Pokemon entity in the server world
        ServerLevel level = player.serverLevel();
        if (debugEnabled) {
            System.out.println("[CSA SERVER] Searching in server level: " + level.dimension().location());
        }
        
        int totalEntities = 0;
        int pokemonEntities = 0;
        boolean foundTarget = false;
        
        for (Entity entity : level.getAllEntities()) {
            totalEntities++;
            if (entity instanceof PokemonEntity pokemonEntity) {
                pokemonEntities++;
                UUID entityUUID = pokemonEntity.getUUID();
                
                // Log the first few Pokemon UUIDs to compare with client
                if (debugEnabled && pokemonEntities <= 10) {
                    System.out.println("[CSA SERVER] Pokemon #" + pokemonEntities + ": " + 
                        pokemonEntity.getPokemon().getDisplayName().getString() + 
                        " Entity UUID: " + entityUUID);
                }
                
                if (entityUUID.equals(pokemonUUID)) {
                    foundTarget = true;
                    if (debugEnabled) {
                        System.out.println("[CSA SERVER] Found target Pokemon entity!");
                        System.out.println("[CSA SERVER] Pokemon name: " + pokemonEntity.getPokemon().getDisplayName().getString());
                        System.out.println("[CSA SERVER] Pokemon level: " + pokemonEntity.getPokemon().getLevel());
                        
                        // Check if Pokemon already has any effects
                        var existingEffects = pokemonEntity.getActiveEffects();
                        System.out.println("[CSA SERVER] Pokemon has " + existingEffects.size() + " existing effects");
                        for (var effect : existingEffects) {
                            System.out.println("[CSA SERVER] Existing effect: " + effect.getEffect().toString());
                        }
                    }
                    
                    // Apply the glowing effect server-side (this will sync to all clients)
                    MobEffectInstance glowEffect = new MobEffectInstance(
                        MobEffects.GLOWING, 
                        durationSeconds * 20,  // Convert seconds to ticks
                        0,      // Amplifier
                        false,  // Ambient
                        false,  // Visible particles
                        true    // Show icon
                    );
                    
                    boolean effectAdded = pokemonEntity.addEffect(glowEffect);
                    if (debugEnabled) {
                        System.out.println("[CSA SERVER] Effect added successfully: " + effectAdded);
                        
                        // Check if the effect was actually applied
                        boolean hasGlowingAfter = pokemonEntity.hasEffect(MobEffects.GLOWING);
                        System.out.println("[CSA SERVER] Pokemon has glowing effect after application: " + hasGlowingAfter);
                        
                        if (hasGlowingAfter) {
                            var glowEffectInstance = pokemonEntity.getEffect(MobEffects.GLOWING);
                            if (glowEffectInstance != null) {
                                System.out.println("[CSA SERVER] Glow effect duration: " + glowEffectInstance.getDuration() + " ticks");
                                System.out.println("[CSA SERVER] Glow effect amplifier: " + glowEffectInstance.getAmplifier());
                            }
                        }
                    }
                    
                    break;
                }
            }
        }
        
        if (debugEnabled) {
            System.out.println("[CSA SERVER] Search completed. Total entities: " + totalEntities + ", Pokemon entities: " + pokemonEntities);
            if (!foundTarget) {
                System.out.println("[CSA SERVER] ERROR: Target Pokemon with UUID " + pokemonUUID + " not found!");
            }
        }
    }
}
