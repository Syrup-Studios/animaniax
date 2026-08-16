package net.syrupstudios.animaniax.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.entity.GoatFaintingState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Goat.class)
abstract class GoatFaintingDataMixin implements GoatFaintingState {
    @Unique private static final EntityDataAccessor<Boolean> ANIMANIAX_GOAT_FAINTING =
            SynchedEntityData.defineId(Goat.class, EntityDataSerializers.BOOLEAN);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void animaniax$defineFaintingData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ANIMANIAX_GOAT_FAINTING, false);
    }

    @Override @Unique public boolean animaniax$isFainting() {
        return ((Goat) (Object) this).getEntityData().get(ANIMANIAX_GOAT_FAINTING);
    }

    @Override @Unique public void animaniax$setFainting(boolean fainting) {
        ((Goat) (Object) this).getEntityData().set(ANIMANIAX_GOAT_FAINTING, fainting);
    }
}
