//? if neoforge {
/*
package net.syrupstudios.animaniax.block;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

public final class BeeHiveNeoForgeFluidHandler implements IFluidHandler {
    private final BeeHiveBlockEntity hive;

    public BeeHiveNeoForgeFluidHandler(BeeHiveBlockEntity hive) {
        this.hive = hive;
    }

    @Override public int getTanks() { return 1; }

    @Override public FluidStack getFluidInTank(int tank) {
        return tank == 0 && hive.getHoney() > 0
                ? new FluidStack(BeeHiveContent.HONEY_SOURCE, hive.getHoney())
                : FluidStack.EMPTY;
    }

    @Override public int getTankCapacity(int tank) {
        return tank == 0 ? BeeHiveBlockEntity.CAPACITY : 0;
    }

    @Override public boolean isFluidValid(int tank, FluidStack stack) {
        return tank == 0 && isHoney(stack);
    }

    @Override public int fill(FluidStack resource, FluidAction action) {
        if (!isHoney(resource) || resource.isEmpty()) return 0;
        int accepted = Math.min(resource.getAmount(), BeeHiveBlockEntity.CAPACITY - hive.getHoney());
        if (accepted > 0 && action.execute()) hive.setHoney(hive.getHoney() + accepted);
        return accepted;
    }

    @Override public FluidStack drain(FluidStack resource, FluidAction action) {
        if (!isHoney(resource) || resource.isEmpty()) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    @Override public FluidStack drain(int maxDrain, FluidAction action) {
        int drained = Math.min(Math.max(0, maxDrain), hive.getHoney());
        if (drained == 0) return FluidStack.EMPTY;
        if (action.execute()) hive.setHoney(hive.getHoney() - drained);
        return new FluidStack(BeeHiveContent.HONEY_SOURCE, drained);
    }

    private static boolean isHoney(FluidStack stack) {
        return !stack.isEmpty() && stack.getFluid() == BeeHiveContent.HONEY_SOURCE
                && stack.getComponents().isPatchEmpty();
    }
}
*/
//?}
