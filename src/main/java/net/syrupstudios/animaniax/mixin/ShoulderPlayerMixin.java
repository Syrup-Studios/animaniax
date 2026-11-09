package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.syrupstudios.animaniax.entity.ShoulderCarryState.ShoulderCarrier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class ShoulderPlayerMixin implements ShoulderCarrier {
    @Unique private static final EntityDataAccessor<String> animaniax$visualType =
            SynchedEntityData.defineId(Player.class, EntityDataSerializers.STRING);
    @Unique private CompoundTag animaniax$shoulderData = new CompoundTag();
    @Unique private Entity animaniax$renderPet;
    @Unique private String animaniax$renderPetType = "";

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void animaniax$defineShoulderData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(animaniax$visualType, "");
    }

    @Override public CompoundTag animaniax$getShoulderData() { return animaniax$shoulderData; }
    @Override public String animaniax$getVisualType() { return ((Player) (Object) this).getEntityData().get(animaniax$visualType); }
    @Override public Entity animaniax$getRenderPet() { return animaniax$renderPet; }
    @Override public String animaniax$getRenderPetType() { return animaniax$renderPetType; }
    @Override public void animaniax$setRenderPet(String type, Entity pet) { animaniax$renderPetType = type; animaniax$renderPet = pet; }
    @Override public void animaniax$setShoulderData(CompoundTag data) {
        animaniax$shoulderData = data.copy();
        String id = data.getString("id");
        ((Player) (Object) this).getEntityData().set(animaniax$visualType, id);
    }
}
