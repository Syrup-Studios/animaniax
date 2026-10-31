package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.network.chat.Component;

public final class BeeHiveBlock extends Block implements EntityBlock {
    private final boolean wild;

    public BeeHiveBlock(Properties properties, boolean wild) {
        super(properties);
        this.wild = wild;
    }

    public boolean isWild() { return wild; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BeeHiveBlockEntity(pos, state);
    }

    @Override @SuppressWarnings("unchecked") public <T extends BlockEntity>
            net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(Level level, BlockState state,
                    net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        return level.isClientSide || type != BeeHiveContent.ENTITY ? null
                : (net.minecraft.world.level.block.entity.BlockEntityTicker<T>)
                        (net.minecraft.world.level.block.entity.BlockEntityTicker<BeeHiveBlockEntity>) BeeHiveBlockEntity::serverTick;
    }

    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level,
            BlockPos pos, Player player, net.minecraft.world.InteractionHand hand, BlockHitResult hit) {
        boolean bottle = stack.is(Items.GLASS_BOTTLE);
        boolean bucket = stack.is(Items.BUCKET);
        if (!bottle && !bucket) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BeeHiveBlockEntity hive)
                || hive.getHoney() < BeeHiveBlockEntity.BOTTLE_AMOUNT) return ItemInteractionResult.FAIL;
        hive.removeHoney(BeeHiveBlockEntity.BOTTLE_AMOUNT);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            ItemStack filled = new ItemStack(bottle ? BeeHiveContent.HONEY_BOTTLE : BeeHiveContent.HONEY_BUCKET);
            if (stack.isEmpty()) player.setItemInHand(hand, filled);
            else if (!player.getInventory().add(filled)) player.drop(filled, false);
        }
        return ItemInteractionResult.SUCCESS;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (!player.isShiftKeyDown()) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BeeHiveBlockEntity hive)) return InteractionResult.PASS;
        player.displayClientMessage(Component.literal("Honey stored: " + hive.getHoney() + " mB"), true);
        return InteractionResult.SUCCESS;
    }
}
