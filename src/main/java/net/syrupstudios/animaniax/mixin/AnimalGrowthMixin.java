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
    @Unique private static final long ANIMANIAX$GROWTH_STEPS = 85L;
    @Unique private static final long ANIMANIAX$VANILLA_BABY_AGE = 24_000L;
    @Unique private long animaniax$growthRemainder;

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveGrowthRemainder(CompoundTag tag, CallbackInfo ci) {
        AgeableMob animal = (AgeableMob) (Object) this;
        if (animal instanceof Animal && AnimalProfile.has(animal.getType(), Capability.GROWTH))
            tag.putLong("AnimaniaxGrowthRemainder", animaniax$growthRemainder);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadGrowthRemainder(CompoundTag tag, CallbackInfo ci) {
        AgeableMob animal = (AgeableMob) (Object) this;
        if (!(animal instanceof Animal) || !AnimalProfile.has(animal.getType(), Capability.GROWTH)) return;
        long denominator = ANIMANIAX$GROWTH_STEPS * AnimalCareConfig.childGrowthTick;
        animaniax$growthRemainder = tag.contains("AnimaniaxGrowthRemainder")
                ? Math.floorMod(tag.getLong("AnimaniaxGrowthRemainder"), denominator) : 0L;
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"))
    private void animaniax$updateBabyGrowth(AgeableMob animal, int age) {
        if (animal instanceof Animal && AnimalProfile.has(animal.getType(), Capability.GROWTH) && animal.getAge() < 0) {
            if (((AnimalSleepState) animal).animaniax$isSleeping() || !((AnimalCareState) animal).animaniax$hasGrowthCare()) return;
            long denominator = ANIMANIAX$GROWTH_STEPS * AnimalCareConfig.childGrowthTick;
            long accumulated = animaniax$growthRemainder + ANIMANIAX$VANILLA_BABY_AGE;
            long ageSteps = accumulated / denominator;
            animaniax$growthRemainder = accumulated % denominator;
            age = (int) Math.min(0L, (long) animal.getAge() + ageSteps);
        }
        animal.setAge(age);
    }
}
