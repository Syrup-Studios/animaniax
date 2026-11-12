package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.food.FoodProperties;

public final class TruffleContent {
    public static final Item TRUFFLE = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(2).saturationModifier(0.7F).build()));
    public static final Item TRUFFLE_SOUP = new BonusEffectFoodItem(new Item.Properties().stacksTo(1).food(
            new FoodProperties.Builder().nutrition(10).saturationModifier(0.6F)
                    .usingConvertsTo(Items.BOWL).build()),
            new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
    public static final Item CHOCOLATE_TRUFFLE = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(6).saturationModifier(0.7F).build()),
            new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 3));

    private TruffleContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "truffle"), TRUFFLE);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "truffle_soup"), TRUFFLE_SOUP);
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "chocolate_truffle"), CHOCOLATE_TRUFFLE);
    }
}
