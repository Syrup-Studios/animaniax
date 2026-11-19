package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
abstract class CowFlowerBreedingMixin {
    @Inject(method = "isFood", at = @At("RETURN"), cancellable = true)
    private void animaniax$acceptFlowerBreedingFood(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (AnimalProfile.acceptsFlowerBreedingFood(((Cow) (Object) this).getType(), stack)) cir.setReturnValue(true);
    }
}
