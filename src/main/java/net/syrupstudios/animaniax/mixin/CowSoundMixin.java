package net.syrupstudios.animaniax.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Cow;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
abstract class CowSoundMixin {
    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$ambientSound(CallbackInfoReturnable<SoundEvent> cir) {
        Cow cow = (Cow) (Object) this;
        cir.setReturnValue(AnimaniaSounds.pickBySex(cow, cow.isBaby(),
                AnimaniaSounds.CALF_AMBIENT, AnimaniaSounds.COW_AMBIENT, AnimaniaSounds.BULL_AMBIENT));
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        Cow cow = (Cow) (Object) this;
        cir.setReturnValue(AnimaniaSounds.pickBySex(cow, cow.isBaby(),
                AnimaniaSounds.CALF_HURT, AnimaniaSounds.COW_HURT, AnimaniaSounds.BULL_ANGRY));
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
        Cow cow = (Cow) (Object) this;
        cir.setReturnValue(cow.isBaby() ? AnimaniaSounds.CALF_HURT : AnimaniaSounds.COW_DEATH);
    }
}
