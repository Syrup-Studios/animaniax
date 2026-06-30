package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class SaltLickBlockEntity extends BlockEntity {
    public static final int MAX_USES = 200;
    private int uses = MAX_USES;

    public SaltLickBlockEntity(BlockPos pos, BlockState state) { super(SaltLickContent.ENTITY, pos, state); }

    public void setUses(int value) {
        uses = Math.max(1, Math.min(MAX_USES, value));
        setChanged();
    }

    public int getUses() { return uses; }

    public boolean use() {
        if (uses <= 0 || level == null || level.isClientSide) return false;
        uses--;
        setChanged();
        if (uses == 0) level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), 3);
        return true;
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        uses = tag.contains("Uses") ? Math.max(0, Math.min(MAX_USES, tag.getInt("Uses"))) : MAX_USES;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Uses", uses);
    }
}
