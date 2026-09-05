package net.syrupstudios.animaniax.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.syrupstudios.animaniax.entity.PeafowlEntity;

public final class PeafowlContent {
    public static final Item RAW_PEACOCK = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F)
                    .build()), new MobEffectInstance(MobEffects.CONFUSION, 200, 3));
    public static final Item COOKED_PEACOCK = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(6).saturationModifier(0.5F)
                    .build()), new MobEffectInstance(MobEffects.LUCK, 600, 0));
    public static final Item RAW_PRIME_PEACOCK = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(1).saturationModifier(1.0F)
                    .build()), new MobEffectInstance(MobEffects.CONFUSION, 200, 3));
    public static final Item COOKED_PRIME_PEACOCK = new BonusEffectFoodItem(new Item.Properties().food(
            new FoodProperties.Builder().nutrition(9).saturationModifier(0.5F)
                    .build()), new MobEffectInstance(MobEffects.LUCK, 1200, 1));
    public static final Item PEACOCK_EGG_BLUE = new Item(new Item.Properties().stacksTo(16));
    public static final Item PEACOCK_EGG_WHITE = new Item(new Item.Properties().stacksTo(16));
    public static final Item[] FEATHERS = new Item[7];
    private static final String[] MORPHS = {"blue", "white", "taupe", "purple", "peach", "opal", "charcoal"};

    static { for (int i = 0; i < MORPHS.length; i++) FEATHERS[i] = new Item(new Item.Properties()); }
    private PeafowlContent() {}

    public static Item feather(String morph) {
        for (int i = 0; i < MORPHS.length; i++) if (MORPHS[i].equalsIgnoreCase(morph)) return FEATHERS[i];
        return FEATHERS[0];
    }

    public static ItemStack egg(String morph) {
        String validMorph = PeafowlEntity.MORPHS.stream().filter(value -> value.equalsIgnoreCase(morph))
                .findFirst().orElse(PeafowlEntity.MORPHS.get(0));
        ItemStack egg = new ItemStack(validMorph.equals("Blue") ? PEACOCK_EGG_BLUE : PEACOCK_EGG_WHITE);
        CompoundTag tag = new CompoundTag();
        tag.putString("Morph", validMorph);
        egg.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return egg;
    }

    public static ItemStack eggForBreed(String breed) {
        return breed.startsWith("peafowl:") ? egg(breed.substring("peafowl:".length())) : new ItemStack(net.minecraft.world.item.Items.EGG);
    }

    public static void registerFabric() {
        register("raw_peacock", RAW_PEACOCK); register("cooked_peacock", COOKED_PEACOCK);
        register("raw_prime_peacock", RAW_PRIME_PEACOCK); register("cooked_prime_peacock", COOKED_PRIME_PEACOCK);
        register("peacock_egg_blue", PEACOCK_EGG_BLUE); register("peacock_egg_white", PEACOCK_EGG_WHITE);
        for (int i = 0; i < MORPHS.length; i++) register(MORPHS[i] + "_peafowl_feather", FEATHERS[i]);
    }

    private static void register(String path, Item item) {
        Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", path), item);
    }
}
