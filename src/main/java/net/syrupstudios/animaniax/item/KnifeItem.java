package net.syrupstudios.animaniax.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class KnifeItem extends Item {
    public KnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }
}
