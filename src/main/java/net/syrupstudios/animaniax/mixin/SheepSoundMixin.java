package net.syrupstudios.animaniax.mixin;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.Sheep;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
abstract class SheepSoundMixin {
    @Inject(method = "getAmbientSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$ambientSound(CallbackInfoReturnable<SoundEvent> cir) {
        Sheep sheep = (Sheep) (Object) this;
        cir.setReturnValue(sheep.isBaby() ? AnimaniaSounds.LAMB_AMBIENT : AnimaniaSounds.SHEEP_AMBIENT);
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$hurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> cir) {
        Sheep sheep = (Sheep) (Object) this;
        cir.setReturnValue(sheep.isBaby() ? AnimaniaSounds.LAMB_HURT : AnimaniaSounds.SHEEP_HURT);
    }

    @Inject(method = "getDeathSound", at = @At("HEAD"), cancellable = true)
    private void animaniax$deathSound(CallbackInfoReturnable<SoundEvent> cir) {
        Sheep sheep = (Sheep) (Object) this;
        cir.setReturnValue(sheep.isBaby() ? AnimaniaSounds.LAMB_HURT : AnimaniaSounds.SHEEP_HURT);
    }
}
