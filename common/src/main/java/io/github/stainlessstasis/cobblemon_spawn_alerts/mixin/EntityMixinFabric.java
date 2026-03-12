package io.github.stainlessstasis.cobblemon_spawn_alerts.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlertsClient;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = Entity.class)
public abstract class EntityMixinFabric {
    @ModifyReturnValue(method = "method_5851()Z", at = @At("RETURN"))
    public boolean isCurrentlyGlowing(boolean original) {
        if (original) {
            return true;
        }

        Entity entity = (Entity)(Object)this;
        if (!entity.level().isClientSide) {
            return false;
        }

        return entity instanceof PokemonEntity pe && CobblemonSpawnAlertsClient.glowing.containsKey(pe.getPokemon().getUuid());
    }
}
