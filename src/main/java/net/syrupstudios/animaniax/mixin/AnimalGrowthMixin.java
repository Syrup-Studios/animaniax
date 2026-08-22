package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalProfile.Capability;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
abstract class AnimalGrowthMixin {
    @Unique private static final long ANIMANIAX$VANILLA_BABY_AGE = 24_000L;
    @Unique private int animaniax$growthPhase;
    @Unique private int animaniax$ageRemainder;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveGrowthState(CompoundTag tag, CallbackInfo ci) {
        AgeableMob animal = (AgeableMob) (Object) this;
        if (animal instanceof Animal && AnimalProfile.has(animal.getType(), Capability.GROWTH)) {
            tag.putInt("AnimaniaxGrowthPhase", animaniax$growthPhase);
            tag.putInt("AnimaniaxAgeRemainder", animaniax$ageRemainder);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadGrowthState(CompoundTag tag, CallbackInfo ci) {
        AgeableMob animal = (AgeableMob) (Object) this;
        if (!(animal instanceof Animal) || !AnimalProfile.has(animal.getType(), Capability.GROWTH)) return;
        animaniax$growthPhase = tag.contains("AnimaniaxGrowthPhase")
                ? Math.max(0, Math.min(AnimalCareConfig.childGrowthTick, tag.getInt("AnimaniaxGrowthPhase"))) : 0;
        animaniax$ageRemainder = tag.contains("AnimaniaxAgeRemainder")
                ? Math.floorMod(tag.getInt("AnimaniaxAgeRemainder"), 85) : 0;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"))
    private void animaniax$updateBabyGrowth(AgeableMob animal, int age) {
        if (animal instanceof Animal && AnimalProfile.has(animal.getType(), Capability.GROWTH) && animal.getAge() < 0) {
            if (animaniax$growthPhase < AnimalCareConfig.childGrowthTick) animaniax$growthPhase++;
            if (animaniax$growthPhase >= AnimalCareConfig.childGrowthTick
                    && !((AnimalSleepState) animal).animaniax$isSleeping()
                    && ((AnimalCareState) animal).animaniax$hasGrowthCare()) {
                long stepNumerator = ANIMANIAX$VANILLA_BABY_AGE + animaniax$ageRemainder;
                long ageSteps = stepNumerator / 85L;
                animaniax$ageRemainder = (int) (stepNumerator % 85L);
                animaniax$growthPhase = 0;
                age = (int) Math.min(0L, (long) animal.getAge() + ageSteps);
                animal.setAge(age);
                return;
            }
            return;
        }
        animal.setAge(age);
    }
}
