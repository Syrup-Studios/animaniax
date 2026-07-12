package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.item.ContainerUtils;

public final class CheeseMoldBlock extends Block implements EntityBlock {
    public static final BooleanProperty FILLED = BooleanProperty.create("filled");

    public CheeseMoldBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FILLED, false));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FILLED);
    }

    @Override protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.is(Items.WATER_BUCKET) && AnimalCareConfig.disableSaltCreation) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (stack.is(Items.WATER_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.SHEEP_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.GOAT_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.FRIESIAN_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.HOLSTEIN_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.JERSEY_MILK_BUCKET)) {
            if (state.getValue(FILLED)) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof CheeseMoldBlockEntity mold
                    && mold.fill(stack)) {
                ContainerUtils.returnEmptyBucket(player, stack);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!state.getValue(FILLED) && CheeseMoldFluidTransfer.canFill(player, hand)) {
            if (!level.isClientSide && level.getBlockEntity(pos) instanceof CheeseMoldBlockEntity mold)
                CheeseMoldFluidTransfer.fill(mold, player, hand);
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (level.getBlockEntity(pos) instanceof CheeseMoldBlockEntity mold && player.getAbilities().instabuild) {
            mold.suppressContentsDrop();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!newState.is(this) && !level.isClientSide
                && level.getBlockEntity(pos) instanceof CheeseMoldBlockEntity mold && mold.shouldDropContents()) {
            if (mold.isReady()) popResource(level, pos, mold.getOutput());
        }
        super.onRemove(state, level, pos, newState, moving);
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (!state.getValue(FILLED)) return InteractionResult.PASS;
        if (level.isClientSide) return InteractionResult.sidedSuccess(true);
        if (!(level.getBlockEntity(pos) instanceof CheeseMoldBlockEntity mold)) return InteractionResult.PASS;
        if (player.isShiftKeyDown()) {
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("message.animaniax.cheese_mold.age", mold.getAgePercent()), true);
            return InteractionResult.sidedSuccess(false);
        }
        if (!mold.isReady()) return InteractionResult.PASS;
        ItemStack output = mold.getOutput();
        if (!player.getInventory().add(output)) player.drop(output, false);
        mold.empty();
        return InteractionResult.sidedSuccess(false);
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheeseMoldBlockEntity(pos, state);
    }

    @Override @SuppressWarnings("unchecked") public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
            BlockState state, BlockEntityType<T> type) {
        return level.isClientSide || type != CheeseMoldContent.MOLD_ENTITY ? null
                : (BlockEntityTicker<T>) (BlockEntityTicker<CheeseMoldBlockEntity>) CheeseMoldBlockEntity::serverTick;
    }
}
