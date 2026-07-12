package net.syrupstudios.animaniax.block;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;
*/
//?}

public final class CheeseMoldFluidTransfer {
    private CheeseMoldFluidTransfer() {}

    public static boolean canFill(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty() || held.is(Items.BUCKET) || isNamedBucket(held)) return false;
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        for (Fluid fluid : supportedFluids()) {
            if (!isAllowedFluid(fluid)) continue;
            try (Transaction transaction = Transaction.openOuter()) {
                if (storage.extract(FluidVariant.of(fluid), FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET)
                    return true;
            }
        }
        return false;
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (handler == null) return false;
        for (Fluid fluid : supportedFluids()) {
            if (!isAllowedFluid(fluid)) continue;
            FluidStack drained = handler.drain(new FluidStack(fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.SIMULATE);
            if (drained.getAmount() == FluidType.BUCKET_VOLUME && drained.is(fluid)) return true;
        }
        return false;
        */
        //?}
    }

    public static boolean fill(CheeseMoldBlockEntity mold, Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        if (!canFill(player, hand) || !mold.canAcceptFluid()) return false;
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        for (Fluid fluid : supportedFluids()) {
            if (!isAllowedFluid(fluid)) continue;
            try (Transaction transaction = Transaction.openOuter()) {
                FluidVariant variant = FluidVariant.of(fluid);
                if (storage.extract(variant, FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET) continue;
                if (!mold.fillFluid(fluid)) return false;
                if (!player.getAbilities().instabuild) transaction.commit();
                return true;
            }
        }
        return false;
        //?}
        //? if neoforge {
        /*
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (handler == null) return false;
        for (Fluid fluid : supportedFluids()) {
            if (!isAllowedFluid(fluid)) continue;
            FluidStack request = new FluidStack(fluid, FluidType.BUCKET_VOLUME);
            FluidStack simulated = handler.drain(request, IFluidHandler.FluidAction.SIMULATE);
            if (simulated.getAmount() != FluidType.BUCKET_VOLUME || !simulated.is(fluid)) continue;
            if (player.getAbilities().instabuild) return mold.fillFluid(fluid);
            FluidStack drained = handler.drain(request, IFluidHandler.FluidAction.EXECUTE);
            if (drained.getAmount() != FluidType.BUCKET_VOLUME || !drained.is(fluid)) return false;
            if (!mold.fillFluid(fluid)) return false;
            ItemStack emptied = handler.getContainer();
            if (held.getCount() == 1) player.setItemInHand(hand, emptied);
            else {
                held.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, emptied);
            }
            return true;
        }
        return false;
        */
        //?}
    }

    private static boolean isNamedBucket(ItemStack stack) {
        return stack.is(Items.WATER_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.SHEEP_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.GOAT_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.FRIESIAN_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.HOLSTEIN_MILK_BUCKET)
                || stack.is(net.syrupstudios.animaniax.item.AnimalMilkContent.JERSEY_MILK_BUCKET);
    }

    private static boolean isAllowedFluid(Fluid fluid) {
        return fluid != Fluids.WATER || !AnimalCareConfig.disableSaltCreation;
    }

    private static Fluid[] supportedFluids() {
        return new Fluid[] {Fluids.WATER,
                AnimalMilkFluids.source("friesian"), AnimalMilkFluids.source("holstein"),
                AnimalMilkFluids.source("jersey"), AnimalMilkFluids.source("goat"), AnimalMilkFluids.source("sheep")};
    }
}
