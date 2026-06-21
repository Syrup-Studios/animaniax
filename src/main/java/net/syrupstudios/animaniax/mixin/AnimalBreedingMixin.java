package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.animal.Animal;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
abstract class AnimalBreedingMixin {
    @Inject(method = "canFallInLove", at = @At("RETURN"), cancellable = true)
    private void animaniax$requireCareForBreeding(CallbackInfoReturnable<Boolean> cir) {
        if (((AnimalCareState) this).animaniax$careBlocksBreeding()
                || ((AnimalSleepState) this).animaniax$isSleeping()) cir.setReturnValue(false);
    }
}
