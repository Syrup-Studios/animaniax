package net.syrupstudios.animaniax.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
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
*///?}

public final class WaterContainerTransfer {
    private WaterContainerTransfer() {}

    public static boolean isWaterContainer(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.WATER_BUCKET)) return true;
        if (held.is(Items.BUCKET) || held.isEmpty()) return false;
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        try (Transaction transaction = Transaction.openOuter()) {
            return storage.extract(FluidVariant.of(Fluids.WATER), FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET;
        }
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (handler == null) return false;
        FluidStack simulated = handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        return simulated.getAmount() == FluidType.BUCKET_VOLUME && simulated.is(Fluids.WATER);
        *///?}
    }

    public static boolean water(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.WATER_BUCKET)) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (held.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
            }
            return true;
        }
        if (held.is(Items.BUCKET) || held.isEmpty()) return false;
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        FluidVariant water = FluidVariant.of(Fluids.WATER);
        try (Transaction transaction = Transaction.openOuter()) {
            if (storage.extract(water, FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET) return false;
            if (!player.getAbilities().instabuild) transaction.commit();
            return true;
        }
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (handler == null) return false;
        FluidStack simulated = handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.SIMULATE);
        if (simulated.getAmount() != FluidType.BUCKET_VOLUME || !simulated.is(Fluids.WATER)) return false;
        if (!player.getAbilities().instabuild) {
            FluidStack drained = handler.drain(FluidType.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            if (drained.getAmount() != FluidType.BUCKET_VOLUME || !drained.is(Fluids.WATER)) return false;
            ItemStack emptied = handler.getContainer();
            if (held.getCount() == 1) player.setItemInHand(hand, emptied);
            else {
                held.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, emptied);
            }
        }
        return true;
        *///?}
    }
}
