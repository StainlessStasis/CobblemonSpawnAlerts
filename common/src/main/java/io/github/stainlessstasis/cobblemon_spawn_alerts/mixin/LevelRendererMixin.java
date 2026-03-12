package io.github.stainlessstasis.cobblemon_spawn_alerts.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.stainlessstasis.cobblemon_spawn_alerts.core.CobblemonSpawnAlertsClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @ModifyExpressionValue(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getTeamColor()I"))
    public int getColor(int original, @Local Entity entity) {
        if (!(entity instanceof PokemonEntity pe)) return original;
        return CobblemonSpawnAlertsClient.glowing.getOrDefault(pe.getPokemon().getUuid(), original);
    }
}
