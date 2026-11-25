package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class SaltLickBlockEntity extends BlockEntity {
    public static final int MAX_USES = 200;
    private int uses = AnimalCareConfig.saltLickMaxUses;

    public SaltLickBlockEntity(BlockPos pos, BlockState state) { super(SaltLickContent.ENTITY, pos, state); }

    public void setUses(int value) {
        uses = Math.max(1, Math.min(AnimalCareConfig.saltLickMaxUses, value));
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

    @Override public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        uses = tag.contains("Uses") ? Math.max(0, Math.min(AnimalCareConfig.saltLickMaxUses, tag.getInt("Uses"))) : AnimalCareConfig.saltLickMaxUses;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("Uses", uses);
    }
}
