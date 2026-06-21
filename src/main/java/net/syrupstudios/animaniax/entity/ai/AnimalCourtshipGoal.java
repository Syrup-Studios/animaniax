package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.ChickenSexState;

public final class AnimalCourtshipGoal extends Goal {
    private static final int PARTNER_RANGE = 8;
    private final Animal animal;
    private Animal mate;
    private int scanAt;
    private int courtshipTicks;

    public AnimalCourtshipGoal(Animal animal) {
        this.animal = animal;
        this.scanAt = animal.tickCount + animal.getRandom().nextInt(100);
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override public boolean canUse() {
        if (animal.level().isClientSide || animal.tickCount < scanAt || !isMale(animal) || !eligible(animal)) return false;
        scanAt = animal.tickCount + AnimalCareConfig.matingTimer;
        if (tooManyNearby()) return false;
        AABB bounds = animal.getBoundingBox().inflate(PARTNER_RANGE);
        List<Animal> candidates = animal.level().getEntitiesOfClass(Animal.class, bounds,
                candidate -> candidate != animal && candidate.getType() == animal.getType()
                        && isFemale(candidate) && eligible(candidate)
                        && ((AnimalReproductionState) animal).animaniax$canMateBonded(candidate)
                        && candidate.hasLineOfSight(animal)
                        && animal.distanceToSqr(candidate) <= PARTNER_RANGE * PARTNER_RANGE);
        mate = candidates.stream().findFirst().orElse(null);
        return mate != null;
    }

    @Override public boolean canContinueToUse() {
        return mate != null && courtshipTicks > 0 && eligible(animal) && eligible(mate)
                && ((AnimalReproductionState) animal).animaniax$canMateBonded(mate)
                && !animal.isInLove() && !mate.isInLove();
    }

    @Override public void start() { courtshipTicks = 200; }

    @Override public void tick() {
        if (mate == null) return;
        animal.getLookControl().setLookAt(mate, 10.0F, animal.getMaxHeadXRot());
        mate.getLookControl().setLookAt(animal, 10.0F, mate.getMaxHeadXRot());
        double distance = animal.distanceToSqr(mate);
        if (distance <= 3.24 && mate.hasLineOfSight(animal)) {
            animal.getNavigation().stop();
            mate.getNavigation().stop();
            ((AnimalReproductionState) animal).animaniax$setCourtshipPartner(mate.getUUID());
            ((AnimalReproductionState) mate).animaniax$setCourtshipPartner(animal.getUUID());
            animal.setInLove(null);
            mate.setInLove(null);
            mate = null;
            return;
        }
        if (courtshipTicks-- % 20 == 0) {
            animal.getNavigation().moveTo(mate, 1.0);
            mate.getNavigation().moveTo(animal, 1.0);
        }
    }

    @Override public void stop() {
        animal.getNavigation().stop();
        if (mate != null) mate.getNavigation().stop();
        mate = null;
    }

    private boolean eligible(Animal candidate) {
        if (animaniax$hasActiveCourtship(candidate)) return false;
        if (candidate.isBaby() || candidate.getAge() != 0 || candidate.isInLove() || animaniax$isSleeping(candidate)
                || candidate.isVehicle() || candidate.isPassenger() || !candidate.canFallInLove()) return false;
        AnimalCareState care = (AnimalCareState) candidate;
        AnimalSterilizationState sterilization = (AnimalSterilizationState) candidate;
        AnimalReproductionState reproduction = (AnimalReproductionState) candidate;
        if (sterilization.animaniax$isSterilized() || care.animaniax$careBlocksBreeding()
                || reproduction.animaniax$isPregnant() || reproduction.animaniax$fertilityCooldownRemaining() > 0
                || !care.animaniax$isFed() || !care.animaniax$isWatered()) return false;
        if (AnimalCareConfig.requireAnimalInteractionForAI && !care.animaniax$wasInteractedWith()) return false;
        return !AnimalCareConfig.feedToBreed || care.animaniax$wasHandFed();
    }

    private static boolean animaniax$isSleeping(Animal candidate) {
        return candidate instanceof AnimalSleepState sleepState && sleepState.animaniax$isSleeping()
                || candidate.isSleeping();
    }

    private boolean animaniax$hasActiveCourtship(Animal candidate) {
        AnimalReproductionState reproduction = (AnimalReproductionState) candidate;
        java.util.UUID partnerId = reproduction.animaniax$courtshipPartner();
        if (partnerId == null) return false;
        net.minecraft.world.entity.Entity partner = candidate.level() instanceof ServerLevel level
                ? level.getEntity(partnerId) : null;
        if (!candidate.isInLove() || !(partner instanceof Animal mate) || !mate.isAlive() || !mate.isInLove()
                || !candidate.getUUID().equals(((AnimalReproductionState) mate).animaniax$courtshipPartner())) {
            reproduction.animaniax$setCourtshipPartner(null);
            return false;
        }
        return true;
    }

    private boolean tooManyNearby() {
        int limit = AnimalCareConfig.entityBreedingLimit;
        if (limit <= 0) return true;
        AABB bounds = animal.getBoundingBox().inflate(AnimalCareConfig.animalCapSearchRange);
        return animal.level().getEntitiesOfClass(Animal.class, bounds,
                candidate -> candidate.getType() == animal.getType() && candidate != animal).size() + 1 >= limit;
    }

    private static boolean isMale(Animal animal) {
        if (animal instanceof Chicken chicken)
            return !((ChickenSexState) chicken).animaniax$isHen();
        return animal instanceof AnimalReproductionState reproduction && !reproduction.animaniax$isFemale();
    }

    private static boolean isFemale(Animal animal) {
        if (animal instanceof Chicken chicken)
            return ((ChickenSexState) chicken).animaniax$isHen();
        return animal instanceof AnimalReproductionState reproduction && reproduction.animaniax$isFemale();
    }
}
