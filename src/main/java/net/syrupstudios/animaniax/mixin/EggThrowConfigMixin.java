package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EggItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.item.BrownEggContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EggItem.class)
abstract class EggThrowConfigMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void animaniax$respectEggThrowConfig(Level level, Player player, InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> callback) {
        Item item = (Item) (Object) this;
        if (!level.isClientSide && !AnimalCareConfig.allowEggThrowing
                && (item == Items.EGG || item == BrownEggContent.BROWN_EGG)) {
            callback.setReturnValue(InteractionResultHolder.fail(player.getItemInHand(hand)));
        }
    }
}
