package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.animaniax.entity.RabbitKillerState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Rabbit.class)
abstract class KillerRabbitMixin {
    @Unique private KillerPlayerTargetGoal animaniax$killerTargetGoal;
    @Unique private KillerMeleeGoal animaniax$killerMeleeGoal;
    @Unique private boolean animaniax$killerGoalsActive;

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void animaniax$createKillerGoals(CallbackInfo ci) {
        Rabbit rabbit = (Rabbit) (Object) this;
        animaniax$killerTargetGoal = new KillerPlayerTargetGoal(rabbit);
        animaniax$killerMeleeGoal = new KillerMeleeGoal(rabbit);
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void animaniax$syncKillerMode(CallbackInfo ci) {
        Rabbit rabbit = (Rabbit) (Object) this;
        if (rabbit.level().isClientSide || animaniax$killerTargetGoal == null || animaniax$killerMeleeGoal == null) return;

        boolean namedKiller = rabbit.hasCustomName() && "Killer".equals(rabbit.getCustomName().getString());
        RabbitKillerState state = (RabbitKillerState) rabbit;
        AttributeInstance maxHealth = rabbit.getAttribute(Attributes.MAX_HEALTH);
        AttributeInstance attackDamage = rabbit.getAttribute(Attributes.ATTACK_DAMAGE);
        if (maxHealth == null || attackDamage == null) return;

        if (namedKiller) {
            if (!state.animaniax$areKillerStatsBoosted()) {
                state.animaniax$setPreKillerMaxHealth(maxHealth.getBaseValue());
                state.animaniax$setPreKillerAttackDamage(attackDamage.getBaseValue());
                state.animaniax$setKillerStatsBoosted(true);
                maxHealth.setBaseValue(50.0);
                attackDamage.setBaseValue(5.0);
                rabbit.setHealth(50.0F);
            }
            if (!animaniax$killerGoalsActive) {
                MobGoalSelectorAccessor selectors = (MobGoalSelectorAccessor) (Mob) rabbit;
                selectors.animaniax$getTargetSelector().addGoal(1, animaniax$killerTargetGoal);
                selectors.animaniax$getGoalSelector().addGoal(3, animaniax$killerMeleeGoal);
                animaniax$killerGoalsActive = true;
            }
        } else {
            if (animaniax$killerGoalsActive) {
                MobGoalSelectorAccessor selectors = (MobGoalSelectorAccessor) (Mob) rabbit;
                selectors.animaniax$getTargetSelector().removeGoal(animaniax$killerTargetGoal);
                selectors.animaniax$getGoalSelector().removeGoal(animaniax$killerMeleeGoal);
                animaniax$killerGoalsActive = false;
            }
            if (state.animaniax$areKillerStatsBoosted()) {
                maxHealth.setBaseValue(state.animaniax$getPreKillerMaxHealth());
                attackDamage.setBaseValue(state.animaniax$getPreKillerAttackDamage());
                rabbit.setHealth(Math.min(rabbit.getHealth(), rabbit.getMaxHealth()));
                state.animaniax$setKillerStatsBoosted(false);
                state.animaniax$setPreKillerMaxHealth(0.0);
                state.animaniax$setPreKillerAttackDamage(0.0);
            }
        }
    }

    @Unique
    private static boolean animaniax$isNamedKiller(Rabbit rabbit) {
        return rabbit.hasCustomName() && "Killer".equals(rabbit.getCustomName().getString());
    }

    @Unique
    private static final class KillerPlayerTargetGoal extends NearestAttackableTargetGoal<Player> {
        private final Rabbit rabbit;

        private KillerPlayerTargetGoal(Rabbit rabbit) {
            super(rabbit, Player.class, true);
            this.rabbit = rabbit;
        }

        @Override public boolean canUse() {
            return animaniax$isNamedKiller(rabbit) && super.canUse();
        }

        @Override public boolean canContinueToUse() {
            return animaniax$isNamedKiller(rabbit) && super.canContinueToUse();
        }
    }

    @Unique
    private static final class KillerMeleeGoal extends MeleeAttackGoal {
        private final Rabbit rabbit;

        private KillerMeleeGoal(Rabbit rabbit) {
            super(rabbit, 1.4, true);
            this.rabbit = rabbit;
        }

        @Override public boolean canUse() {
            return animaniax$isNamedKiller(rabbit) && super.canUse();
        }

        @Override public boolean canContinueToUse() {
            return animaniax$isNamedKiller(rabbit) && super.canContinueToUse();
        }
    }
}
