package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;
import net.syrupstudios.animaniax.entity.AnimalSleepState;

public final class JuvenilePlayGoal extends Goal {
    private static final int PRIORITY = 7;
    private final Mob animal;
    private final Class<? extends Mob> mateType;
    private Mob mate;
    private boolean active;
    private boolean chaser;
    private boolean canSwap = true;
    private boolean started;
    private int startWait;

    public JuvenilePlayGoal(Mob animal, Class<? extends Mob> mateType) {
        this.animal = animal;
        this.mateType = mateType;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override public boolean canUse() {
        if (active) return true;
        return eligible(animal)
                && !animal.level().getEntitiesOfClass(mateType, animal.getBoundingBox().inflate(5), this::available).isEmpty()
                && animal.getRandom().nextFloat() < 0.2F;
    }

    @Override public boolean canContinueToUse() {
        if (!active || mate == null || !mate.isAlive() || mate.level() != animal.level()
                || !eligible(animal) || !eligible(mate)) return false;
        JuvenilePlayGoal other = (JuvenilePlayGoal) mateGoal(mate);
        if (!other.active || other.mate != animal) return false;
        if (!other.started && ++startWait > 5) return false;
        return animal.getRandom().nextFloat() >= 0.1F;
    }

    @Override public void start() {
        if (active) {
            started = true;
            return;
        }
        mate = animal.level().getEntitiesOfClass(mateType, animal.getBoundingBox().inflate(5), this::available)
                .stream().findFirst().orElse(null);
        if (mate == null) return;
        JuvenilePlayGoal other = (JuvenilePlayGoal) mateGoal(mate);
        active = other.active = true;
        started = true;
        other.started = false;
        startWait = other.startWait = 0;
        chaser = true;
        other.chaser = false;
        other.mate = animal;
        other.canSwap = canSwap = true;
    }

    @Override public void tick() {
        if (!active || mate == null) return;
        JuvenilePlayGoal other = (JuvenilePlayGoal) mateGoal(mate);
        if (!other.started) return;
        double distance = animal.distanceTo(mate);
        if (distance > 1.5) other.canSwap = canSwap = true;
        if (distance <= 0.5 && canSwap) {
            chaser = !chaser;
            other.chaser = !other.chaser;
            other.canSwap = canSwap = false;
        }
        if (chaser) {
            if (animal.getNavigation().isDone()) animal.getNavigation().moveTo(mate, 1.0);
        } else if (animal.getNavigation().isDone()) {
            double dx = animal.getX() - mate.getX();
            double dz = animal.getZ() - mate.getZ();
            double length = Math.sqrt(dx * dx + dz * dz);
            if (length < 0.001) {
                dx = 1;
                dz = 0;
                length = 1;
            }
            double distanceAway = 8 + animal.getRandom().nextDouble() * 4;
            Vec3 target = animal.position().add(dx / length * distanceAway, 0, dz / length * distanceAway);
            animal.getNavigation().moveTo(target.x, target.y, target.z, 1.0);
        }
    }

    @Override public void stop() {
        JuvenilePlayGoal other = mate == null ? null : (JuvenilePlayGoal) mateGoal(mate);
        active = started = false;
        mate = null;
        animal.getNavigation().stop();
        if (other != null && other.mate == animal) {
            other.active = other.started = false;
            other.mate = null;
        }
    }

    private boolean available(Mob candidate) {
        return candidate != animal && eligible(candidate)
                && !((JuvenilePlayGoal) mateGoal(candidate)).active;
    }

    private static boolean eligible(Mob mob) {
        return mob.isBaby() && !asleep(mob) && !mob.isLeashed() && !mob.isVehicle() && !mob.isPassenger()
                && !mob.isInWater() && mob.getTarget() == null
                && !((net.minecraft.world.entity.TamableAnimal) mob).isOrderedToSit();
    }

    private static boolean asleep(Mob mob) {
        return mob.isSleeping() || ((AnimalSleepState) mob).animaniax$isSleeping();
    }

    private static Goal mateGoal(Mob mob) {
        return mob instanceof net.syrupstudios.animaniax.entity.DogEntity dog ? dog.animaniax$getPlayGoal()
                : ((net.syrupstudios.animaniax.entity.CompanionCatEntity) mob).animaniax$getPlayGoal();
    }
}
