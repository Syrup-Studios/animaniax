package net.syrupstudios.animaniax.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;

public final class ToadEntity extends Frog {
    public ToadEntity(EntityType<? extends Frog> type, Level level) { super(type, level); }

    @Override protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new AmphibianAvoidPlayerGoal(this));
        goalSelector.addGoal(5, new AmphibianAvoidPredatorGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Frog.createAttributes().add(Attributes.SCALE, 1.3);
    }

    @Override protected SoundEvent getAmbientSound() { return AnimaniaSounds.TOAD_AMBIENT; }
    @Override protected SoundEvent getHurtSound(DamageSource source) { return null; }
    @Override protected SoundEvent getDeathSound() { return null; }

    @Override public boolean isFood(net.minecraft.world.item.ItemStack stack) { return false; }
    @Override public boolean canFallInLove() { return false; }
    @Override public boolean canMate(Animal other) { return false; }
    @Override public Frog getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }
}
