package net.syrupstudios.animaniax.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.block.SeedPileContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class SeedPilePlacementMixin {
    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void animaniax$placeSeedPile(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = context.getItemInHand();
        Block pile = stack.is(Items.WHEAT_SEEDS) ? SeedPileContent.WHEAT
                : stack.is(Items.PUMPKIN_SEEDS) ? SeedPileContent.PUMPKIN
                : stack.is(Items.MELON_SEEDS) ? SeedPileContent.MELON
                : stack.is(Items.BEETROOT_SEEDS) ? SeedPileContent.BEETROOT : null;
        if (pile == null) return;
        if (AnimalCareConfig.shiftSeedPlacement
                && (context.getPlayer() == null || !context.getPlayer().isShiftKeyDown())) return;
        BlockPos clicked = context.getClickedPos();
        BlockPos target = context.getLevel().getBlockState(clicked).canBeReplaced()
                ? clicked : clicked.relative(context.getClickedFace());
        BlockPos support = target.below();
        if (!SeedPileContent.canPlace(context.getLevel(), support)) return;
        if (!context.getLevel().isClientSide) {
            if (!SeedPileContent.place(context.getLevel(), support, pile)) return;
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) stack.shrink(1);
        }
        cir.setReturnValue(InteractionResult.sidedSuccess(context.getLevel().isClientSide));
    }
}
