package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

public class BiomeUtil {
    public static String getBiomeKey(Level level, Vec3 position) {
        BlockPos blockPos = BlockPos.containing(position);
        Holder<Biome> biomeHolder = level.getBiome(blockPos);
        if (biomeHolder.unwrapKey().isEmpty()) {
            return "N/A";
        }
        return "biome."+biomeHolder.unwrapKey().get().location().toLanguageKey();
    }
}
