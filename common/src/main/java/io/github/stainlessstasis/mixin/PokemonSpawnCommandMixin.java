//package io.github.stainlessstasis.mixin;
//
//import com.cobblemon.mod.common.api.events.CobblemonEvents;
//import com.cobblemon.mod.common.api.events.entity.SpawnEvent;
//import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
//import com.cobblemon.mod.common.api.spawning.CobblemonWorldSpawnerManager;
//import com.cobblemon.mod.common.api.spawning.SpawnCause;
//import com.cobblemon.mod.common.api.spawning.WorldSlice;
//import com.cobblemon.mod.common.api.spawning.context.AreaSpawningContext;
//import com.cobblemon.mod.common.api.spawning.spawner.PlayerSpawner;
//import com.cobblemon.mod.common.command.SpawnPokemon;
//import com.cobblemon.mod.common.command.argument.PokemonPropertiesArgumentType;
//import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
//import com.mojang.brigadier.Command;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
//import kotlin.Unit;
//import net.minecraft.ChatFormatting;
//import net.minecraft.commands.CommandSourceStack;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.entity.MobSpawnType;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.phys.Vec3;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Unique;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//
//import java.util.ArrayList;
//import java.util.concurrent.atomic.AtomicBoolean;
//
//import static com.cobblemon.mod.common.util.LocalizationUtilsKt.commandLang;
//
//@Mixin(value = SpawnPokemon.class)
//public abstract class PokemonSpawnCommandMixin {
//    @Unique
//    private final SimpleCommandExceptionType NO_SPECIES_EXCEPTION = new SimpleCommandExceptionType(commandLang("${NAME}.nospecies").withStyle(ChatFormatting.RED));
//    @Unique
//    private final SimpleCommandExceptionType INVALID_POS_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Invalid position").withStyle(ChatFormatting.RED));
//    @Unique
//    private final SimpleCommandExceptionType FAILED_SPAWN_EXCEPTION = new SimpleCommandExceptionType(Component.literal("Unable to spawn at the given position").withStyle(ChatFormatting.RED));
//
//    // this is to make pokemon spawned via command be counted as a "natural" spawn or some shit so that the mod works with commands for testing
//    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
//    private void execute(CommandContext<CommandSourceStack> context, Vec3 pos, CallbackInfoReturnable<Integer> cir) throws CommandSyntaxException {
//        if (!(context.getSource().getPlayer() instanceof ServerPlayer player)) {
//            return;
//        }
//
//        ServerLevel world = player.serverLevel();
//        BlockPos blockPos = BlockPos.containing(pos);
//        if (!Level.isInSpawnableBounds(blockPos)) {
//            throw INVALID_POS_EXCEPTION.create();
//        }
//        PokemonProperties properties = PokemonPropertiesArgumentType.Companion.getPokemonProperties(context, "properties");
//        if (properties.getSpecies() == null) {
//            throw NO_SPECIES_EXCEPTION.create();
//        }
//        PokemonEntity pokemonEntity = properties.createEntity(world);
//        pokemonEntity.moveTo(pos.x, pos.y, pos.z, pokemonEntity.getYRot(), pokemonEntity.getXRot());
//        pokemonEntity.getEntityData().set(PokemonEntity.getSPAWN_DIRECTION(), pokemonEntity.getRandom().nextFloat() * 360F);
//        pokemonEntity.finalizeSpawn(world, world.getCurrentDifficultyAt(blockPos), MobSpawnType.COMMAND, null);
//
//        PlayerSpawner spawner = CobblemonWorldSpawnerManager.INSTANCE.getSpawnersForPlayers().get(player.getUUID());
//        SpawnCause cause = new SpawnCause(spawner, spawner.chooseBucket(), pokemonEntity);
//        WorldSlice slice = spawner.getProspector().prospect(spawner, spawner.getArea(cause));
//        AreaSpawningContext spawningContext = new AreaSpawningContext(
//                cause, world, blockPos, 15, 15, true,
//                new ArrayList<>(), 0, slice.nearbyBlocks(blockPos, 1, 1), slice);
//
//        AtomicBoolean idkWhatToCallThisButTheCommandDidntError = new AtomicBoolean(false);
//        CobblemonEvents.ENTITY_SPAWN.postThen(new SpawnEvent<>(pokemonEntity, spawningContext),
//                cancelled -> {
//                    idkWhatToCallThisButTheCommandDidntError.set(true);
//                    return Unit.INSTANCE;
//                },
//                succeeded -> {
//                    idkWhatToCallThisButTheCommandDidntError.set(true);
//                    spawningContext.getWorld().addFreshEntity(pokemonEntity);
//                    return Unit.INSTANCE;
//                });
//
//        if (idkWhatToCallThisButTheCommandDidntError.get()) {
//            cir.setReturnValue(Command.SINGLE_SUCCESS);
//            cir.cancel();
//            return;
//        }
//
//        cir.cancel();
//        throw FAILED_SPAWN_EXCEPTION.create();
//    }
//}
