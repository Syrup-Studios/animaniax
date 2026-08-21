package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class CheeseMoldBlockEntity extends BlockEntity {
    private static final long MAX_MATURATION_TICKS = 2_147_483_000L;
    private long ageTicks;
    private String cheeseType = "cheese";
    private boolean suppressContentsDrop;

    public CheeseMoldBlockEntity(BlockPos pos, BlockState state) {
        super(CheeseMoldContent.MOLD_ENTITY, pos, state);
    }

    public void suppressContentsDrop() { suppressContentsDrop = true; }

    public boolean shouldDropContents() { return !suppressContentsDrop && ageTicks > 0; }

    public boolean fill(net.minecraft.world.item.ItemStack milk) {
        if (!canAcceptFluid()) return false;
        if (milk.is(net.minecraft.world.item.Items.WATER_BUCKET)) {
            cheeseType = "salt";
        } else if (milk.is(net.syrupstudios.animaniax.item.AnimalMilkContent.FRIESIAN_MILK_BUCKET)) {
            cheeseType = "friesian_cheese";
        } else if (milk.is(net.syrupstudios.animaniax.item.AnimalMilkContent.HOLSTEIN_MILK_BUCKET)) {
            cheeseType = "holstein_cheese";
        } else if (milk.is(net.syrupstudios.animaniax.item.AnimalMilkContent.JERSEY_MILK_BUCKET)) {
            cheeseType = "jersey_cheese";
        } else if (milk.is(net.syrupstudios.animaniax.item.AnimalMilkContent.GOAT_MILK_BUCKET)) {
            cheeseType = "goat_cheese";
        } else if (milk.is(net.syrupstudios.animaniax.item.AnimalMilkContent.SHEEP_MILK_BUCKET)) {
            cheeseType = "sheep_cheese";
        } else return false;
        return startMaturation();
    }

    public boolean canAcceptFluid() {
        return level != null && ageTicks == 0 && getBlockState().is(CheeseMoldContent.MOLD)
                && !getBlockState().getValue(CheeseMoldBlock.FILLED);
    }

    public boolean fillFluid(net.minecraft.world.level.material.Fluid fluid) {
        if (!canAcceptFluid()) return false;
        if (fluid == net.minecraft.world.level.material.Fluids.WATER) cheeseType = "salt";
        else if (fluid == AnimalMilkFluids.source("friesian")) cheeseType = "friesian_cheese";
        else if (fluid == AnimalMilkFluids.source("holstein")) cheeseType = "holstein_cheese";
        else if (fluid == AnimalMilkFluids.source("jersey")) cheeseType = "jersey_cheese";
        else if (fluid == AnimalMilkFluids.source("goat")) cheeseType = "goat_cheese";
        else if (fluid == AnimalMilkFluids.source("sheep")) cheeseType = "sheep_cheese";
        else return false;
        return startMaturation();
    }

    private boolean startMaturation() {
        ageTicks = 1;
        level.setBlock(worldPosition, getBlockState().setValue(CheeseMoldBlock.FILLED, true), 3);
        setChanged();
        return true;
    }

    public net.minecraft.world.item.Item getCheeseItem() {
        return cheeseType.equals("salt") ? CheeseMoldContent.SALT : CheeseMoldContent.cheeseFor(cheeseType);
    }

    public net.minecraft.world.item.ItemStack getOutput() {
        return new net.minecraft.world.item.ItemStack(getCheeseItem(), cheeseType.equals("salt")
                ? AnimalCareConfig.saltCreationAmount : 1);
    }

    private static long maturationTicks() {
        return Math.max(1, Math.min(MAX_MATURATION_TICKS, AnimalCareConfig.cheeseMaturityTime));
    }

    public int getAgePercent() { return (int) Math.min(100, ageTicks * 100 / maturationTicks()); }

    public boolean isFilled() { return ageTicks > 0; }

    public boolean isReady() {
        return ageTicks >= maturationTicks();
    }

    public void empty() {
        ageTicks = 0;
        cheeseType = "cheese";
        if (level != null && getBlockState().is(CheeseMoldContent.MOLD)) {
            level.setBlock(worldPosition, getBlockState().setValue(CheeseMoldBlock.FILLED, false), 3);
        }
        setChanged();
    }

    public static void serverTick(net.minecraft.world.level.Level level, BlockPos pos, BlockState state,
            CheeseMoldBlockEntity mold) {
        long maturationTicks = maturationTicks();
        if (mold.ageTicks > 0 && mold.ageTicks < maturationTicks) {
            mold.ageTicks++;
            if (mold.ageTicks % 20 == 0 || mold.ageTicks == maturationTicks) mold.setChanged();
        }
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (ageTicks > 0) { tag.putLong("AgeTicks", ageTicks); tag.putString("CheeseType", cheeseType); }
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ageTicks = Math.max(0, Math.min(MAX_MATURATION_TICKS, tag.getLong("AgeTicks")));
        cheeseType = tag.contains("CheeseType") ? tag.getString("CheeseType") : "cheese";
    }
}
