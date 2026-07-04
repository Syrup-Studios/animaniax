package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Chicken;
import net.syrupstudios.animaniax.block.NestBlock;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.ChickenSexState;

public final class NestingGoal extends Goal {
    private final Chicken chicken;
    private BlockPos nest;
    private BlockPos approach;
    private int nextSearch;

    public NestingGoal(Chicken chicken) {
        this.chicken = chicken;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() {
        if (!chicken.level().isDay() || chicken.isSleeping()
                || ((AnimalSleepState) chicken).animaniax$isSleeping()
                || !((ChickenSexState) chicken).animaniax$isHen() || chicken.isBaby()
                || !((ChickenSexState) chicken).animaniax$isNestLayReady()
                || chicken.isPassenger() || chicken.isVehicle() || animaniax$needsCare()
                || chicken.tickCount < nextSearch) return false;
        nest = findNest();
        nextSearch = chicken.tickCount + 40;
        return nest != null && approach != null;
    }

    @Override public boolean canContinueToUse() {
        return nest != null && approach != null && ((ChickenSexState) chicken).animaniax$isNestLayReady()
                && !chicken.isPassenger() && !chicken.isVehicle()
                && !animaniax$needsCare()
                && chicken.level().getBlockState(nest).is(NestContent.NEST)
                && NestBlock.canAcceptEgg(chicken.level(), nest, animaniax$eggBreed());
    }

    @Override public void start() {
        if (animaniax$isAtApproach()) {
            chicken.getNavigation().stop();
        } else if (nest == null || approach == null || !animaniax$moveToApproach()) {
            nest = null;
            approach = null;
            nextSearch = chicken.tickCount + 100;
        }
    }

    @Override public void tick() {
        if (nest == null || approach == null) return;
        if (animaniax$isAtApproach()) {
            chicken.getNavigation().stop();
            if (NestBlock.addEgg(chicken.level(), nest, animaniax$eggBreed())) {
                ((ChickenSexState) chicken).animaniax$resetNestLayCooldown();
                stop();
                nextSearch = chicken.tickCount + 40;
            }
        } else if (chicken.getNavigation().isDone()) {
            if (!animaniax$moveToApproach()) {
                nest = null;
                approach = null;
                nextSearch = chicken.tickCount + 100;
            }
        }
    }

    @Override public void stop() {
        chicken.getNavigation().stop();
        nest = null;
        approach = null;
    }

    private BlockPos findNest() {
        approach = null;
        BlockPos origin = chicken.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int y = -2; y <= 2; y++) for (int x = -16; x <= 16; x++) for (int z = -16; z <= 16; z++) {
            BlockPos pos = origin.offset(x, y, z);
            if (x * x + z * z > 256) continue;
            if (!chicken.level().hasChunkAt(pos)) continue;
            var state = chicken.level().getBlockState(pos);
            if (!state.is(NestContent.NEST) || !NestBlock.canAcceptEgg(chicken.level(), pos, animaniax$eggBreed())) continue;
            BlockPos candidateApproach = findApproach(pos);
            if (candidateApproach == null) continue;
            double distance = chicken.distanceToSqr(candidateApproach.getX() + 0.5,
                    candidateApproach.getY(), candidateApproach.getZ() + 0.5);
            if (distance < nearestDistance) {
                nearest = pos;
                approach = candidateApproach;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private BlockPos findApproach(BlockPos nest) {
        BlockPos current = chicken.blockPosition();
        if (current.equals(nest) || current.equals(nest.above())) return current;
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos candidate = nest.relative(direction);
            if (!chicken.level().hasChunkAt(candidate) || !chicken.level().getBlockState(candidate).isAir()
                    || !chicken.level().getBlockState(candidate.below())
                    .isFaceSturdy(chicken.level(), candidate.below(), Direction.UP)) continue;
            double distance = chicken.distanceToSqr(candidate.getX() + 0.5,
                    candidate.getY() + 0.5, candidate.getZ() + 0.5);
            if (distance < nearestDistance) { nearest = candidate; nearestDistance = distance; }
        }
        return nearest;
    }

    private boolean animaniax$isAtApproach() {
        return approach != null && ((approach.equals(nest) || approach.equals(nest.above())
                ? chicken.blockPosition().equals(approach)
                : chicken.distanceToSqr(approach.getX() + 0.5,
                        approach.getY(), approach.getZ() + 0.5) <= 0.25));
    }

    private boolean animaniax$moveToApproach() {
        return approach != null && chicken.getNavigation().moveTo(
                approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 0, 1.0);
    }

    private boolean animaniax$needsCare() {
        AnimalCareState care = (AnimalCareState) chicken;
        return care.animaniax$needsWater() || care.animaniax$needsSeeds();
    }

    private String animaniax$eggBreed() {
        return ((ChickenBreedState) chicken).animaniax$getChickenBreed();
    }
}
