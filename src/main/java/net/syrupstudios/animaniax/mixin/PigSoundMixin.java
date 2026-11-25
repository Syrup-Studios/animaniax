package net.syrupstudios.animaniax.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Pig;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
abstract class PigSoundMixin {
    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$ambientSound(CallbackInfoReturnable<SoundEvent> cir) {
        Pig pig = (Pig) (Object) this;
        cir.setReturnValue(AnimaniaSounds.pickBySex(pig, pig.isBaby(),
                AnimaniaSounds.PIGLET_AMBIENT, AnimaniaSounds.PIG_AMBIENT, AnimaniaSounds.HOG_AMBIENT));
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        Pig pig = (Pig) (Object) this;
        cir.setReturnValue(pig.isBaby() ? AnimaniaSounds.PIGLET_HURT : AnimaniaSounds.PIG_HURT);
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
        Pig pig = (Pig) (Object) this;
        cir.setReturnValue(pig.isBaby() ? AnimaniaSounds.PIGLET_HURT : AnimaniaSounds.PIG_HURT);
    }
}
