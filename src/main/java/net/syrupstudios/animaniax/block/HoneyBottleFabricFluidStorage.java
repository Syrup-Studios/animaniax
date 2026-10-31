//? if fabric {
package net.syrupstudios.animaniax.block;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.Items;

public final class HoneyBottleFabricFluidStorage implements SingleSlotStorage<FluidVariant> {
    private static final long FABRIC_UNITS_PER_MILLIBUCKET = 81;
    private static final long CAPACITY = 1000 * FABRIC_UNITS_PER_MILLIBUCKET;
    private final ContainerItemContext context;
    private final FluidVariant honey = FluidVariant.of(BeeHiveContent.HONEY_SOURCE);

    public HoneyBottleFabricFluidStorage(ContainerItemContext context) {
        this.context = context;
    }

    @Override public long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!resource.equals(honey) || !isSingleBottle()) return 0;
        int current = HoneyBottleFluidAmount.get(context.getItemVariant().toStack());
        long inserted = Math.min(maxAmount / FABRIC_UNITS_PER_MILLIBUCKET, 1000 - current);
        if (inserted == 0) return 0;
        var updated = context.getItemVariant().toStack();
        HoneyBottleFluidAmount.set(updated, current + (int) inserted);
        return context.exchange(ItemVariant.of(updated), 1, transaction) == 1
                ? inserted * FABRIC_UNITS_PER_MILLIBUCKET : 0;
    }

    @Override public long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notBlankNotNegative(resource, maxAmount);
        if (!resource.equals(honey) || !isSingleBottle()) return 0;
        int current = HoneyBottleFluidAmount.get(context.getItemVariant().toStack());
        long extracted = Math.min(maxAmount / FABRIC_UNITS_PER_MILLIBUCKET, current);
        if (extracted == 0) return 0;
        var updated = context.getItemVariant().toStack();
        if (current == extracted) return context.exchange(ItemVariant.of(Items.GLASS_BOTTLE), 1, transaction) == 1
                ? extracted * FABRIC_UNITS_PER_MILLIBUCKET : 0;
        HoneyBottleFluidAmount.set(updated, current - (int) extracted);
        return context.exchange(ItemVariant.of(updated), 1, transaction) == 1
                ? extracted * FABRIC_UNITS_PER_MILLIBUCKET : 0;
    }

    @Override public boolean isResourceBlank() { return getAmount() == 0; }
    @Override public FluidVariant getResource() { return isResourceBlank() ? FluidVariant.blank() : honey; }
    @Override public long getAmount() {
        return isSingleBottle()
                ? HoneyBottleFluidAmount.get(context.getItemVariant().toStack()) * FABRIC_UNITS_PER_MILLIBUCKET : 0;
    }
    @Override public long getCapacity() { return CAPACITY; }

    private boolean isSingleBottle() {
        return context.getItemVariant().isOf(BeeHiveContent.HONEY_BOTTLE) && context.getAmount() == 1;
    }
}
//?}
