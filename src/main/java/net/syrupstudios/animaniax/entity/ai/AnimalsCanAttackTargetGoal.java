package net.syrupstudios.animaniax.entity.ai;

import java.util.function.Predicate;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class AnimalsCanAttackTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
    public AnimalsCanAttackTargetGoal(Mob mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    public AnimalsCanAttackTargetGoal(Mob mob, Class<T> targetType, int interval, boolean mustSee, boolean mustReach,
            Predicate<LivingEntity> targetPredicate) {
        super(mob, targetType, interval, mustSee, mustReach, targetPredicate);
    }

    @Override public boolean canUse() {
        return AnimalCareConfig.animalsCanAttackOthers && super.canUse();
    }

    @Override public boolean canContinueToUse() {
        return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse();
    }
}
