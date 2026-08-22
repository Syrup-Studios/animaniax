package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.Path;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;

public final class AnimalSleepGoal extends Goal {
    private static final int SEARCH_RANGE = 16;
    private static final int MAX_PATH_CHECKS = 8;
    private final Mob mob;
    private int delay;
    private int bedSearchIndex;
    private int candidateSearchIndex;
    private int pathChecks;
    private boolean bedSearchComplete;
    private int searchCandidateOrdinal;
    private BlockPos target;

    public AnimalSleepGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        AnimalSleepState sleep = (AnimalSleepState) mob;
        if (!AnimalCareConfig.animalsSleep) {
            sleep.animaniax$setSleeping(false);
            return false;
        }
        if (isOrderedToSit()) {
            sleep.animaniax$setSleeping(false);
            return false;
        }
        if (sleep.animaniax$isSleeping()) {
            if (shouldWake()) {
                sleep.animaniax$setSleeping(false);
                return false;
            }
            target = null;
            return true;
        }
        if (++delay <= 100 + mob.getRandom().nextInt(100)) return false;
        delay = 0;
        if (!isSleepTime() || mob.isLeashed() || mob.isVehicle()
                || mob.isPassenger() || mob.getTarget() != null || mob.isOnFire() || mob.isInWater()
                || mob.level().isRainingAt(mob.blockPosition()) || mob.getRandom().nextInt(3) != 0) return false;
        target = findBed();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return AnimalCareConfig.animalsSleep && (isSleeping() ? !shouldWake() : target != null && !shouldWake());
    }

    @Override
    public void start() {
        if (isSleeping()) return;
        mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0.8);
    }

    @Override
    public void tick() {
        if (isSleeping()) return;
        if (mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) <= 2.5) {
            ((AnimalSleepState) mob).animaniax$setSleeping(true);
            target = null;
            delay = 0;
        } else if (mob.getNavigation().isDone()) {
            target = null;
            delay = 0;
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        if (!AnimalCareConfig.animalsSleep || shouldWake()) ((AnimalSleepState) mob).animaniax$setSleeping(false);
        target = null;
    }

    private boolean shouldWake() {
        BlockPos pos = mob.blockPosition();
        return !AnimalCareConfig.animalsSleep || isOrderedToSit()
                || !isSleepTime() || mob.isOnFire() || mob.isLeashed()
                || mob.isVehicle() || mob.isPassenger() || mob.isInWater() || mob.getTarget() != null
                || mob.getLastHurtByMob() != null && mob.tickCount - mob.getLastHurtByMobTimestamp() < 20
                || mob.isFreezing() || mob.level().getBlockState(pos).is(Blocks.POWDER_SNOW)
                || mob instanceof AnimalReproductionState reproduction && reproduction.animaniax$isPregnant()
                        && reproduction.animaniax$gestationRemaining() < 200
                || mob.level().isRainingAt(pos) && mob.level().canSeeSky(pos);
    }

    private BlockPos findBed() {
        List<AnimalProfile.SleepBed> beds = AnimalProfile.sleepBeds(mob.getType());
        if (beds.isEmpty()) return null;
        pathChecks = 0;
        int completedBeds = 0;
        while (pathChecks < MAX_PATH_CHECKS && completedBeds < beds.size()) {
            AnimalProfile.SleepBed bed = beds.get(bedSearchIndex);
            BlockPos found = findBed(bed, mob.blockPosition().offset(0, bed.scanOriginYOffset(), 0));
            if (found != null) {
                bedSearchIndex = 0;
                candidateSearchIndex = 0;
                return found;
            }
            if (!bedSearchComplete) return null;
            bedSearchIndex = (bedSearchIndex + 1) % beds.size();
            candidateSearchIndex = 0;
            if (++completedBeds == beds.size()) bedSearchIndex = 0;
        }
        return null;
    }

    private BlockPos findBed(AnimalProfile.SleepBed bed, BlockPos origin) {
        bedSearchComplete = true;
        searchCandidateOrdinal = 0;
        for (int radius = 0; radius <= SEARCH_RANGE; radius++) {
            for (int y = -SEARCH_RANGE / 2; y <= SEARCH_RANGE / 2; y++) {
                for (int x = -radius; x <= radius; x++) {
                    BlockPos found = findReachableBed(bed, origin.offset(x, y, -radius));
                    if (found != null) return found;
                    if (!bedSearchComplete) return null;
                    if (radius > 0) {
                        found = findReachableBed(bed, origin.offset(x, y, radius));
                        if (found != null) return found;
                        if (!bedSearchComplete) return null;
                    }
                }
                for (int z = -radius + 1; z < radius; z++) {
                    for (int x : new int[]{-radius, radius}) {
                        BlockPos found = findReachableBed(bed, origin.offset(x, y, z));
                        if (found != null) return found;
                        if (!bedSearchComplete) return null;
                    }
                }
            }
        }
        bedSearchComplete = true;
        return null;
    }

    private BlockPos findReachableBed(AnimalProfile.SleepBed bed, BlockPos floor) {
        if (!mob.level().hasChunkAt(floor) || !mob.level().getBlockState(floor).is(bed.block())) return null;
        if (searchCandidateOrdinal++ < candidateSearchIndex) return null;
        if (pathChecks >= MAX_PATH_CHECKS) {
            bedSearchComplete = false;
            return null;
        }
        candidateSearchIndex++;
        pathChecks++;
        Path path = mob.getNavigation().createPath(floor.offset(0, bed.destinationYOffset(), 0), 0);
        return path != null && path.canReach() ? floor.offset(0, bed.destinationYOffset(), 0) : null;
    }

    private boolean isSleeping() { return ((AnimalSleepState) mob).animaniax$isSleeping(); }

    private boolean isSleepTime() {
        long time = mob.level().getDayTime() % 24000L;
        return AnimalProfile.shouldSleepAt(mob.getType(), time);
    }

    private boolean isOrderedToSit() {
        return mob instanceof TamableAnimal pet && pet.isOrderedToSit();
    }
}
