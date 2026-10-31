package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;

final class BeeHivePollenTracker {
    private static final int HORIZONTAL_RADIUS = 4;
    private static final int VERTICAL_RADIUS = 2;

    private BeeHivePollenTracker() {}

    static int countNearbyFlowers(Level level, BlockPos origin) {
        if (level == null || origin == null) return 0;
        int count = 0;
        BlockPos min = origin.offset(-HORIZONTAL_RADIUS, -VERTICAL_RADIUS, -HORIZONTAL_RADIUS);
        BlockPos max = origin.offset(HORIZONTAL_RADIUS, VERTICAL_RADIUS, HORIZONTAL_RADIUS);
        for (BlockPos pos : BlockPos.betweenClosed(min, max)) {
            if (!level.hasChunkAt(pos)) continue;
            if (level.getBlockState(pos).is(BlockTags.FLOWERS)) {
                count++;
                if (count >= 8) break;
            }
        }
        return count;
    }

    static boolean hasNearbyFlowers(Level level, BlockPos origin) {
        return countNearbyFlowers(level, origin) > 0;
    }

    static int honeyYield(Level level, BlockPos origin) {
        return countNearbyFlowers(level, origin) >= 4 ? 30 : 25;
    }
}
