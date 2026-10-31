package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;

public final class HoneyFluidBlock extends LiquidBlock {
    public HoneyFluidBlock(FlowingFluid fluid, Properties properties) { super(fluid, properties); }

    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hit) {
        if (!stack.is(Items.GLASS_BOTTLE) || !state.getFluidState().isSource()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            ItemStack bottle = new ItemStack(BeeHiveContent.HONEY_BOTTLE);
            if (stack.isEmpty()) player.setItemInHand(hand, bottle);
            else if (!player.getInventory().add(bottle)) player.drop(bottle, false);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        Vec3 flow = state.getFluidState().getFlow(level, pos);
        entity.setDeltaMovement(entity.getDeltaMovement().add(flow.x / 2000.0, flow.y / 2000.0, flow.z / 2000.0));
        if (entity instanceof LivingEntity living) living.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                net.minecraft.world.effect.MobEffects.REGENERATION, 1, 0, false, false));
    }
}
