package net.syrupstudios.animaniax.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.advancement.FeedAnimalTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
abstract class AnimalFeedAdvancementMixin {
    @org.spongepowered.asm.mixin.Unique private ItemStack animaniax$feedItem = ItemStack.EMPTY;

    @Inject(method = "mobInteract", at = @At("HEAD"))
    private void animaniax$triggerFeedAdvancement(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        Animal animal = (Animal) (Object) this;
        var item = player.getItemInHand(hand);
        animaniax$feedItem = player instanceof ServerPlayer && animal.isFood(item) ? item.copy() : ItemStack.EMPTY;
    }

    @Inject(method = "mobInteract", at = @At("RETURN"))
    private void animaniax$triggerSuccessfulFeed(Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResult> cir) {
        if (player instanceof ServerPlayer serverPlayer && !animaniax$feedItem.isEmpty()
                && cir.getReturnValue().consumesAction()) {
            FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, (Animal) (Object) this, animaniax$feedItem);
        }
        animaniax$feedItem = ItemStack.EMPTY;
    }
}
