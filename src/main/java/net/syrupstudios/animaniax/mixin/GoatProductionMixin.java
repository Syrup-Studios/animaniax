package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.MilkContainerTransfer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Goat.class)
abstract class GoatProductionMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void animaniax$milkGoat(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Goat goat = (Goat) (Object) this;
        ItemStack bucket = player.getItemInHand(hand);
        if (player.getItemInHand(hand).is(Items.SHEARS) && !goat.isBaby()
                && "Angora".equals(((GoatBreedState) goat).animaniax$getGoatBreed())
                && !((GoatBreedState) goat).animaniax$isSheared()) {
            if (!goat.level().isClientSide) {
                ((GoatBreedState) goat).animaniax$setSheared(true);
                ((AnimalSleepState) goat).animaniax$setSleeping(false);
                goat.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
                goat.spawnAtLocation(new ItemStack(Items.WHITE_WOOL, goat.getRandom().nextInt(2) + 1), 1.0F);
                player.getItemInHand(hand).hurtAndBreak(1, player,
                        hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(goat.level().isClientSide));
            return;
        }
        if (!bucket.is(Items.BUCKET) && !goat.isBaby() && MilkContainerTransfer.canFill(player, hand, "goat")) {
            AnimalReproductionState reproduction = (AnimalReproductionState) goat;
            AnimalCareState care = (AnimalCareState) goat;
            if (!care.animaniax$isContent() || !reproduction.animaniax$isFemale()
                    || !reproduction.animaniax$isLactating()) {
                if (!goat.level().isClientSide) {
                    String reason = !reproduction.animaniax$isFemale() ? "Only female goats can produce milk."
                            : !reproduction.animaniax$isLactating() ? "This goat is not lactating."
                            : "This goat is unhappy. Feed and water it before collecting milk.";
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(reason));
                }
            } else if (!goat.level().isClientSide && MilkContainerTransfer.fill(player, hand, "goat")) {
                care.animaniax$milk();
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(goat.level().isClientSide));
            return;
        }
        if (!bucket.is(Items.BUCKET) || goat.isBaby()) return;

        AnimalReproductionState reproduction = (AnimalReproductionState) goat;
        AnimalCareState care = (AnimalCareState) goat;
        if (!care.animaniax$isContent() || !reproduction.animaniax$isFemale()
                || !reproduction.animaniax$isLactating()) {
            if (!goat.level().isClientSide) {
                String reason = !reproduction.animaniax$isFemale() ? "Only female goats can produce milk."
                        : !reproduction.animaniax$isLactating() ? "This goat is not lactating."
                        : "This goat is unhappy. Feed and water it before collecting milk.";
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(reason));
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(goat.level().isClientSide));
            return;
        }

        if (!goat.level().isClientSide) {
            care.animaniax$milk();
            player.setItemInHand(hand, ItemUtils.createFilledResult(bucket, player,
                    new ItemStack(AnimalMilkContent.GOAT_MILK_BUCKET)));
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(goat.level().isClientSide));
    }
}
