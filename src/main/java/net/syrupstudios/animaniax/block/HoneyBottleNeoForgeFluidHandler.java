//? if neoforge {
/*
package net.syrupstudios.animaniax.block;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public final class HoneyBottleNeoForgeFluidHandler implements IFluidHandlerItem {
    private ItemStack container;

    public HoneyBottleNeoForgeFluidHandler(ItemStack stack) {
        this.container = stack.copy();
    }

    @Override public ItemStack getContainer() { return container; }
    @Override public int getTanks() { return 1; }
    @Override public FluidStack getFluidInTank(int tank) {
        return tank == 0 && container.is(BeeHiveContent.HONEY_BOTTLE)
                ? new FluidStack(BeeHiveContent.HONEY_SOURCE, HoneyBottleFluidAmount.get(container))
                : FluidStack.EMPTY;
    }
    @Override public int getTankCapacity(int tank) { return tank == 0 ? FluidType.BUCKET_VOLUME : 0; }
    @Override public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && FluidStack.isSameFluidSameComponents(
                stack, new FluidStack(BeeHiveContent.HONEY_SOURCE, 1));
    }
    @Override public int fill(FluidStack resource, FluidAction action) {
        if (!isFluidValid(0, resource) || container.getCount() != 1
                || !container.is(BeeHiveContent.HONEY_BOTTLE)) return 0;
        int current = HoneyBottleFluidAmount.get(container);
        int filled = Math.min(resource.getAmount(), FluidType.BUCKET_VOLUME - current);
        if (filled > 0 && action.execute()) HoneyBottleFluidAmount.set(container, current + filled);
        return filled;
    }
    @Override public FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty() || !FluidStack.isSameFluidSameComponents(resource, getFluidInTank(0))
                || container.getCount() != 1 || !container.is(BeeHiveContent.HONEY_BOTTLE)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }
    @Override public FluidStack drain(int maxDrain, FluidAction action) {
        if (maxDrain <= 0 || container.getCount() != 1 || !container.is(BeeHiveContent.HONEY_BOTTLE)) return FluidStack.EMPTY;
        int current = HoneyBottleFluidAmount.get(container);
        int drained = Math.min(maxDrain, current);
        if (drained == 0) return FluidStack.EMPTY;
        if (action.execute()) {
            if (drained == current) container = new ItemStack(Items.GLASS_BOTTLE);
            else HoneyBottleFluidAmount.set(container, current - drained);
        }
        return new FluidStack(BeeHiveContent.HONEY_SOURCE, drained);
    }
}
*/
//?}
