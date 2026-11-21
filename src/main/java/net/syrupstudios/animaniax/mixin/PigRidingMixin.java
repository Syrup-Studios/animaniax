package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
abstract class PigRidingMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void animaniax$mountWithoutSaddle(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Pig pig = (Pig) (Object) this;
        if (pig.isBaby() || pig.isSaddled() || pig.isVehicle()
                || !(pig instanceof AnimalCareState care) || care.animaniax$needsGraze()
                || care.animaniax$needsWater() || !player.getItemInHand(hand).is(Items.CARROT_ON_A_STICK)) return;
        if (pig.level().isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else if (player.startRiding(pig)) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Inject(method = "getControllingPassenger", at = @At("RETURN"), cancellable = true)
    private void animaniax$allowCarrotStickSteering(CallbackInfoReturnable<net.minecraft.world.entity.LivingEntity> cir) {
        Pig pig = (Pig) (Object) this;
        if (cir.getReturnValue() != null || pig.isBaby()
                || !(pig.getFirstPassenger() instanceof Player rider)
                || !(pig instanceof AnimalCareState care) || care.animaniax$needsGraze()
                || care.animaniax$needsWater()) return;
        if (rider.getMainHandItem().is(Items.CARROT_ON_A_STICK)
                || rider.getOffhandItem().is(Items.CARROT_ON_A_STICK)) cir.setReturnValue(rider);
    }
}
