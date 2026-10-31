package net.syrupstudios.animaniax.block;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

final class HoneyBottleFluidAmount {
    private static final String AMOUNT_TAG = "AnimaniaxHoneyAmount";

    private HoneyBottleFluidAmount() {}

    static int get(ItemStack stack) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        return tag.contains(AMOUNT_TAG) ? Math.max(0, Math.min(1000, tag.getInt(AMOUNT_TAG))) : 1000;
    }

    static void set(ItemStack stack, int amount) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (amount == 1000) tag.remove(AMOUNT_TAG);
        else tag.putInt(AMOUNT_TAG, amount);
        if (tag.isEmpty()) stack.remove(DataComponents.CUSTOM_DATA);
        else stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
}
