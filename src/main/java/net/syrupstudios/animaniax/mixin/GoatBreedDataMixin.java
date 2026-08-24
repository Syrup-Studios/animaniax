package net.syrupstudios.animaniax.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.entity.GoatBreedSyncedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Goat.class)
abstract class GoatBreedDataMixin implements GoatBreedSyncedState {
    @Unique private static final EntityDataAccessor<String> ANIMANIAX$GOAT_BREED =
            SynchedEntityData.defineId(Goat.class, EntityDataSerializers.STRING);
    @Unique private static final EntityDataAccessor<Boolean> ANIMANIAX$GOAT_SHEARED =
            SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void animaniax$defineGoatBreedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ANIMANIAX$GOAT_BREED, "Alpine");
        builder.define(ANIMANIAX$GOAT_SHEARED, false);
    }

    @Override @Unique public String animaniax$getSyncedGoatBreed() {
        return ((Goat) (Object) this).getEntityData().get(ANIMANIAX$GOAT_BREED);
    }

    @Override @Unique public void animaniax$setSyncedGoatBreed(String breed) {
        ((Goat) (Object) this).getEntityData().set(ANIMANIAX$GOAT_BREED, breed);
    }

    @Override @Unique public boolean animaniax$getSyncedGoatSheared() {
        return ((Goat) (Object) this).getEntityData().get(ANIMANIAX$GOAT_SHEARED);
    }

    @Override @Unique public void animaniax$setSyncedGoatSheared(boolean sheared) {
        ((Goat) (Object) this).getEntityData().set(ANIMANIAX$GOAT_SHEARED, sheared);
    }
}
