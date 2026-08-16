package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.phys.AABB;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.SheepSparringState;

public final class SheepSparringGoal extends Goal {
    private static final int SEARCH_DELAY = 1000;
    private static final int START_TIMEOUT = 40;
    private static final double SEARCH_RANGE = 10.0;
    private final Sheep ram;
    private int searchDelay;
    private Sheep rival;

    public SheepSparringGoal(Sheep ram) {
        this.ram = ram;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!(ram.level() instanceof ServerLevel level)) return false;
        SheepSparringState state = (SheepSparringState) ram;
        UUID partnerId = state.animaniax$sparringPartner();
        if (partnerId != null) {
            Entity entity = level.getEntity(partnerId);
            if (entity instanceof Sheep sheep && animaniax$validPair(ram, sheep, level)) {
                rival = sheep;
                return true;
            }
            animaniax$clearPair(ram, level);
        }
        if (!animaniax$eligible(ram) || !level.isDay()) {
            searchDelay = 0;
            return false;
        }
        if (++searchDelay <= SEARCH_DELAY) return false;
        searchDelay = 0;
        if (ram.getRandom().nextInt(20) == 0) return false;

        AABB bounds = ram.getBoundingBox().inflate(SEARCH_RANGE);
        for (Sheep candidate : level.getEntitiesOfClass(Sheep.class, bounds,
                other -> other != ram && animaniax$eligible(other)
                        && ((SheepSparringState) other).animaniax$sparringPartner() == null)) {
            if (animaniax$eligible(ram) && animaniax$eligible(candidate)) {
                long now = level.getGameTime();
                long endTick = now + 100 + ram.getRandom().nextInt(50);
                long startDeadline = now + START_TIMEOUT;
                ((SheepSparringState) ram).animaniax$setSparring(candidate.getUUID(), startDeadline, endTick);
                ((SheepSparringState) candidate).animaniax$setSparring(ram.getUUID(), startDeadline, endTick);
                rival = candidate;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if (!(ram.level() instanceof ServerLevel level) || rival == null
                || !animaniax$validPair(ram, rival, level)) return false;
        SheepSparringState ownState = (SheepSparringState) ram;
        SheepSparringState rivalState = (SheepSparringState) rival;
        long now = level.getGameTime();
        return now < ownState.animaniax$sparringEndTick()
                && (ownState.animaniax$isSparringActive() && rivalState.animaniax$isSparringActive()
                        || now < ownState.animaniax$sparringStartDeadline());
    }

    @Override
    public void start() {
        if (rival != null) ((SheepSparringState) ram).animaniax$setSparringActive(true);
    }

    @Override
    public void tick() {
        if (rival == null) return;
        SheepSparringState ownState = (SheepSparringState) ram;
        SheepSparringState rivalState = (SheepSparringState) rival;
        if (!ownState.animaniax$isSparringActive() || !rivalState.animaniax$isSparringActive()) return;
        ram.getLookControl().setLookAt(rival, 10.0F, ram.getMaxHeadXRot());
        if (ram.distanceToSqr(rival) <= 2.25) {
            ram.getNavigation().stop();
        } else if (ram.tickCount % 4 == 0) {
            ram.getNavigation().moveTo(rival, 1.3);
        }
    }

    @Override
    public void stop() {
        if (ram.level() instanceof ServerLevel level) animaniax$clearPair(ram, level);
        ram.getNavigation().stop();
        rival = null;
    }

    private static boolean animaniax$validPair(Sheep ram, Sheep rival, ServerLevel level) {
        return AnimalCareConfig.animalsCanAttackOthers && level.isDay()
                && animaniax$eligible(ram) && animaniax$eligible(rival)
                && rival.level() == level && level.getEntity(rival.getUUID()) == rival
                && level.getGameTime() < ((SheepSparringState) ram).animaniax$sparringEndTick()
                && rival.getUUID().equals(((SheepSparringState) ram).animaniax$sparringPartner())
                && ram.getUUID().equals(((SheepSparringState) rival).animaniax$sparringPartner())
                && (((SheepSparringState) ram).animaniax$isSparringActive()
                        && ((SheepSparringState) rival).animaniax$isSparringActive()
                        || level.getGameTime() < ((SheepSparringState) ram).animaniax$sparringStartDeadline());
    }

    private static boolean animaniax$eligible(Sheep sheep) {
        return sheep.isAlive() && !sheep.isBaby() && !sheep.isSleeping()
                && !sheep.isLeashed() && !sheep.isVehicle() && !sheep.isPassenger() && sheep.getTarget() == null
                && !((AnimalSleepState) sheep).animaniax$isSleeping()
                && sheep instanceof AnimalReproductionState sex && !sex.animaniax$isFemale()
                && sheep instanceof AnimalSterilizationState sterilization
                && !sterilization.animaniax$isSterilized()
                && AnimalCareConfig.animalsCanAttackOthers;
    }

    private static void animaniax$clearPair(Sheep sheep, ServerLevel level) {
        SheepSparringState state = (SheepSparringState) sheep;
        UUID partnerId = state.animaniax$sparringPartner();
        state.animaniax$clearSparring();
        if (partnerId == null) return;
        Entity partner = level.getEntity(partnerId);
        if (partner instanceof Sheep rival
                && ((SheepSparringState) rival).animaniax$sparringPartner() != null
                && ((SheepSparringState) rival).animaniax$sparringPartner().equals(sheep.getUUID())) {
            ((SheepSparringState) rival).animaniax$clearSparring();
        }
    }
}
