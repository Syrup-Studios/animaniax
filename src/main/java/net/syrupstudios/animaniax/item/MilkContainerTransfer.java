package net.syrupstudios.animaniax.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
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
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
*///?}

public final class MilkContainerTransfer {
    private MilkContainerTransfer() {}

    public static boolean canFill(Player player, InteractionHand hand, String kind) {
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.BUCKET)) return false;
        if (held.is(Items.GLASS_BOTTLE)) return !kind.equals("lava");
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        try (Transaction transaction = Transaction.openOuter()) {
            return storage.insert(FluidVariant.of(fluid(kind)),
                    FluidConstants.BUCKET, transaction) == FluidConstants.BUCKET;
        }
        //?}
        //? if neoforge {
        /*
        ItemStack one = player.getItemInHand(hand).copyWithCount(1);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(one).orElse(null);
        if (handler == null) return false;
        FluidStack milk = new FluidStack(fluid(kind), FluidType.BUCKET_VOLUME);
        FluidTank source = new FluidTank(FluidType.BUCKET_VOLUME);
        source.fill(milk, FluidAction.EXECUTE);
        FluidStack moved = FluidUtil.tryFluidTransfer(handler, source, milk, false);
        return moved.getAmount() == FluidType.BUCKET_VOLUME && FluidStack.isSameFluidSameComponents(moved, milk);
        *///?}
    }

    public static boolean fill(Player player, InteractionHand hand, String kind) {
        if (!canFill(player, hand, kind)) return false;
        ItemStack held = player.getItemInHand(hand);
        if (held.is(Items.GLASS_BOTTLE) && !kind.equals("lava")) {
            ContainerUtils.fillGlassBottle(player, hand, held,
                    new ItemStack(AnimalMilkContent.MILK_BOTTLE));
            return true;
        }
        //? if fabric {
        Storage<FluidVariant> storage = ContainerItemContext.forPlayerInteraction(player, hand).find(FluidStorage.ITEM);
        if (storage == null) return false;
        try (Transaction transaction = Transaction.openOuter()) {
            if (storage.insert(FluidVariant.of(fluid(kind)),
                    FluidConstants.BUCKET, transaction) != FluidConstants.BUCKET) return false;
            transaction.commit();
            return true;
        }
        //?}
        //? if neoforge {
        /*
        FluidStack milk = new FluidStack(fluid(kind), FluidType.BUCKET_VOLUME);
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(held.copyWithCount(1)).orElse(null);
        if (handler == null) return false;
        FluidTank source = new FluidTank(FluidType.BUCKET_VOLUME);
        source.fill(milk, FluidAction.EXECUTE);
        FluidStack moved = FluidUtil.tryFluidTransfer(handler, source, milk, true);
        if (moved.getAmount() != FluidType.BUCKET_VOLUME || !FluidStack.isSameFluidSameComponents(moved, milk)) return false;
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, handler.getContainer()));
        return true;
        *///?}
    }

    public static Fluid genericMilkFluid() {
        return fluid("generic");
    }

    private static Fluid fluid(String kind) {
        if (kind.equals("lava")) return Fluids.LAVA;
        if (!kind.equals("generic")) return net.syrupstudios.animaniax.block.AnimalMilkFluids.source(kind);
        //? if fabric {
        for (String id : new String[] {"porting_lib:milk", "milk:still_milk"}) {
            Fluid registered = BuiltInRegistries.FLUID.getOptional(ResourceLocation.parse(id)).orElse(null);
            if (registered != null) return registered;
        }
        return net.syrupstudios.animaniax.block.AnimalMilkFluids.source(kind);
        //?}
        //? if neoforge {
        /*
        return net.neoforged.neoforge.common.NeoForgeMod.MILK.value();
        *///?}
    }
}
