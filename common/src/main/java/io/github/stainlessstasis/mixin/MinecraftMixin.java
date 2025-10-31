package io.github.stainlessstasis.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import io.github.stainlessstasis.core.CobblemonSpawnAlerts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Final public Options options;

    @Inject(method = "shouldEntityAppearGlowing", at = @At("HEAD"), cancellable = true)
    public void shouldEntityAppearGlowing(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        boolean glow =
                // Vanilla
                entity.isCurrentlyGlowing()
                || (this.player != null && this.player.isSpectator() && this.options.keySpectatorOutlines.isDown() && entity.getType() == EntityType.PLAYER)
                // CSA
                || (entity instanceof PokemonEntity pe && CobblemonSpawnAlerts.glowing.contains(pe.getPokemon().getUuid()));
        cir.setReturnValue(glow);
    }
}
