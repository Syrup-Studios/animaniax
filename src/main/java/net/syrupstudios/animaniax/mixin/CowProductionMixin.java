package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUtils;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.MilkContainerTransfer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cow.class)
abstract class CowProductionMixin {
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void animaniax$requireCareForMilk(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Cow cow = (Cow) (Object) this;
        ItemStack held = player.getItemInHand(hand);
        AnimalReproductionState reproduction = (AnimalReproductionState) cow;
        if (!held.is(Items.BUCKET) && !cow.isBaby() && cow.getType() == EntityType.COW) {
            String kind = switch (((CowBreedState) cow).animaniax$getBreed()) {
                case "Friesian" -> "friesian";
                case "Holstein" -> "holstein";
                case "Jersey" -> "jersey";
                default -> "generic";
            };
            if (kind != null && MilkContainerTransfer.canFill(player, hand, kind)) {
                AnimalCareState care = (AnimalCareState) cow;
                if (!care.animaniax$isContent() || !reproduction.animaniax$isFemale()
                        || !reproduction.animaniax$isLactating()) {
                    if (!cow.level().isClientSide) {
                        String message = !reproduction.animaniax$isFemale() ? "Only female cows can produce milk."
                                : !reproduction.animaniax$isLactating() ? "This cow is not lactating."
                                : "This cow is unhappy. Feed and water it before collecting milk.";
                        player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
                    }
                } else if (!cow.level().isClientSide && MilkContainerTransfer.fill(player, hand, kind)) {
                    care.animaniax$milk();
                }
                cir.setReturnValue(InteractionResult.sidedSuccess(cow.level().isClientSide));
                return;
            }
        }
        if (held.is(Items.BUCKET) && !cow.isBaby() && (!((AnimalCareState) cow).animaniax$isContent()
                || !reproduction.animaniax$isFemale() || !reproduction.animaniax$isLactating())) {
            if (!cow.level().isClientSide) {
                String message = !reproduction.animaniax$isFemale() ? "Only female cows can produce milk."
                        : !reproduction.animaniax$isLactating() ? "This cow is not lactating."
                        : "This cow is unhappy. Feed and water it before collecting milk.";
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(cow.level().isClientSide));
        }
    }

    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack animaniax$makeCowThirsty(ItemStack bucket, Player player, ItemStack milk) {
        Cow cow = (Cow) (Object) this;
        ((AnimalCareState) cow).animaniax$milk();
        if (cow.getType() == EntityType.COW) {
            milk = switch (((CowBreedState) cow).animaniax$getBreed()) {
                case "Friesian" -> new ItemStack(AnimalMilkContent.FRIESIAN_MILK_BUCKET);
                case "Holstein" -> new ItemStack(AnimalMilkContent.HOLSTEIN_MILK_BUCKET);
                case "Jersey" -> new ItemStack(AnimalMilkContent.JERSEY_MILK_BUCKET);
                default -> milk;
            };
        }
        return ItemUtils.createFilledResult(bucket, player, milk);
    }
}
