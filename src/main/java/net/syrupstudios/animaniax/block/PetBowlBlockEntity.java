package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EntityType;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class PetBowlBlockEntity extends BlockEntity implements Container {
    public static final int WATER_CAPACITY = 1000;
    private ItemStack food = ItemStack.EMPTY;
    private int waterAmount;

    public PetBowlBlockEntity(BlockPos pos, BlockState state) {
        super(PetBowlContent.BLOCK_ENTITY, pos, state);
    }

    public int getWaterAmount() { return waterAmount; }
    public ItemStack getFood() { return food; }

    public boolean addWater(int amount) {
        int added = Math.min(amount, WATER_CAPACITY - waterAmount);
        if (added <= 0 || !food.isEmpty()) return false;
        waterAmount += added;
        setChanged();
        return true;
    }

    public boolean consumeWater(int amount) {
        if (waterAmount < amount) return false;
        waterAmount -= amount;
        setChanged();
        return true;
    }

    public boolean canFeed(EntityType<?> animal) {
        return !food.isEmpty() && AnimalProfile.acceptsPetBowlFood(animal, food);
    }

    public boolean consumeFood(EntityType<?> animal) {
        if (!canFeed(animal)) return false;
        food.shrink(1);
        if (food.isEmpty()) food = ItemStack.EMPTY;
        setChanged();
        return true;
    }

    public int addFood(ItemStack offered) {
        if (!canPlaceItem(0, offered)) return 0;
        int added = Math.min(offered.getCount(), 3 - food.getCount());
        food = food.isEmpty() ? offered.copyWithCount(added) : food.copyWithCount(food.getCount() + added);
        setChanged();
        return added;
    }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return food.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return slot == 0 ? food : ItemStack.EMPTY; }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || food.isEmpty()) return ItemStack.EMPTY;
        ItemStack removed = food.split(amount);
        if (food.isEmpty()) food = ItemStack.EMPTY;
        setChanged();
        return removed;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack removed = food;
        food = ItemStack.EMPTY;
        return removed;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot != 0 || !stack.isEmpty() && (!PetBowlBlock.isFoodItem(stack) || !food.isEmpty()
                && !ItemStack.isSameItemSameComponents(food, stack) || waterAmount > 0)) return;
        food = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(Math.min(3, stack.getCount()));
        setChanged();
    }

    @Override public int getMaxStackSize() { return 3; }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && PetBowlBlock.isFoodItem(stack) && waterAmount == 0
                && (food.isEmpty() || ItemStack.isSameItemSameComponents(food, stack)) && food.getCount() < 3;
    }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public void clearContent() { food = ItemStack.EMPTY; waterAmount = 0; setChanged(); }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        food = tag.contains("Food", 10) ? ItemStack.parse(registries, tag.getCompound("Food")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        if (food.getCount() > 3 || !PetBowlBlock.isFoodItem(food)) food = ItemStack.EMPTY;
        waterAmount = Math.max(0, Math.min(WATER_CAPACITY, tag.getInt("WaterAmount")));
        if (!food.isEmpty()) waterAmount = 0;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!food.isEmpty()) tag.put("Food", food.save(registries));
        tag.putInt("WaterAmount", waterAmount);
    }
}
