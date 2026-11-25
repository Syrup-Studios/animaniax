package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;

public final class BullRetaliationGoal extends Goal {
    private final Mob bull;
    private LivingEntity attacker;
    private int lastHurtTimestamp = Integer.MIN_VALUE;
    private int attackCooldown;
    private int retaliationTicks;

    public BullRetaliationGoal(Mob bull) {
        this.bull = bull;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!animaniax$canRetaliate()) return false;
        LivingEntity recentAttacker = bull.getLastHurtByMob();
        if (recentAttacker == null || !recentAttacker.isAlive() || animaniax$isIgnoredPlayer(recentAttacker)
                || bull.getLastHurtByMobTimestamp() == lastHurtTimestamp) return false;
        attacker = recentAttacker;
        lastHurtTimestamp = bull.getLastHurtByMobTimestamp();
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return animaniax$canRetaliate() && attacker != null && attacker.isAlive()
                && !animaniax$isIgnoredPlayer(attacker) && retaliationTicks > 0
                && bull.distanceToSqr(attacker) < 256;
    }

    @Override
    public void start() {
        bull.setTarget(attacker);
        attackCooldown = 0;
        retaliationTicks = 200;
        if (!animaniax$isFemale()) {
            bull.playSound(AnimaniaSounds.BULL_ANGRY, 0.8F, 1.0F);
        }
    }

    @Override
    public void stop() {
        bull.getNavigation().stop();
        if (bull.getTarget() == attacker) bull.setTarget(null);
        attacker = null;
    }

    @Override
    public void tick() {
        if (attacker == null) return;
        retaliationTicks--;
        bull.getLookControl().setLookAt(attacker, 30, 30);
        if (bull.distanceToSqr(attacker) > 4) {
            if (bull.tickCount % 4 == 0) bull.getNavigation().moveTo(attacker, 1.2);
        } else {
            bull.getNavigation().stop();
            if (attackCooldown > 0) attackCooldown--;
            if (attackCooldown == 0 && bull.hasLineOfSight(attacker)) {
                boolean female = animaniax$isFemale();
                if (attacker.hurt(bull.damageSources().mobAttack(bull), female ? 2.0F : 5.0F)
                        && !female && attacker instanceof Player player) {
                    player.knockback(1.0, bull.getX() - player.getX(), bull.getZ() - player.getZ());
                }
                attackCooldown = 20;
            }
        }
    }

    private boolean animaniax$canRetaliate() {
        if (bull.isBaby() || !(bull instanceof AnimalReproductionState sex)) return false;
        return sex.animaniax$isFemale() || (AnimalCareConfig.animalsCanAttackOthers
                && bull instanceof AnimalSterilizationState sterilization
                && !sterilization.animaniax$isSterilized());
    }

    private boolean animaniax$isFemale() {
        return bull instanceof AnimalReproductionState sex && sex.animaniax$isFemale();
    }

    private static boolean animaniax$isIgnoredPlayer(LivingEntity entity) {
        return entity instanceof Player player && (player.isCreative() || player.isSpectator());
    }
}
