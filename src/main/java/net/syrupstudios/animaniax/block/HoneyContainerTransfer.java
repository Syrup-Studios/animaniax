package net.syrupstudios.animaniax.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
//?}
//? if neoforge {
/*
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.items.ItemHandlerHelper;
*/
//?}

public final class HoneyContainerTransfer {
    private HoneyContainerTransfer() {}

    public static boolean canCollect(BeeHiveBlockEntity hive, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || hive.getHoney() < BeeHiveBlockEntity.BOTTLE_AMOUNT) return false;
        //? if fabric {
        Storage<FluidVariant> container = ContainerItemContext.forPlayerInteraction(player, hand)
                .find(FluidStorage.ITEM);
        if (container == null || !isEmpty(container)) return false;
        FluidVariant honey = FluidVariant.of(BeeHiveContent.HONEY_SOURCE);
        try (Transaction transaction = Transaction.openOuter()) {
            return container.insert(honey, FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET;
        }
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem container = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (container == null) return false;
        for (int tank = 0; tank < container.getTanks(); tank++)
            if (!container.getFluidInTank(tank).isEmpty()) return false;
        FluidStack honey = new FluidStack(BeeHiveContent.HONEY_SOURCE, FluidType.BUCKET_VOLUME);
        return container.fill(honey, IFluidHandler.FluidAction.SIMULATE) == FluidType.BUCKET_VOLUME;
        */
        //?}
    }

    public static boolean collect(BeeHiveBlockEntity hive, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!canCollect(hive, player, hand)) return false;
        //? if fabric {
        Storage<FluidVariant> container = ContainerItemContext.forPlayerInteraction(player, hand)
                .find(FluidStorage.ITEM);
        FluidVariant honey = FluidVariant.of(BeeHiveContent.HONEY_SOURCE);
        long amount = FluidConstants.BUCKET;
        if (player.getAbilities().instabuild) {
            try (Transaction transaction = Transaction.openOuter()) {
                if (container.insert(honey, amount, transaction) != amount) return false;
            }
            hive.removeHoney(BeeHiveBlockEntity.BOTTLE_AMOUNT);
            return true;
        }
        try (Transaction transaction = Transaction.openOuter()) {
            if (container.insert(honey, amount, transaction) != amount) return false;
            if (hive.getFluidStorage().extract(honey, amount, transaction) != amount) return false;
            transaction.commit();
            return true;
        }
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem container = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (container == null) return false;
        FluidStack honey = new FluidStack(BeeHiveContent.HONEY_SOURCE, FluidType.BUCKET_VOLUME);
        if (container.fill(honey, IFluidHandler.FluidAction.SIMULATE) != FluidType.BUCKET_VOLUME) return false;
        if (player.getAbilities().instabuild) {
            hive.removeHoney(BeeHiveBlockEntity.BOTTLE_AMOUNT);
            return true;
        }
        if (container.fill(honey, IFluidHandler.FluidAction.EXECUTE) != FluidType.BUCKET_VOLUME) return false;
        ItemStack filled = container.getContainer();
        if (held.getCount() == 1) player.setItemInHand(hand, filled);
        else {
            held.shrink(1);
            ItemHandlerHelper.giveItemToPlayer(player, filled);
        }
        hive.removeHoney(BeeHiveBlockEntity.BOTTLE_AMOUNT);
        return true;
        */
        //?}
    }

    //? if fabric {
    private static boolean isEmpty(Storage<FluidVariant> storage) {
        for (var view : storage) {
            if (!view.isResourceBlank() && view.getAmount() > 0) return false;
        }
        return true;
    }
    //?}
}
