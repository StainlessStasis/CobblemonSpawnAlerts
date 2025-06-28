package io.github.stainlessstasis.mixin;

import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
import com.cobblemon.mod.common.api.spawning.*;
import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext;
import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner;
import com.cobblemon.mod.common.command.SpawnPokemon;
import com.mojang.brigadier.context.CommandContext;
import kotlin.Unit;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(value = SpawnPokemon.class, remap = true)
public class PokemonSpawnCommandMixin {
    @Unique
    private UUID playerUUID;
    @Unique
    private BlockPos blockPos;

    @Inject(method = "execute", at = @At("HEAD"))
    private void execute(CommandContext<CommandSourceStack> context, Vec3 pos, CallbackInfoReturnable<Integer> cir) {
        playerUUID = context.getSource().getPlayer().getUUID();
        blockPos = BlockPos.containing(pos);
    }

    @Redirect(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"
            ),
            remap = true
    )
    private boolean onAddFreshEntity(ServerLevel world, Entity entity) {
        if (playerUUID == null) {
            return false;
        }
        if (blockPos == null) {
            return false;
        }

        PlayerSpawner spawner = CobblemonWorldSpawnerManager.INSTANCE.getSpawnersForPlayers().get(playerUUID);
        SpawnCause cause = new SpawnCause(spawner, spawner.chooseBucket(), entity);
        WorldSlice slice = spawner.getProspector().prospect(spawner, spawner.getArea(cause));
        AreaSpawningContext spawningContext = new AreaSpawningContext(
                cause, world, blockPos, 15, 15, true,
                new ArrayList<>(), 0, slice.nearbyBlocks(blockPos, 1, 1), slice);

        AtomicBoolean idkWhatToCallThisButTheCommandDidntError = new AtomicBoolean(false);
        CobblemonEvents.ENTITY_SPAWN.postThen(new SpawnEvent<>(entity, spawningContext),
                cancelled -> {
                    idkWhatToCallThisButTheCommandDidntError.set(true);
                    return Unit.INSTANCE;
                },
                succeeded -> {
                    idkWhatToCallThisButTheCommandDidntError.set(true);
                    spawningContext.getWorld().addFreshEntity(entity);
                    return Unit.INSTANCE;
                });

        return idkWhatToCallThisButTheCommandDidntError.get();
    }
}
