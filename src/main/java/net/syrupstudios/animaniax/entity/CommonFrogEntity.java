package net.syrupstudios.animaniax.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.jetbrains.annotations.Nullable;

public final class CommonFrogEntity extends Frog {
    private static final ResourceKey<DamageType> PEPE = ResourceKey.create(Registries.DAMAGE_TYPE,
            net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("animaniax", "pepe"));
    private boolean pepeModeApplied;
    private double previousMaxHealthBase = 10.0D;
    @Nullable private LivingEntity target;

    public CommonFrogEntity(EntityType<? extends Frog> type, Level level) { super(type, level); }

    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new AmphibianAvoidPlayerGoal(this));
        goalSelector.addGoal(5, new AmphibianAvoidPredatorGoal(this));
        goalSelector.addGoal(1, new LeapAtTargetGoal(this, 0.5F) {
            @Override public boolean canUse() { return isPepe() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isPepe() && super.canContinueToUse(); }
        });
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0D, true) {
            @Override public boolean canUse() { return isPepe() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isPepe() && super.canContinueToUse(); }
        });
        targetSelector.addGoal(1, new HurtByTargetGoal(this) {
            @Override public boolean canUse() { return isPepe() && super.canUse(); }
            @Override public boolean canContinueToUse() { return isPepe() && super.canContinueToUse(); }
        });
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, FerretEntity.class, true,
                target -> isPepe()));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, HedgehogEntity.class, true,
                target -> isPepe()));
    }

    @Override public void setTarget(@Nullable LivingEntity target) {
        this.target = target;
        super.setTarget(target);
    }

    @Override @Nullable public LivingEntity getTarget() {
        return isPepe() ? target : super.getTarget();
    }

    public boolean isPepe() {
        return hasCustomName() && "Pepe".equals(getCustomName().getString());
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setVariant(BuiltInRegistries.FROG_VARIANT.getHolderOrThrow(
                getRandom().nextBoolean() ? FrogVariant.TEMPERATE : FrogVariant.WARM));
        updatePepeMode();
        return result;
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        target = null;
        super.setTarget(null);
        boolean hasPreviousMaxHealth = tag.contains("AnimaniaxPepePreviousMaxHealth");
        if (hasPreviousMaxHealth)
            previousMaxHealthBase = tag.getDouble("AnimaniaxPepePreviousMaxHealth");
        AttributeInstance maxHealth = getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        if (isPepe()) {
            if (!hasPreviousMaxHealth) {
                previousMaxHealthBase = maxHealth.getBaseValue();
            }
            maxHealth.setBaseValue(20.0D);
            if (!hasPreviousMaxHealth) setHealth(20.0F);
            pepeModeApplied = true;
        } else if (hasPreviousMaxHealth) {
            maxHealth.setBaseValue(previousMaxHealthBase);
            if (getHealth() > maxHealth.getValue()) setHealth((float) maxHealth.getValue());
            pepeModeApplied = false;
        } else {
            pepeModeApplied = false;
        }
    }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (pepeModeApplied)
            tag.putDouble("AnimaniaxPepePreviousMaxHealth", previousMaxHealthBase);
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) updatePepeMode();
    }

    @Override public boolean doHurtTarget(Entity target) {
        if (!isPepe()) return super.doHurtTarget(target);
        DamageSource source = damageSources().source(PEPE);
        boolean hit = target.hurt(source, 2.0F);
        target.hurt(source, 2.0F);
        if (hit && level() instanceof ServerLevel serverLevel)
            EnchantmentHelper.doPostAttackEffects(serverLevel, target, source);
        if (target instanceof Player player)
            player.knockback(1.0D, getX() - target.getX(), getZ() - target.getZ());
        return hit;
    }

    private void updatePepeMode() {
        AttributeInstance maxHealth = getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) return;
        if (isPepe() && !pepeModeApplied) {
            previousMaxHealthBase = maxHealth.getBaseValue();
            maxHealth.setBaseValue(20.0D);
            setHealth(20.0F);
            pepeModeApplied = true;
        } else if (!isPepe() && pepeModeApplied) {
            maxHealth.setBaseValue(previousMaxHealthBase);
            if (getHealth() > maxHealth.getValue()) setHealth((float) maxHealth.getValue());
            target = null;
            super.setTarget(null);
            pepeModeApplied = false;
        }
    }

    @Override protected SoundEvent getAmbientSound() { return AnimaniaSounds.COMMON_FROG_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return null; }
    @Override protected SoundEvent getDeathSound() { return null; }

    @Override public boolean isFood(net.minecraft.world.item.ItemStack stack) { return false; }
    @Override public boolean canFallInLove() { return false; }
    @Override public boolean canMate(net.minecraft.world.entity.animal.Animal other) { return false; }
    @Override public Frog getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }
}
