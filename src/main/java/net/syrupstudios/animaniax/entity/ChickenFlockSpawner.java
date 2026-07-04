package net.syrupstudios.animaniax.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;

public final class ChickenFlockSpawner {
    private ChickenFlockSpawner() {}

    public static void spawn(ServerLevelAccessor level, DifficultyInstance difficulty, Chicken hen) {
        if (hen.isBaby() || !((ChickenSexState) hen).animaniax$isHen()) return;
        String breed = ((ChickenBreedState) hen).animaniax$getChickenBreed();
        AABB flockBounds = hen.getBoundingBox().inflate(64);
        if (level.getEntitiesOfClass(Chicken.class, flockBounds,
                nearby -> nearby.getType() == EntityType.CHICKEN).size() > 4) return;
        int choice = hen.getRandom().nextInt(3);
        if (choice == 2) return;

        Chicken member = EntityType.CHICKEN.create(level.getLevel());
        if (member == null) return;
        BlockPos spawnPos = findSpawnPosition(level, hen, member);
        if (spawnPos == null) return;
        member.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
        member.finalizeSpawn(level, difficulty, MobSpawnType.TRIGGERED, null);
        ((ChickenBreedState) member).animaniax$setChickenBreed(breed);
        if (choice == 0) ((ChickenSexState) member).animaniax$setHen(false);
        else member.setBaby(true);
        level.addFreshEntity(member);
    }

    private static BlockPos findSpawnPosition(ServerLevelAccessor level, Chicken hen, Chicken candidate) {
        BlockPos origin = hen.blockPosition();
        int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}, {2, 2}, {-2, 2}, {2, -2}, {-2, -2},
                {3, 1}, {-3, 1}, {3, -1}, {-3, -1}, {1, 3}, {-1, 3}, {1, -3}, {-1, -3}};
        int start = hen.getRandom().nextInt(offsets.length);
        for (int i = 0; i < offsets.length; i++) {
            int[] offset = offsets[(start + i) % offsets.length];
            BlockPos pos = origin.offset(offset[0], 0, offset[1]);
            if (!level.hasChunkAt(pos) || !level.hasChunkAt(pos.below())
                    || !level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                    || !level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                    || !level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP)) continue;
            candidate.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            if (level.noCollision(candidate)) return pos;
        }
        return null;
    }
}
