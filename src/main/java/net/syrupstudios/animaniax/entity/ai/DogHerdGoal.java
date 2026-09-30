package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.syrupstudios.animaniax.entity.DogBreed;
import net.syrupstudios.animaniax.entity.DogEntity;
import net.syrupstudios.animaniax.entity.AnimalSleepState;

public final class DogHerdGoal extends Goal {
    private final Animal animal;
    private DogEntity herder;

    public DogHerdGoal(Animal animal) {
        this.animal = animal;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        if (animal.isSleeping() || ((AnimalSleepState) animal).animaniax$isSleeping()
                || animal.isOnFire() || animal.isAggressive()) return false;
        if (animal.getLastHurtByMob() != null && animal.tickCount - animal.getLastHurtByMobTimestamp() < 100) return false;
        herder = animal.level().getEntitiesOfClass(DogEntity.class, animal.getBoundingBox().inflate(10), this::isHerder)
                .stream().min((a, b) -> Double.compare(animal.distanceToSqr(a), animal.distanceToSqr(b))).orElse(null);
        return herder != null;
    }

    @Override public boolean canContinueToUse() { return canUse(); }

    @Override public void tick() {
        if (herder == null || herder.getNavigation().getPath() == null) return;
        Path path = herder.getNavigation().getPath();
        BlockPos target = path.getTarget();
        animal.getNavigation().moveTo(target.getX(), target.getY(), target.getZ(), 1.0);
    }

    private boolean isHerder(DogEntity dog) {
        return dog.getBreed() == DogBreed.GERMAN_SHEPHERD && dog.isTame()
                && !dog.isOrderedToSit() && !dog.isInSittingPose() && !dog.isSleeping()
                && dog.getNavigation().getPath() != null && !dog.getNavigation().isDone();
    }
}
