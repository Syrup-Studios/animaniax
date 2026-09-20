package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.item.HamsterCarryContent;
import net.syrupstudios.animaniax.item.HamsterCarryItem;
import net.syrupstudios.animaniax.item.HamsterFoodContent;

public final class HamsterWheelBlockEntity extends BlockEntity implements WorldlyContainer {
    private static final int FOOD_CAPACITY = 16;
    private ItemStack food = ItemStack.EMPTY;
    private CompoundTag hamster;
    private int ticks;
    private int energy;
    private boolean ejectionPending;
    //? if fabric {
    final FabricWheelEnergy.Storage fabricEnergy = new FabricWheelEnergy.Storage(this);
    //?}

    public HamsterWheelBlockEntity(BlockPos pos, BlockState state) { super(HamsterWheelContent.ENTITY, pos, state); }
    public boolean occupied() { return hamster != null; }
    public boolean insertHamster(CompoundTag data) {
        if (hamster != null || !validHamster(data)) return false;
        hamster = data.copy();
        ticks = 0;
        ejectionPending = false;
        syncOccupancy();
        return true;
    }
    private static boolean validHamster(CompoundTag data) {
        return HamsterEntity.id("hamster").toString().equals(data.getString("id"))
                && data.hasUUID("UUID") && data.hasUUID("Owner")
                && !data.getBoolean("InBall") && data.getInt("StoredFood") > 0;
    }
    public ItemStack ejectHamster() {
        if (hamster == null) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(HamsterCarryContent.CARRIED_HAMSTER);
        HamsterCarryItem.storeData(stack, hamster);
        hamster = null;
        ticks = 0;
        ejectionPending = false;
        syncOccupancy();
        return stack;
    }
    private void syncOccupancy() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("Occupied", occupied());
        return tag;
    }
    public static void serverTick(Level level, BlockPos pos, BlockState state, HamsterWheelBlockEntity wheel) {
        if (level.isClientSide) return;
        wheel.energy = Math.min(wheel.energy, wheel.getEnergyCapacity());
        if (wheel.hamster != null) {
            if (wheel.ejectionPending) {
                if (++wheel.ticks >= 20) {
                    wheel.ticks = 0;
                    if (!wheel.spawnHamster(level, pos) && !wheel.dropHamsterFallback(level, pos)) wheel.setChanged();
                }
            } else {
                wheel.energy = (int) Math.min(wheel.getEnergyCapacity(),
                        (long) wheel.energy + Math.max(0, AnimalCareConfig.hamsterWheelRFGeneration));
                if (wheel.ticks >= wheel.getUseTime() - 1) {
                    wheel.ticks = 0;
                    if (wheel.food.isEmpty()) {
                        if (!wheel.spawnHamster(level, pos) && !wheel.dropHamsterFallback(level, pos)) {
                            wheel.ejectionPending = true;
                            wheel.setChanged();
                        }
                    } else {
                        wheel.food.shrink(1);
                        if (wheel.food.isEmpty()) wheel.food = ItemStack.EMPTY;
                        wheel.setChanged();
                    }
                } else wheel.ticks++;
                if (wheel.ticks % 20 == 0) wheel.setChanged();
            }
        }
        //? if fabric {
        FabricWheelEnergy.push(wheel);
        //?}
    }
    private boolean spawnHamster(Level level, BlockPos pos) {
        if (hamster == null) return false;
        CompoundTag spawnData = hamster.copy();
        spawnData.putInt("StoredFood", 0);
        HamsterEntity pet = HamsterEntity.TYPE.create(level);
        if (pet == null) return false;
        pet.load(spawnData);
        for (Direction direction : Direction.values()) {
            BlockPos spawnPos = pos.relative(direction);
            BlockPos floorPos = spawnPos.below();
            if (!level.hasChunkAt(spawnPos) || !level.hasChunkAt(floorPos)
                    || !level.getWorldBorder().isWithinBounds(spawnPos)
                    || !level.getBlockState(floorPos).isFaceSturdy(level, floorPos, Direction.UP)) continue;
            pet.moveTo(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D,
                    pet.getYRot(), pet.getXRot());
            if (!level.noCollision(pet)) continue;
            if (level.addFreshEntity(pet)) {
                hamster = null;
                ticks = 0;
                ejectionPending = false;
                syncOccupancy();
                return true;
            }
        }
        pet.discard();
        return false;
    }
    private boolean dropHamsterFallback(Level level, BlockPos pos) {
        if (hamster == null || !level.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS)) return false;
        ItemStack carried = new ItemStack(HamsterCarryContent.CARRIED_HAMSTER);
        HamsterCarryItem.storeData(carried, hamster);
        ItemEntity item = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, carried);
        item.setDefaultPickUpDelay();
        if (!level.addFreshEntity(item)) {
            item.discard();
            return false;
        }
        hamster = null;
        ticks = 0;
        ejectionPending = false;
        syncOccupancy();
        return true;
    }
    public int extractEnergy(int amount) { int extracted = Math.min(Math.max(0, amount), energy); energy -= extracted; if (extracted > 0) setChanged(); return extracted; }
    public int getEnergy() { return Math.min(energy, getEnergyCapacity()); }
    public int getEnergyCapacity() { return Math.max(1, AnimalCareConfig.hamsterWheelCapacity); }
    private int getUseTime() { return Math.max(1, AnimalCareConfig.hamsterWheelUseTime); }
    void setEnergyFromTransfer(int amount) { energy = Math.max(0, Math.min(getEnergyCapacity(), amount)); }
    void markEnergyChanged() { setChanged(); }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        food = tag.contains("Food", 10) ? ItemStack.parse(registries, tag.getCompound("Food")).orElse(ItemStack.EMPTY) : ItemStack.EMPTY;
        if (tag.contains("Occupied", 1)) hamster = tag.getBoolean("Occupied") ? (hamster == null ? new CompoundTag() : hamster) : null;
        else hamster = tag.contains("Hamster", 10) ? tag.getCompound("Hamster").copy() : null;
        ejectionPending = hamster != null && tag.getBoolean("EjectionPending") && food.isEmpty();
        ticks = Math.max(0, Math.min(ejectionPending ? 19 : getUseTime() - 1, tag.getInt("Ticks")));
        energy = Math.max(0, Math.min(getEnergyCapacity(), tag.getInt("Energy")));
    }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!food.isEmpty()) tag.put("Food", food.save(registries));
        if (hamster != null) tag.put("Hamster", hamster.copy());
        tag.putInt("Ticks", ticks);
        tag.putBoolean("EjectionPending", ejectionPending);
        tag.putInt("Energy", energy);
    }
    @Override public int getContainerSize() { return 1; }
    @Override public void clearContent() { food = ItemStack.EMPTY; setChanged(); }
    @Override public boolean isEmpty() { return food.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return slot == 0 ? food : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int amount) { if (slot != 0) return ItemStack.EMPTY; ItemStack out = food.split(amount); if (food.isEmpty()) food = ItemStack.EMPTY; setChanged(); return out; }
    @Override public ItemStack removeItemNoUpdate(int slot) { if (slot != 0) return ItemStack.EMPTY; ItemStack out = food; food = ItemStack.EMPTY; return out; }
    @Override public void setItem(int slot, ItemStack stack) { if (slot == 0 && (stack.isEmpty() || stack.is(HamsterFoodContent.HAMSTER_FOOD))) { food = stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(Math.min(FOOD_CAPACITY, stack.getCount())); if (!food.isEmpty() && ejectionPending) { ejectionPending = false; ticks = 0; } setChanged(); } }
    @Override public int getMaxStackSize() { return FOOD_CAPACITY; }
    @Override public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }
    @Override public int[] getSlotsForFace(Direction side) { return new int[] {0}; }
    @Override public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 0 && stack.is(HamsterFoodContent.HAMSTER_FOOD) && food.getCount() < FOOD_CAPACITY; }
    @Override public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction side) { return slot == 0; }
}
