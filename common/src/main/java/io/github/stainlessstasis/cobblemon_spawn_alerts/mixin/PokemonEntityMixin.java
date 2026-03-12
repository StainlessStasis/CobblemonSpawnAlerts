package io.github.stainlessstasis.cobblemon_spawn_alerts.mixin;

import com.cobblemon.mod.common.api.scheduling.ScheduledTask;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import io.github.stainlessstasis.cobblemon_spawn_alerts.alert.DespawnReason;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlerts;
import io.github.stainlessstasis.cobblemon_spawn_alerts.platform.Services;
import kotlin.Unit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PokemonEntity.class)
public class PokemonEntityMixin {
    @Inject(method = "remove", at = @At(value = "HEAD"))
    public void remove(Entity.RemovalReason reason, CallbackInfo ci) {
        PokemonEntity pokemonEntity = (PokemonEntity)(Object)this;

        if (pokemonEntity.level() instanceof ServerLevel level && CobblemonSpawnAlerts.globallyAlerted.contains(pokemonEntity.getPokemon().getUuid())) {
            new ScheduledTask.Builder().delay(3f).execute(task -> {
                if (CobblemonSpawnAlerts.despawned.contains(pokemonEntity.getPokemon().getUuid())) {
                    CobblemonSpawnAlerts.despawned.remove(pokemonEntity.getPokemon().getUuid());
                    return Unit.INSTANCE;
                }
                DespawnReason despawnReason = reason == Entity.RemovalReason.KILLED ? DespawnReason.DIED : DespawnReason.DESPAWNED;
                Services.PLATFORM.onPokemonDespawned(level, pokemonEntity.getPokemon(), "N/A", despawnReason);
                return Unit.INSTANCE;
            }).build();
        }
    }
}
