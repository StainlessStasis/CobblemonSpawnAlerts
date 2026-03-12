package io.github.stainlessstasis.cobblemon_spawn_alerts.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class DimensionUtil {
    public static String getDimensionKey(Level level) {
        return level.dimension().location().toLanguageKey().replaceFirst("\\.", ":");
    }

    public static String getDimensionKey(Entity entity) {
        return getDimensionKey(entity.level());
    }

    public static ResourceKey<Level> getDimension(String key) {
        String[] split = StringUtil.splitIdentifier(key);
        ResourceLocation rl = ResourceLocation.fromNamespaceAndPath(split[0], split[1]);
        return ResourceKey.create(Registries.DIMENSION, rl);
    }
}
