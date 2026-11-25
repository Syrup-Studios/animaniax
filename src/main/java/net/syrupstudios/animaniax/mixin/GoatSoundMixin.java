package net.syrupstudios.animaniax.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Goat.class)
abstract class GoatSoundMixin {
    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$ambientSound(CallbackInfoReturnable<SoundEvent> cir) {
        Goat goat = (Goat) (Object) this;
        cir.setReturnValue(goat.isBaby() ? AnimaniaSounds.KID_AMBIENT : AnimaniaSounds.GOAT_AMBIENT);
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        Goat goat = (Goat) (Object) this;
        cir.setReturnValue(goat.isBaby() ? AnimaniaSounds.KID_HURT : AnimaniaSounds.GOAT_HURT);
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
        Goat goat = (Goat) (Object) this;
        cir.setReturnValue(goat.isBaby() ? AnimaniaSounds.KID_HURT : AnimaniaSounds.GOAT_HURT);
    }
}
