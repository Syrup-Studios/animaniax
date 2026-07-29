package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class RabbitBreedMixin implements RabbitBreedState {
    @Unique private static final String[] ANIMANIAX$BREEDS = {
            "Lop", "Rex", "Dutch", "Havana", "New Zealand", "Jack", "Cottontail", "Chinchilla"
    };
    @Unique private String animaniax$rabbitBreed = "Cottontail";

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() != EntityType.RABBIT) return;
        tag.putString("AnimaniaxRabbitBreed", animaniax$rabbitBreed);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadRabbitBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() == EntityType.RABBIT && tag.contains("AnimaniaxRabbitBreed")) {
            animaniax$setRabbitBreed(tag.getString("AnimaniaxRabbitBreed"));
        }
    }

    @Override @Unique public String animaniax$getRabbitBreed() { return animaniax$rabbitBreed; }

    @Override @Unique public void animaniax$setRabbitBreed(String breed) {
        for (String validBreed : ANIMANIAX$BREEDS) {
            if (validBreed.equals(breed)) {
                animaniax$rabbitBreed = validBreed;
                return;
            }
        }
        animaniax$rabbitBreed = "Cottontail";
    }

}
