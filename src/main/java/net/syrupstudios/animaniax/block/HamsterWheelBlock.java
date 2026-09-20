package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.item.HamsterCarryItem;
import net.syrupstudios.animaniax.item.HamsterFoodContent;

public final class HamsterWheelBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 2, 15), Block.box(2, 2, 2, 14, 14, 14));
    public HamsterWheelBlock(Properties properties) { super(properties); }
    @Override public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) { return SHAPE; }
    @Override public boolean hasAnalogOutputSignal(BlockState state) { return true; }
    @Override public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return level.getBlockEntity(pos) instanceof HamsterWheelBlockEntity wheel && wheel.occupied() ? 15 : 0; }
    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof HamsterWheelBlockEntity wheel)) return InteractionResult.PASS;
        if (!wheel.occupied()) return InteractionResult.PASS;
        if (!level.isClientSide) giveOrDrop(player, wheel.ejectHamster());
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof HamsterWheelBlockEntity wheel)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.is(HamsterFoodContent.HAMSTER_FOOD) && wheel.getItem(0).getCount() < 16) {
            if (!level.isClientSide) { int add = Math.min(stack.getCount(), 16 - wheel.getItem(0).getCount()); wheel.setItem(0, wheel.getItem(0).isEmpty() ? stack.copyWithCount(add) : wheel.getItem(0).copyWithCount(wheel.getItem(0).getCount() + add)); if (!player.getAbilities().instabuild) stack.shrink(add); }
            return ItemInteractionResult.SUCCESS;
        }
        var data = HamsterCarryItem.getData(stack);
        if (data == null || wheel.occupied()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (!level.isClientSide && wheel.insertHamster(data)) { stack.shrink(1); }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
    private static void giveOrDrop(Player player, ItemStack stack) { if (!stack.isEmpty() && !player.getInventory().add(stack)) player.drop(stack, false); }
    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new HamsterWheelBlockEntity(pos, state); }
    @Override @SuppressWarnings("unchecked") public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) { return level.isClientSide || type != HamsterWheelContent.ENTITY ? null : (BlockEntityTicker<T>) (BlockEntityTicker<HamsterWheelBlockEntity>) HamsterWheelBlockEntity::serverTick; }
    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide && level.getBlockEntity(pos) instanceof HamsterWheelBlockEntity wheel) {
            ItemStack carried = wheel.ejectHamster(); if (!carried.isEmpty()) popResource(level, pos, carried);
            ItemStack food = wheel.removeItemNoUpdate(0); if (!food.isEmpty()) popResource(level, pos, food);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
