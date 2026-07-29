package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeItem;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import net.syrupstudios.animaniax.block.SheepWoolContent;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.MilkContainerTransfer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Sheep.class)
abstract class SheepProductionMixin {
    @Inject(method = "shear", at = @At("HEAD"), cancellable = true)
    private void animaniax$dropBreedWool(SoundSource soundSource, CallbackInfo ci) {
        Sheep sheep = (Sheep) (Object) this;
        if (sheep.level().isClientSide || sheep.isBaby() || sheep.isSheared()
                || !(sheep instanceof SheepBreedState breedState)) return;
        net.minecraft.world.item.Item wool = SheepWoolContent.forShearing(sheep, breedState);
        if (wool == null) return;
        sheep.level().playSound(null, sheep, SoundEvents.SHEEP_SHEAR, soundSource, 1.0F, 1.0F);
        sheep.setSheared(true);
        sheep.spawnAtLocation(new ItemStack(wool, sheep.getRandom().nextInt(2) + 1));
        ci.cancel();
    }

    @Inject(method = "ate", at = @At("HEAD"))
    private void animaniax$restoreCareFromGrazing(CallbackInfo ci) {
        ((AnimalCareState) this).animaniax$graze();
    }

    @Redirect(method = "ate", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/entity/animal/Sheep;setSheared(Z)V"))
    private void animaniax$keepWoolShearedUntilTimer(Sheep sheep, boolean sheared) {
        if (sheared) sheep.setSheared(true);
    }

    @Inject(method = "setSheared", at = @At("TAIL"))
    private void animaniax$setWoolRegrowthTimer(boolean sheared, CallbackInfo ci) {
        Sheep sheep = (Sheep) (Object) this;
        if (sheep.level().isClientSide || !(sheep instanceof SheepBreedState state)) return;
        if (sheared) state.animaniax$startWoolRegrowthTimer();
        else state.animaniax$setWoolRegrowthTimer(0);
    }

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void animaniax$tickWoolRegrowth(CallbackInfo ci) {
        Sheep sheep = (Sheep) (Object) this;
        if (sheep.level().isClientSide || !sheep.isSheared() || !(sheep instanceof SheepBreedState state)) return;
        int remaining = state.animaniax$getWoolRegrowthTimer();
        if (remaining <= 1) sheep.setSheared(false);
        else state.animaniax$setWoolRegrowthTimer(remaining - 1);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void animaniax$milkEwe(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Sheep sheep = (Sheep) (Object) this;
        ItemStack bucket = player.getItemInHand(hand);
        SheepBreedState breedState = (SheepBreedState) sheep;
        if (bucket.getItem() instanceof DyeItem && !sheep.isBaby() && !sheep.isSheared()
                && !SheepWoolContent.canDye(breedState)) {
            cir.setReturnValue(InteractionResult.SUCCESS_NO_ITEM_USED);
            return;
        }
        if (!bucket.is(Items.BUCKET) && !sheep.isBaby() && MilkContainerTransfer.canFill(player, hand, "sheep")) {
            AnimalReproductionState reproduction = (AnimalReproductionState) sheep;
            AnimalCareState care = (AnimalCareState) sheep;
            if (!care.animaniax$isContent() || !reproduction.animaniax$isFemale()
                    || !reproduction.animaniax$isLactating()) {
                if (!sheep.level().isClientSide) {
                    String reason = !reproduction.animaniax$isFemale() ? "Only female sheep can produce milk."
                            : !reproduction.animaniax$isLactating() ? "This sheep is not lactating."
                            : "This sheep is unhappy. Feed and water it before collecting milk.";
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(reason));
                }
            } else if (!sheep.level().isClientSide && MilkContainerTransfer.fill(player, hand, "sheep")) {
                care.animaniax$milk();
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(sheep.level().isClientSide));
            return;
        }
        if (!bucket.is(Items.BUCKET) || sheep.isBaby()) return;

        AnimalReproductionState reproduction = (AnimalReproductionState) sheep;
        AnimalCareState care = (AnimalCareState) sheep;
        if (!care.animaniax$isContent() || !reproduction.animaniax$isFemale()
                || !reproduction.animaniax$isLactating()) {
            if (!sheep.level().isClientSide) {
                String reason = !reproduction.animaniax$isFemale() ? "Only female sheep can produce milk."
                        : !reproduction.animaniax$isLactating() ? "This sheep is not lactating."
                        : "This sheep is unhappy. Feed and water it before collecting milk.";
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(reason));
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(sheep.level().isClientSide));
            return;
        }

        if (!sheep.level().isClientSide) {
            care.animaniax$milk();
            player.setItemInHand(hand, ItemUtils.createFilledResult(bucket, player,
                    new ItemStack(AnimalMilkContent.SHEEP_MILK_BUCKET)));
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(sheep.level().isClientSide));
    }
}
