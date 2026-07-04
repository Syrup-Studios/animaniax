package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class TroughBlockEntity extends BlockEntity {
    public static final int WATER_CAPACITY = 1000;
    private ItemStack feed = ItemStack.EMPTY;
    private boolean creativeDestruction;
    private int waterAmount = -1;

    public TroughBlockEntity(BlockPos pos, BlockState state) { super(TroughContent.TROUGH_ENTITY, pos, state); }

    private TroughBlockEntity controller() {
        if (level == null || !getBlockState().is(TroughContent.TROUGH)
                || getBlockState().getValue(TroughBlock.PART) == TroughBlock.Part.MAIN) return this;
        BlockPos mainPos = worldPosition.relative(getBlockState().getValue(TroughBlock.FACING).getCounterClockWise());
        return level.hasChunkAt(mainPos) && level.getBlockEntity(mainPos) instanceof TroughBlockEntity main ? main : this;
    }

    public int getWaterAmount() { TroughBlockEntity main = controller(); if (main != this) return main.getWaterAmount(); syncWaterFromState(); return Math.max(0, waterAmount); }

    public boolean addWater(int amount) {
        if (controller() != this) return controller().addWater(amount);
        int oldAmount = getWaterAmount();
        if (getBlockState().getValue(TroughBlock.CONTENT) == 2) return false;
        int added = Math.min(amount, WATER_CAPACITY - oldAmount);
        if (added <= 0) return false;
        waterAmount = oldAmount + added;
        syncWaterToState();
        return true;
    }

    public void setWaterAmount(int amount) {
        if (controller() != this) { controller().setWaterAmount(amount); return; }
        waterAmount = Math.max(0, Math.min(WATER_CAPACITY, amount));
        syncWaterToState();
    }

    public boolean consumeWater(int amount) {
        if (controller() != this) return controller().consumeWater(amount);
        if (getWaterAmount() < amount) return false;
        setWaterAmount(waterAmount - amount);
        return true;
    }

    public void markCreativeDestruction() { creativeDestruction = true; }
    public boolean isCreativeDestruction() { return creativeDestruction; }

    public ItemStack getFeed() {
        if (controller() != this) return controller().getFeed();
        syncStackFromState();
        return feed;
    }

    public ItemStack takeFeed() {
        if (controller() != this) return controller().takeFeed();
        syncStackFromState();
        if (feed.isEmpty()) return ItemStack.EMPTY;
        ItemStack extracted = feed.split(1);
        if (feed.isEmpty()) feed = ItemStack.EMPTY;
        setChanged();
        return extracted;
    }

    public int addFeed(ItemStack offered) {
        if (controller() != this) return controller().addFeed(offered);
        BlockState state = getBlockState();
        syncStackFromState();
        if (!isFeed(offered) || state.getValue(TroughBlock.CONTENT) == 1 || state.getValue(TroughBlock.LEVEL) >= 3
                || !feed.isEmpty() && !ItemStack.isSameItemSameComponents(feed, offered)) return 0;
        int added = Math.min(offered.getCount(), 3 - state.getValue(TroughBlock.LEVEL));
        if (added <= 0) return 0;
        feed = feed.isEmpty() ? offered.copyWithCount(added) : feed.copyWithCount(feed.getCount() + added);
        setChanged();
        return added;
    }

    public static boolean isFeed(ItemStack stack) {
        return stack.is(Items.WHEAT) || stack.is(Items.APPLE) || stack.is(Items.CARROT) || stack.is(Items.BEETROOT)
                || stack.is(Items.POTATO) || stack.is(Items.POISONOUS_POTATO) || stack.is(AnimalProfile.POULTRY_FOODS)
                || stack.is(AnimalProfile.PIG_FOODS)
                || stack.is(AnimalProfile.HORSE_FOODS)
                || stack.is(Items.EGG) || stack.is(net.syrupstudios.animaniax.item.BrownEggContent.BROWN_EGG)
                || stack.is(Items.BEEF) || stack.is(Items.COD) || stack.is(Items.SALMON)
                || stack.is(Items.TROPICAL_FISH) || stack.is(Items.PUFFERFISH)
                || stack.is(AnimalProfile.COW_FOODS) || stack.is(AnimalProfile.GOAT_FOODS)
                || stack.is(AnimalProfile.RABBIT_FOODS) || stack.is(AnimalProfile.SHEEP_FOODS);
    }

    public boolean canFeed(EntityType<?> animal) {
        if (controller() != this) return controller().canFeed(animal);
        syncStackFromState();
        return !feed.isEmpty() && AnimalProfile.acceptsTroughFeed(animal, feed);
    }

    public boolean consumeFeed(EntityType<?> animal) {
        if (controller() != this) return controller().consumeFeed(animal);
        syncStackFromState();
        if (feed.isEmpty()) return false;
        if (animal != null && !canFeed(animal)) return false;
        feed.shrink(1);
        if (feed.isEmpty()) feed = ItemStack.EMPTY;
        setChanged();
        return true;
    }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        feed = tag.contains("Feed", 10) ? ItemStack.parse(registries, tag.getCompound("Feed")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        waterAmount = tag.contains("WaterAmount") ? Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("WaterAmount"))) : -1;
        syncWaterFromState();
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!feed.isEmpty()) tag.put("Feed", feed.save(registries));
        tag.putInt("WaterAmount", getWaterAmount());
    }

    @Override public void setChanged() {
        TroughBlockEntity main = controller();
        if (main != this) { main.setChanged(); return; }
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.is(TroughContent.TROUGH)) {
                int count = feed.isEmpty() ? 0 : feed.getCount();
                int content = count > 0 ? 2 : waterAmount > 0 ? 1 : 0;
                int projection = count > 0 ? count : waterAmount > 0 ? (waterAmount * 3 + WATER_CAPACITY - 1) / WATER_CAPACITY : 0;
                BlockState projected = state.setValue(TroughBlock.CONTENT, content).setValue(TroughBlock.LEVEL, projection);
                if (!state.equals(projected)) level.setBlock(worldPosition, projected, 3);
                BlockPos endPos = TroughBlock.endPos(worldPosition, state.getValue(TroughBlock.FACING));
                if (level.hasChunkAt(endPos)) {
                    BlockState end = level.getBlockState(endPos);
                    if (end.is(TroughContent.TROUGH) && end.getValue(TroughBlock.PART) == TroughBlock.Part.END
                            && (!end.getValue(TroughBlock.CONTENT).equals(content) || end.getValue(TroughBlock.LEVEL) != projection))
                        level.setBlock(endPos, end.setValue(TroughBlock.CONTENT, content).setValue(TroughBlock.LEVEL, projection), 3);
                }
            }
        }
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            BlockPos endPos = TroughBlock.endPos(worldPosition, getBlockState().getValue(TroughBlock.FACING));
            if (level.hasChunkAt(endPos) && level.getBlockState(endPos).is(TroughContent.TROUGH))
                level.updateNeighbourForOutputSignal(endPos, TroughContent.TROUGH);
        }
    }

    private void syncStackFromState() {
        BlockState state = getBlockState();
        int count = state.is(TroughContent.TROUGH) && state.getValue(TroughBlock.CONTENT) == 2 ? state.getValue(TroughBlock.LEVEL) : 0;
        if (count == 0) feed = ItemStack.EMPTY;
        else if (feed.isEmpty()) feed = new ItemStack(Items.WHEAT, count);
        else if (feed.getCount() != count) feed = feed.copyWithCount(count);
    }

    private void syncWaterFromState() {
        if (waterAmount >= 0) return;
        BlockState state = getBlockState();
        waterAmount = state.is(TroughContent.TROUGH) && state.getValue(TroughBlock.CONTENT) == 1
                ? switch (state.getValue(TroughBlock.LEVEL)) { case 1 -> 333; case 2 -> 667; case 3 -> WATER_CAPACITY; default -> 0; } : 0;
    }

    private void syncWaterToState() {
        if (level == null || level.isClientSide || !getBlockState().is(TroughContent.TROUGH)) return;
        BlockState state = getBlockState();
        int projection = (waterAmount * 3 + WATER_CAPACITY - 1) / WATER_CAPACITY;
        level.setBlock(worldPosition, state.setValue(TroughBlock.CONTENT, waterAmount > 0 ? 1 : 0)
                .setValue(TroughBlock.LEVEL, projection), 3);
        setChanged();
    }
}
