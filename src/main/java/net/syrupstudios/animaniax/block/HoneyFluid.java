package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
//? if neoforge {
/*
import net.neoforged.neoforge.fluids.FluidType;
*///?}

public abstract class HoneyFluid extends FlowingFluid {
    //? if neoforge {
    /*
    @Override public FluidType getFluidType() { return net.syrupstudios.animaniax.loader.NeoForgeEntrypoint.HONEY_TYPE.get(); }
    *///?}
    @Override public Fluid getFlowing() { return BeeHiveContent.HONEY_FLOWING; }
    @Override public Fluid getSource() { return BeeHiveContent.HONEY_SOURCE; }
    @Override public Item getBucket() { return BeeHiveContent.HONEY_BUCKET; }
    @Override protected boolean canConvertToSource(Level level) { return false; }
    @Override protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        net.minecraft.world.level.block.Block.dropResources(state, level, pos,
                state.hasBlockEntity() ? level.getBlockEntity(pos) : null);
    }
    @Override protected int getSlopeFindDistance(LevelReader level) { return 4; }
    @Override protected int getDropOff(LevelReader level) { return 2; }
    @Override public int getTickDelay(LevelReader level) { return 20; }
    @Override protected float getExplosionResistance() { return 100.0F; }
    @Override protected boolean canBeReplacedWith(FluidState state, BlockGetter level, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !isSame(fluid);
    }
    @Override protected BlockState createLegacyBlock(FluidState state) {
        return BeeHiveContent.HONEY_BLOCK.defaultBlockState().setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }
    @Override public boolean isSame(Fluid fluid) { return fluid == BeeHiveContent.HONEY_SOURCE || fluid == BeeHiveContent.HONEY_FLOWING; }

    public static final class Source extends HoneyFluid {
        @Override public int getAmount(FluidState state) { return 8; }
        @Override public boolean isSource(FluidState state) { return true; }
    }

    public static final class Flowing extends HoneyFluid {
        public Flowing() { registerDefaultState(getStateDefinition().any().setValue(LEVEL, 7)); }
        @Override protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }
        @Override public int getAmount(FluidState state) { return state.getValue(LEVEL); }
        @Override public boolean isSource(FluidState state) { return false; }
    }
}
