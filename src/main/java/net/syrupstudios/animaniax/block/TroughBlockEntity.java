package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.EntityType;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class TroughBlockEntity extends BlockEntity implements WorldlyContainer {
    public static final int WATER_CAPACITY = 1000;
    private ItemStack feed = ItemStack.EMPTY;
    private ItemStack bucketOutput = ItemStack.EMPTY;
    private boolean creativeDestruction;
    private int waterAmount = -1;
    private int slopAmount;

    public TroughBlockEntity(BlockPos pos, BlockState state) { super(TroughContent.TROUGH_ENTITY, pos, state); }
    private TroughBlockEntity controller() {
        if (level == null || !getBlockState().is(TroughContent.TROUGH)
                || getBlockState().getValue(TroughBlock.PART) == TroughBlock.Part.MAIN) return this;
        BlockPos mainPos = worldPosition.relative(getBlockState().getValue(TroughBlock.FACING).getCounterClockWise());
        return level.hasChunkAt(mainPos) && level.getBlockEntity(mainPos) instanceof TroughBlockEntity main ? main : this;
    }
    public int getWaterAmount() { TroughBlockEntity main = controller(); if (main != this) return main.getWaterAmount(); syncWaterFromState(); return Math.max(0, waterAmount); }
    public int getSlopAmount() { return controller() == this ? slopAmount : controller().getSlopAmount(); }
    public boolean addWater(int amount) {
        if (controller() != this) return controller().addWater(amount);
        int oldAmount = getWaterAmount();
        if (getBlockState().getValue(TroughBlock.CONTENT) == 2 || getBlockState().getValue(TroughBlock.CONTENT) == 3) return false;
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
    public boolean addSlop() {
        if (controller() != this) return controller().addSlop();
        if (getBlockState().getValue(TroughBlock.CONTENT) != 0 || getBlockState().getValue(TroughBlock.LEVEL) != 0) return false;
        slopAmount = WATER_CAPACITY;
        syncSlopToState();
        return true;
    }
    public boolean consumeSlop() {
        if (controller() != this) return controller().consumeSlop();
        if (getBlockState().getValue(TroughBlock.CONTENT) != 3 || slopAmount < 100) return false;
        slopAmount -= 100;
        syncSlopToState();
        return true;
    }
    public void markCreativeDestruction() { creativeDestruction = true; }
    public boolean isCreativeDestruction() { return creativeDestruction; }
    @Override public int getContainerSize() { return controller() == this ? 2 : controller().getContainerSize(); }
    @Override public int getMaxStackSize() { return 3; }
    @Override public boolean isEmpty() { return controller() == this ? bucketOutput.isEmpty() && feed.isEmpty() && getBlockState().getValue(TroughBlock.CONTENT) == 0 : controller().isEmpty(); }
    @Override public ItemStack getItem(int slot) { if (controller() != this) return controller().getItem(slot); syncStackFromState(); return slot == 0 ? feed : slot == 1 ? bucketOutput : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int amount) {
        if (controller() != this) return controller().removeItem(slot, amount);
        if (!AnimalCareConfig.allowTroughAutomation || amount <= 0) return ItemStack.EMPTY;
        if (slot == 0) syncStackFromState();
        ItemStack removed;
        if (slot == 0) {
            removed = feed.split(amount);
            if (feed.isEmpty()) feed = ItemStack.EMPTY;
        } else if (slot == 1 && !bucketOutput.isEmpty()) {
            removed = bucketOutput.split(amount);
            if (bucketOutput.isEmpty()) bucketOutput = ItemStack.EMPTY;
        } else {
            return ItemStack.EMPTY;
        }
        setChanged();
        return removed;
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
    @Override public ItemStack removeItemNoUpdate(int slot) {
        if (controller() != this) return controller().removeItemNoUpdate(slot);
        if (!AnimalCareConfig.allowTroughAutomation || slot != 1) return ItemStack.EMPTY;
        ItemStack removed = bucketOutput;
        bucketOutput = ItemStack.EMPTY;
        return removed;
    }
    @Override public void setItem(int slot, ItemStack stack) {
        if (controller() != this) { controller().setItem(slot, stack); return; }
        if (!AnimalCareConfig.allowTroughAutomation) return;
        if (slot == 0 && stack.is(net.syrupstudios.animaniax.item.SlopContent.SLOP_BUCKET)) {
            if (!canPlaceItem(slot, stack) || !addSlop()) return;
            bucketOutput = new ItemStack(Items.BUCKET);
            setChanged();
        } else if (slot == 0 && stack.is(Items.WATER_BUCKET)) {
            if (!canPlaceItem(slot, stack) || !TroughBlock.fillWater(level, worldPosition)) return;
            bucketOutput = new ItemStack(Items.BUCKET);
            setChanged();
        } else if (slot == 0 && (stack.isEmpty() || isFeed(stack) && canPlaceItem(slot, stack)) && stack.getCount() <= 3) {
            feed = stack.isEmpty() ? ItemStack.EMPTY : stack.copy();
            setChanged();
        }
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
        if (AnimalProfile.has(animal, AnimalProfile.Capability.TROUGH_SLOP)
                && getBlockState().getValue(TroughBlock.CONTENT) == 3) return slopAmount >= 100;
        return !feed.isEmpty() && AnimalProfile.acceptsTroughFeed(animal, feed);
    }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        bucketOutput = tag.getBoolean("BucketOutput") ? new ItemStack(Items.BUCKET) : ItemStack.EMPTY;
        feed = tag.contains("Feed", 10) ? ItemStack.parse(registries, tag.getCompound("Feed")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        waterAmount = tag.contains("WaterAmount") ? Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("WaterAmount"))) : -1;
        slopAmount = Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("SlopAmount")));
        syncWaterFromState();
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean("BucketOutput", !bucketOutput.isEmpty());
        if (!feed.isEmpty()) tag.put("Feed", feed.save(registries));
        tag.putInt("WaterAmount", getWaterAmount());
        tag.putInt("SlopAmount", slopAmount);
    }
    @Override public void setChanged() {
        TroughBlockEntity main = controller();
        if (main != this) { main.setChanged(); return; }
        if (level != null && !level.isClientSide) {
            BlockState state = getBlockState();
            if (state.is(TroughContent.TROUGH)) {
                int count = feed.isEmpty() ? 0 : feed.getCount();
                int content = count > 0 ? 2 : waterAmount > 0 ? 1 : slopAmount > 0 ? 3 : 0;
                int projection = count > 0 ? count : waterAmount > 0 ? (waterAmount * 3 + WATER_CAPACITY - 1) / WATER_CAPACITY
                        : slopAmount > 0 ? (slopAmount * 3 + WATER_CAPACITY - 1) / WATER_CAPACITY : 0;
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
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() {}
    @Override public int[] getSlotsForFace(net.minecraft.core.Direction direction) {
        return AnimalCareConfig.allowTroughAutomation ? new int[] {0, 1} : new int[0];
    }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction direction) {
        return AnimalCareConfig.allowTroughAutomation && canPlaceItem(slot, stack);
    }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        if (controller() != this) return controller().canPlaceItem(slot, stack);
        syncStackFromState();
        BlockState state = getBlockState();
        return AnimalCareConfig.allowTroughAutomation && slot == 0
                && (stack.is(net.syrupstudios.animaniax.item.SlopContent.SLOP_BUCKET)
                    ? stack.getCount() == 1 && bucketOutput.isEmpty() && state.getValue(TroughBlock.CONTENT) == 0 && state.getValue(TroughBlock.LEVEL) == 0
                    : stack.is(Items.WATER_BUCKET)
                    ? stack.getCount() == 1 && bucketOutput.isEmpty()
                        && (state.getValue(TroughBlock.CONTENT) == 0 || state.getValue(TroughBlock.CONTENT) == 1)
                        && getWaterAmount() < WATER_CAPACITY
                    : isFeed(stack) && (state.getValue(TroughBlock.CONTENT) == 0 || state.getValue(TroughBlock.CONTENT) == 2)
                        && state.getValue(TroughBlock.LEVEL) < 3
                        && (feed.isEmpty() || ItemStack.isSameItemSameComponents(feed, stack)));
    }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, net.minecraft.core.Direction direction) {
        return AnimalCareConfig.allowTroughAutomation && (slot == 0 && isFeed(stack) || slot == 1 && stack.is(Items.BUCKET));
    }
    public boolean consumeFeed(EntityType<?> animal) {
        if (controller() != this) return controller().consumeFeed(animal);
        if (AnimalProfile.has(animal, AnimalProfile.Capability.TROUGH_SLOP)
                && getBlockState().getValue(TroughBlock.CONTENT) == 3) return consumeSlop();
        syncStackFromState();
        if (feed.isEmpty()) return false;
        if (animal != null && !canFeed(animal)) return false;
        feed.shrink(1);
        if (feed.isEmpty()) feed = ItemStack.EMPTY;
        setChanged();
        return true;
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
    private void syncSlopToState() {
        if (level == null || level.isClientSide || !getBlockState().is(TroughContent.TROUGH)) return;
        BlockState state = getBlockState();
        int projection = (slopAmount * 3 + WATER_CAPACITY - 1) / WATER_CAPACITY;
        level.setBlock(worldPosition, state.setValue(TroughBlock.CONTENT, slopAmount > 0 ? 3 : 0).setValue(TroughBlock.LEVEL, projection), 3);
        setChanged();
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
