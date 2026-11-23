package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if fabric {
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
//?}
//? if neoforge {
/*
import net.minecraft.world.entity.LivingEntity;
*///?}

@Mixin(Wolf.class)
abstract class WolfFoodOverrideMixin {
    //? if fabric {
    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    private Object animaniax$foodValue(ItemStack stack, DataComponentType<?> component) {
        Object current = stack.get(component);
        return component == DataComponents.FOOD && current instanceof FoodProperties food
                ? AnimalCareConfig.foodProperties(stack, food) : current;
    }
    //?}
    //? if neoforge {
    /*
    @Redirect(method = "mobInteract", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/world/item/ItemStack;getFoodProperties(Lnet/minecraft/world/entity/LivingEntity;)Lnet/minecraft/world/food/FoodProperties;"))
    private FoodProperties animaniax$foodValue(ItemStack stack, LivingEntity entity) {
        return AnimalCareConfig.foodProperties(stack, stack.getFoodProperties(entity));
    }
    *///?}
}
