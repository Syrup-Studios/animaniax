package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.syrupstudios.animaniax.entity.AnimalVisualSyncState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
abstract class CowBreedMixin implements CowBreedState {
    @Unique private static final String[] ANIMANIAX$BREEDS = {"Angus", "Hereford", "Texas Longhorn", "Holstein", "Friesian", "Jersey", "Highland"};
    @Unique private String animaniax$breed = "Friesian";

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveBreed(CompoundTag tag, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (mob.getType() == EntityType.COW) tag.putString("AnimaniaxCowBreed", animaniax$breed);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadBreed(CompoundTag tag, CallbackInfo ci) {
        if (((Mob) (Object) this).getType() == EntityType.COW && tag.contains("AnimaniaxCowBreed")) {
            animaniax$setBreed(tag.getString("AnimaniaxCowBreed"));
        }
    }

    @Override
    @Unique
    public String animaniax$getBreed() {
        Mob mob = (Mob) (Object) this;
        if (mob.level().isClientSide) {
            String synced = ((AnimalVisualSyncState) mob).animaniax$getSyncedBreed();
            if (!synced.isEmpty()) return synced;
        }
        return animaniax$breed;
    }

    @Override
    @Unique
    public void animaniax$setBreed(String breed) {
        for (int i = 0; i < ANIMANIAX$BREEDS.length; i++) {
            if (ANIMANIAX$BREEDS[i].equals(breed)) {
                animaniax$breed = ANIMANIAX$BREEDS[i];
                Mob mob = (Mob) (Object) this;
                if (!mob.level().isClientSide && mob.getType() == EntityType.COW) {
                    ((AnimalVisualSyncState) mob).animaniax$setSyncedBreed(animaniax$breed);
                }
                return;
            }
        }
    }
}
