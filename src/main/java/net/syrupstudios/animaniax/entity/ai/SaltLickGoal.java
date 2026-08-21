package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.syrupstudios.animaniax.block.MudBlock;
import net.syrupstudios.animaniax.block.SaltLickBlock;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalProfile.Capability;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class SaltLickGoal extends Goal {
    private static final Direction[] HORIZONTAL = {Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
    private final Mob animal;
    private BlockPos target;
    private Path path;
    private long scanAt;

    public SaltLickGoal(Mob animal) {
        this.animal = animal;
        this.scanAt = (long) animal.tickCount + AnimalCareConfig.saltLickTick;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() {
        if (animal.tickCount <= scanAt) return false;
        if (!isEligible() || animal.getHealth() >= animal.getMaxHealth()
                || animal.isVehicle() || animal.isPassenger() || animal.isSleeping()
                || ((AnimalSleepState) animal).animaniax$isSleeping()) {
            scheduleScan();
            return false;
        }
        if (animal.getRandom().nextInt(3) != 0) return false;
        target = null;
        path = null;
        BlockPos origin = animal.blockPosition();
        for (int radius = 0; radius < AnimalCareConfig.aiBlockSearchRange; radius++) {
            for (int y = -AnimalCareConfig.aiBlockSearchRange / 2 - 1; y <= AnimalCareConfig.aiBlockSearchRange / 2 - 1; y++) {
                for (int x = -radius; x <= radius; x++) {
                    if (Math.abs(x) == radius) {
                        for (int z = -radius; z <= radius; z++) if (tryLick(origin.offset(x, y, z))) return true;
                    } else if (tryLick(origin.offset(x, y, -radius)) || tryLick(origin.offset(x, y, radius))) return true;
                }
            }
        }
        scheduleScan();
        return false;
    }

    private boolean tryLick(BlockPos lick) {
        if (!animal.level().hasChunkAt(lick) || !animal.level().getBlockState(lick).is(SaltLickContent.BLOCK)) return false;
        Path reachable = pathToAdjacent(lick);
        if (reachable == null) return false;
        target = lick;
        path = reachable;
        scheduleScan();
        return true;
    }

    private Path pathToAdjacent(BlockPos lick) {
        PathNavigation navigation = animal.getNavigation();
        for (Direction direction : HORIZONTAL) {
            BlockPos destination = lick.relative(direction);
            if (!animal.level().hasChunkAt(destination)) continue;
            Path candidate = navigation.createPath(destination, 0);
            if (candidate != null && candidate.canReach()) return candidate;
        }
        return null;
    }

    private void scheduleScan() {
        scanAt = (long) animal.tickCount + AnimalCareConfig.saltLickTick;
    }

    @Override public boolean canContinueToUse() {
        return target != null && isEligible() && animal.getHealth() < animal.getMaxHealth()
                && !animal.isVehicle() && !animal.isPassenger() && !animal.isSleeping()
                && !((AnimalSleepState) animal).animaniax$isSleeping();
    }

    private boolean isEligible() {
        boolean saltLickEligible = AnimalProfile.has(animal.getType(), Capability.SALT_LICK_GOAL);
        return saltLickEligible && (animal.getType() != net.minecraft.world.entity.EntityType.PIG
                || !MudBlock.isMud(animal.level().getBlockState(animal.blockPosition().below())));
    }

    @Override public void start() {
        animal.getNavigation().moveTo(path, 1.0);
    }

    @Override public void tick() {
        if (target == null) return;
        animal.getLookControl().setLookAt(target.getX() + .5, target.getY() + .5, target.getZ() + .5);
        if (animal.distanceToSqr(target.getX() + .5, target.getY(), target.getZ() + .5) <= 4.0) {
            animal.getNavigation().stop();
            SaltLickBlock.use(animal.level(), target, animal);
            target = null;
            path = null;
            scheduleScan();
        } else if (animal.getNavigation().isDone()) {
            target = null;
            path = null;
            scheduleScan();
        }
    }

    @Override public void stop() { animal.getNavigation().stop(); target = null; path = null; }
}
