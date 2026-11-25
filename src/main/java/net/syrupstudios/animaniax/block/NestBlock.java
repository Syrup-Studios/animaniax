package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class NestBlock extends Block implements EntityBlock {
    public static final IntegerProperty EGGS = IntegerProperty.create("eggs", 0, 3);
    private static final VoxelShape SHAPE = Shapes.or(Block.box(1, 0, 1, 15, 3, 15),
            Block.box(1, 3, 1, 4, 5, 15), Block.box(12, 3, 1, 15, 5, 15),
            Block.box(4, 3, 1, 12, 5, 4), Block.box(4, 3, 12, 12, 5, 15));

    public NestBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(EGGS, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(EGGS);
    }

    @Override protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
            BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) { return SHAPE; }

    @Override public boolean hasAnalogOutputSignal(BlockState state) { return true; }

    @Override public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return state.getValue(EGGS) * 5;
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;
        int eggs = state.getValue(EGGS);
        if (eggs == 0) return InteractionResult.PASS;
        if (!level.isClientSide) {
            ItemStack egg = new ItemStack(Items.EGG);
            if (level.getBlockEntity(pos) instanceof NestBlockEntity nest) {
                nest.syncEggMetadata(eggs);
                egg = nest.eggStack(0);
                nest.removeEggs(1);
            }
            level.setBlock(pos, state.setValue(EGGS, eggs - 1), Block.UPDATE_ALL);
            if (!player.getInventory().add(egg)) player.drop(egg, false);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override public java.util.List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        java.util.List<ItemStack> drops = new java.util.ArrayList<>(super.getDrops(state, params));
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof NestBlockEntity nest) {
            nest.syncEggMetadata(state.getValue(EGGS));
            for (int i = 0; i < state.getValue(EGGS); i++) drops.add(nest.eggStack(i));
        } else if (state.getValue(EGGS) > 0) drops.add(new ItemStack(Items.EGG, state.getValue(EGGS)));
        return drops;
    }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new NestBlockEntity(pos, state);
    }

    @Override protected net.minecraft.world.level.block.RenderShape getRenderShape(BlockState state) {
        return net.minecraft.world.level.block.RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(EGGS) > 0 && level.getBlockEntity(pos) instanceof NestBlockEntity nest)
            nest.tryHatch(level, pos, state, random);
    }

    public static boolean addEgg(Level level, BlockPos pos, String breed) {
        BlockState state = level.getBlockState(pos);
        if (level.isClientSide || !state.is(NestContent.NEST) || state.getValue(EGGS) >= 3) return false;
        if (!(level.getBlockEntity(pos) instanceof NestBlockEntity nest)) return false;
        if (!nest.canAcceptEgg(breed, state.getValue(EGGS))) return false;
        nest.syncEggMetadata(state.getValue(EGGS));
        if (!level.setBlock(pos, state.setValue(EGGS, state.getValue(EGGS) + 1), Block.UPDATE_ALL)) return false;
        nest.addEggParent(breed);
        return true;
    }

    public static boolean canAcceptEgg(Level level, BlockPos pos, String breed) {
        BlockState state = level.getBlockState(pos);
        return state.is(NestContent.NEST) && state.getValue(EGGS) < 3
                && level.getBlockEntity(pos) instanceof NestBlockEntity nest
                && nest.canAcceptEgg(breed, state.getValue(EGGS));
    }

}
