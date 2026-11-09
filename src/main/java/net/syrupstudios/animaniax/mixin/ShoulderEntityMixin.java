package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.animaniax.entity.ShoulderCarryState.ShoulderCarrier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class ShoulderEntityMixin {
    private static final String KEY = "AnimaniaxShoulderAnimal";

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveShoulder(CompoundTag tag, CallbackInfo ci) {
        CompoundTag data = ((ShoulderCarrier) this).animaniax$getShoulderData();
        if (!data.isEmpty()) tag.put(KEY, data.copy());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadShoulder(CompoundTag tag, CallbackInfo ci) {
        ((ShoulderCarrier) this).animaniax$setShoulderData(tag.getCompound(KEY));
    }
}
