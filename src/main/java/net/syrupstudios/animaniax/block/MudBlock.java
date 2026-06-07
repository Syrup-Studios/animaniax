package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class MudBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 14.08, 16);
    private static final TagKey<Block> PIG_MUD = TagKey.create(Registries.BLOCK,
            ResourceLocation.fromNamespaceAndPath("animaniax", "pig_mud"));

    public MudBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        slowEntity(entity);
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        slowEntity(entity);
        super.entityInside(state, level, pos, entity);
    }

    private static void slowEntity(Entity entity) {
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.2, 1.0, 0.2));
    }

    public static boolean isMud(BlockState state) {
        return state.is(PIG_MUD);
    }
}
