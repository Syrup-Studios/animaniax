package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Lightweight block entity anchoring CraftStudio block-entity rendering for pet beds and props.
 */
public final class PropBlockEntity extends BlockEntity {
    public PropBlockEntity(BlockPos pos, BlockState state) {
        super(PropBlockEntityContent.ENTITY, pos, state);
    }
}
