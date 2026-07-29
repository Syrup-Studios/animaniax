package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MushroomCow.class)
abstract class MooshroomShearingMixin {
    @Redirect(method = "shear", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean animaniax$preserveCowState(Level level, Entity entity) {
        if (entity instanceof Cow cow) {
            AnimalReproductionState original = (AnimalReproductionState) this;
            AnimalReproductionState replacement = (AnimalReproductionState) cow;
            replacement.animaniax$setFemale(original.animaniax$isFemale());
            ((CowBreedState) cow).animaniax$setBreed("Friesian");
        }
        return level.addFreshEntity(entity);
    }
}
