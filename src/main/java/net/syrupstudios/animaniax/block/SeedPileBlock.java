package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

public final class SeedPileBlock extends Block {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(2, 0, 2, 14, 2, 14),
            Block.box(4, 2, 4, 12, 3, 12));

    public SeedPileBlock(BlockBehaviour.Properties properties) { super(properties); }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !neighborState.isFaceSturdy(level, neighborPos, Direction.UP)
                ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    public static boolean canPlace(Level level, BlockPos support) {
        BlockState state = level.getBlockState(support);
        BlockPos pos = support.above();
        return !state.is(Blocks.FARMLAND) && state.isFaceSturdy(level, support, Direction.UP)
                && level.getBlockState(pos).canBeReplaced() && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty();
    }
}
