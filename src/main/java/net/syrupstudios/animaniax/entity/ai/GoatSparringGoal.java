package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.GoatSparringState;

public final class GoatSparringGoal extends Goal {
    private static final int SEARCH_DELAY = 2001;
    private static final int START_TIMEOUT = 40;
    private static final double SEARCH_RANGE = 10.0;
    private final Goat buck;
    private int searchDelay;
    private Goat rival;

    public GoatSparringGoal(Goat buck) {
        this.buck = buck;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(buck.level() instanceof ServerLevel level)) return false;
        GoatSparringState state = (GoatSparringState) buck;
        UUID partnerId = state.animaniax$sparringPartner();
        if (partnerId != null) {
            Entity entity = level.getEntity(partnerId);
            if (entity instanceof Goat goat && animaniax$validPair(buck, goat, level)) {
                rival = goat;
                return true;
            }
            animaniax$clearPair(buck, level);
        }
        if (searchDelay < SEARCH_DELAY) searchDelay++;
        if (searchDelay < SEARCH_DELAY || !level.isDay() || !animaniax$eligible(buck)) return false;
        searchDelay = 0;
        if (buck.getRandom().nextInt(20) == 0) return false;

        AABB bounds = buck.getBoundingBox().inflate(SEARCH_RANGE);
        for (Goat candidate : level.getEntitiesOfClass(Goat.class, bounds,
                other -> other != buck && animaniax$eligible(other)
                        && ((GoatSparringState) other).animaniax$sparringPartner() == null)) {
            if (animaniax$eligible(buck) && animaniax$eligible(candidate)) {
                long now = level.getGameTime();
                long endTick = now + 100 + buck.getRandom().nextInt(50);
                long startDeadline = now + START_TIMEOUT;
                ((GoatSparringState) buck).animaniax$setSparring(candidate.getUUID(), startDeadline, endTick);
                ((GoatSparringState) candidate).animaniax$setSparring(buck.getUUID(), startDeadline, endTick);
                rival = candidate;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(buck.level() instanceof ServerLevel level) || rival == null
                || !animaniax$validPair(buck, rival, level)) return false;
        GoatSparringState ownState = (GoatSparringState) buck;
        GoatSparringState rivalState = (GoatSparringState) rival;
        long now = level.getGameTime();
        return now < ownState.animaniax$sparringEndTick()
                && (ownState.animaniax$isSparringActive() && rivalState.animaniax$isSparringActive()
                        || now < ownState.animaniax$sparringStartDeadline());
    }

    @Override
    public void start() {
        if (rival != null) ((GoatSparringState) buck).animaniax$setSparringActive(true);
    }

    @Override
    public void tick() {
        if (rival == null || !(buck.level() instanceof ServerLevel level)
                || !animaniax$validPair(buck, rival, level)) return;
        GoatSparringState ownState = (GoatSparringState) buck;
        GoatSparringState rivalState = (GoatSparringState) rival;
        if (!ownState.animaniax$isSparringActive() || !rivalState.animaniax$isSparringActive()) return;
        buck.getLookControl().setLookAt(rival, 10.0F, buck.getMaxHeadXRot());
        if (buck.onGround() && rival.onGround() && buck.distanceToSqr(rival) <= 4.0
                && !animaniax$brainOwnsMovement(buck) && !animaniax$brainOwnsMovement(rival)
                && buck.getRandom().nextInt(20) == 0) {
            Vec3 motion = buck.getDeltaMovement();
            double dx = rival.getX() - buck.getX();
            double dz = rival.getZ() - buck.getZ();
            double distance = Math.sqrt(dx * dx + dz * dz);
            if (distance >= 1.0E-4) {
                buck.setDeltaMovement(dx / distance * 0.4 + motion.x * 0.2, 0.25,
                        dz / distance * 0.4 + motion.z * 0.2);
            }
        }
        if (buck.distanceToSqr(rival) <= 2.25) {
            buck.getNavigation().stop();
        } else if (buck.tickCount % 4 == 0) {
            buck.getNavigation().moveTo(rival, 1.3);
        }
    }

    @Override
    public void stop() {
        if (buck.level() instanceof ServerLevel level) animaniax$clearPair(buck, level);
        if (!animaniax$brainOwnsMovement(buck)) buck.getNavigation().stop();
        rival = null;
    }

    private static boolean animaniax$validPair(Goat buck, Goat rival, ServerLevel level) {
        return AnimalCareConfig.animalsCanAttackOthers && level.isDay()
                && animaniax$eligible(buck) && animaniax$eligible(rival)
                && rival.level() == level && level.getEntity(rival.getUUID()) == rival
                && level.getGameTime() < ((GoatSparringState) buck).animaniax$sparringEndTick()
                && rival.getUUID().equals(((GoatSparringState) buck).animaniax$sparringPartner())
                && buck.getUUID().equals(((GoatSparringState) rival).animaniax$sparringPartner())
                && (((GoatSparringState) buck).animaniax$isSparringActive()
                        && ((GoatSparringState) rival).animaniax$isSparringActive()
                        || level.getGameTime() < ((GoatSparringState) buck).animaniax$sparringStartDeadline());
    }

    private static boolean animaniax$eligible(Goat goat) {
        return goat.isAlive() && !goat.isBaby() && !goat.isSleeping() && goat.getTarget() == null
                && !goat.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET)
                && !goat.getBrain().hasMemoryValue(MemoryModuleType.LONG_JUMP_MID_JUMP)
                && !goat.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET)
                && !goat.isLeashed() && !goat.isVehicle() && !goat.isPassenger()
                && !((AnimalSleepState) goat).animaniax$isSleeping()
                && goat instanceof AnimalReproductionState sex && !sex.animaniax$isFemale()
                && goat instanceof AnimalSterilizationState sterilization
                && !sterilization.animaniax$isSterilized()
                && AnimalCareConfig.animalsCanAttackOthers;
    }

    private static boolean animaniax$brainOwnsMovement(Goat goat) {
        return goat.getBrain().hasMemoryValue(MemoryModuleType.RAM_TARGET)
                || goat.getBrain().hasMemoryValue(MemoryModuleType.LONG_JUMP_MID_JUMP)
                || goat.getBrain().hasMemoryValue(MemoryModuleType.WALK_TARGET);
    }

    private static void animaniax$clearPair(Goat goat, ServerLevel level) {
        GoatSparringState state = (GoatSparringState) goat;
        UUID partnerId = state.animaniax$sparringPartner();
        state.animaniax$clearSparring();
        if (partnerId == null) return;
        Entity partner = level.getEntity(partnerId);
        if (partner instanceof Goat rival
                && ((GoatSparringState) rival).animaniax$sparringPartner() != null
                && ((GoatSparringState) rival).animaniax$sparringPartner().equals(goat.getUUID())) {
            ((GoatSparringState) rival).animaniax$clearSparring();
        }
    }
}
