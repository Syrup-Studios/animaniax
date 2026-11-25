package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class PetBowlBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(4, 0, 4, 12, 2, 12),
            Block.box(3, 2, 3, 13, 4, 4), Block.box(3, 2, 12, 13, 4, 13),
            Block.box(3, 2, 4, 4, 4, 12), Block.box(12, 2, 4, 13, 4, 12));

    public PetBowlBlock(Properties properties) { super(properties); }

    public static boolean isFoodItem(ItemStack stack) {
        return stack.is(ItemTags.FISHES) || stack.is(Items.BEEF)
                || stack.is(net.syrupstudios.animaniax.entity.AnimalProfile.CAT_FOODS)
                || stack.is(net.syrupstudios.animaniax.entity.AnimalProfile.DOG_FOODS)
                || stack.is(net.syrupstudios.animaniax.item.HamsterFoodContent.HAMSTER_FOOD);
    }

    @Override public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
            BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) { return SHAPE; }

    @Override public boolean hasAnalogOutputSignal(BlockState state) { return true; }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl)) return 0;
        if (!bowl.getItem(0).isEmpty()) return bowl.getItem(0).getCount() * 5;
        return bowl.getWaterAmount() * 15 / PetBowlBlockEntity.WATER_CAPACITY;
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PetBowlBlockEntity(pos, state);
    }

    @Override protected net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()
                || !(level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl) || bowl.isEmpty()) return InteractionResult.PASS;
        if (!level.isClientSide) {
            ItemStack removed = bowl.removeItem(0, 1);
            if (!player.getInventory().add(removed)) player.drop(removed, false);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.is(Items.WATER_BUCKET) && bowl.getWaterAmount() == 0 && bowl.isEmpty()) {
            if (!level.isClientSide && !player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (stack.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
            }
            if (!level.isClientSide) bowl.addWater(PetBowlBlockEntity.WATER_CAPACITY);
            return ItemInteractionResult.SUCCESS;
        }
        if (stack.is(Items.BUCKET) && bowl.getWaterAmount() == PetBowlBlockEntity.WATER_CAPACITY) {
            if (!level.isClientSide) {
                bowl.consumeWater(PetBowlBlockEntity.WATER_CAPACITY);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack bucket = new ItemStack(Items.WATER_BUCKET);
                    if (stack.isEmpty()) player.setItemInHand(hand, bucket);
                    else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
                }
            }
            return ItemInteractionResult.SUCCESS;
        }
        if (bowl.canPlaceItem(0, stack)) {
            if (!level.isClientSide) {
                bowl.addFood(stack.copyWithCount(1));
                if (!player.getAbilities().instabuild) stack.shrink(1);
            }
            return ItemInteractionResult.SUCCESS;
        }
        return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET) || isFoodItem(stack)
                ? ItemInteractionResult.FAIL : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        if (entity instanceof ItemEntity item && !level.isClientSide
                && level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl) {
            int count = bowl.getItem(0).isEmpty() ? 0 : bowl.getItem(0).getCount();
            int accepted = Math.min(item.getItem().getCount(), 3 - count);
            if (accepted > 0 && bowl.canPlaceItem(0, item.getItem())) {
                int added = bowl.addFood(item.getItem().copyWithCount(accepted));
                item.getItem().shrink(added);
                if (item.getItem().isEmpty()) item.discard();
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock()) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof PetBowlBlockEntity bowl) {
            ItemStack food = bowl.removeItemNoUpdate(0);
            if (!food.isEmpty()) popResource(level, pos, food);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
