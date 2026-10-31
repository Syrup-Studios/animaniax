package net.syrupstudios.animaniax.worldgen;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.syrupstudios.animaniax.block.BeeHiveContent;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class BeeHiveFeature extends Feature<NoneFeatureConfiguration> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("animaniax", "wild_hive");
    public static final BeeHiveFeature INSTANCE = new BeeHiveFeature();

    private BeeHiveFeature() { super(Codec.unit(NoneFeatureConfiguration.INSTANCE)); }

    @Override public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!AnimalCareConfig.hiveSpawning || AnimalCareConfig.hiveSpawningFrequency <= 0
                || context.random().nextInt(200) >= AnimalCareConfig.hiveSpawningFrequency) return false;
        BlockPos origin = context.origin();
        var level = context.level();
        int minX = origin.getX() & -16;
        int minZ = origin.getZ() & -16;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        var hives = new ArrayList<BlockPos>();
        Direction[] directions = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        for (int dx = -8; dx <= 8; dx++) for (int dy = -2; dy <= 3; dy++) for (int dz = -8; dz <= 8; dz++) {
            BlockPos log = origin.offset(dx, dy, dz);
            if (log.getX() < minX || log.getX() > maxX || log.getZ() < minZ || log.getZ() > maxZ
                    || !level.getBlockState(log).is(BlockTags.LOGS)) continue;
            for (Direction direction : directions) {
                BlockPos hive = log.relative(direction);
                if (hive.getX() >= minX && hive.getX() <= maxX && hive.getZ() >= minZ && hive.getZ() <= maxZ
                        && level.getBlockState(hive).isAir()
                        && BeeHiveContent.WILD.defaultBlockState().canSurvive(level, hive)) hives.add(hive);
            }
        }
        if (hives.isEmpty()) return false;
        return level.setBlock(hives.get(context.random().nextInt(hives.size())),
                BeeHiveContent.WILD.defaultBlockState(), 2);
    }
}
