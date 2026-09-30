package net.syrupstudios.animaniax.entity;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class PetSellerContent {
    public static final ResourceKey<PoiType> POI_KEY = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, id("pet_seller"));
    public static final VillagerProfession PROFESSION = new VillagerProfession("pet_seller",
            poi -> poi.is(POI_KEY), poi -> poi.is(POI_KEY), ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CARTOGRAPHER);

    private static final List<BreedTrade> BREEDS = List.of(
            new BreedTrade(true, "american_shorthair", 1, 10, 20),
            new BreedTrade(true, "ragdoll", 2, 10, 20),
            new BreedTrade(true, "norwegian", 3, 10, 20),
            new BreedTrade(true, "asiatic", 2, 15, 25),
            new BreedTrade(true, "exotic", 3, 15, 25),
            new BreedTrade(true, "tabby", 2, 15, 25),
            new BreedTrade(true, "siamese", 3, 25, 35),
            new BreedTrade(false, "blood_hound", 1, 15, 30),
            new BreedTrade(false, "chihuahua", 2, 20, 30),
            new BreedTrade(false, "collie", 3, 20, 30),
            new BreedTrade(false, "corgi", 1, 15, 25),
            new BreedTrade(false, "dachshund", 2, 10, 20),
            new BreedTrade(false, "german_shepherd", 3, 20, 30),
            new BreedTrade(false, "great_dane", 1, 20, 30),
            new BreedTrade(false, "greyhound", 2, 20, 30),
            new BreedTrade(false, "husky", 3, 20, 25),
            new BreedTrade(false, "labrador", 1, 20, 30),
            new BreedTrade(false, "pomeranian", 2, 15, 25),
            new BreedTrade(false, "poodle", 3, 15, 25),
            new BreedTrade(false, "pug", 1, 15, 25));

    private PetSellerContent() {}

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }

    //? if fabric {
    public static void registerFabric() {
        registerPoi(BuiltInRegistries.POINT_OF_INTEREST_TYPE);
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, id("pet_seller"), PROFESSION);
        Int2ObjectMap<VillagerTrades.ItemListing[]> levels = VillagerTrades.TRADES.computeIfAbsent(PROFESSION, ignored -> new it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap<>());
        for (int level = 1; level <= 3; level++) {
            List<VillagerTrades.ItemListing> listings = listingsForLevel(level);
            levels.put(level, listings.toArray(VillagerTrades.ItemListing[]::new));
        }
    }
    //?}

    public static PoiType registerPoi(Registry<PoiType> registry) {
        return net.syrupstudios.animaniax.mixin.PoiTypesAccessor.animaniax$register(registry, POI_KEY,
                Set.of(net.syrupstudios.animaniax.block.PetBedContent.CAT_BED_1.defaultBlockState(), net.syrupstudios.animaniax.block.PetBedContent.CAT_BED_2.defaultBlockState()), 1, 1);
    }

    public static List<VillagerTrades.ItemListing> listingsForLevel(int level) {
        List<VillagerTrades.ItemListing> listings = new ArrayList<>();
        for (BreedTrade breed : BREEDS) {
            if (breed.level != level) continue;
            addVariantListings(listings, breed, false, breed.min, breed.max);
            addVariantListings(listings, breed, true, breed.min + breed.min / 2, breed.max + breed.max / 2);
        }
        return listings;
    }

    private static void addVariantListings(List<VillagerTrades.ItemListing> listings, BreedTrade breed, boolean child,
            int minPrice, int maxPrice) {
        for (int sex = 0; sex < (child ? 1 : 2); sex++) {
            boolean female = sex == 1;
            listings.add((Entity villager, RandomSource random) -> {
                int price = Mth.nextInt(random, minPrice, maxPrice);
                ItemStack result = new ItemStack(breed.cat ? CatContent.SPAWN_EGG : DogContent.SPAWN_EGG);
                CompoundTag data = new CompoundTag();
                data.putString("id", breed.cat ? "animaniax:cat" : "animaniax:dog");
                data.putString("AnimaniaxBreed", breed.name);
                data.putBoolean("AnimaniaxSexSet", true);
                data.putBoolean("AnimaniaxFemale", child ? random.nextBoolean() : female);
                data.putBoolean("AnimaniaxPlayerInteracted", true);
                if (child) data.putInt("Age", -24_000);
                result.set(DataComponents.ENTITY_DATA, CustomData.of(data));
                String variantName = "animal.animaniax.breed." + (breed.cat ? "cat." : "dog.") + breed.name;
                String label = breed.cat
                        ? child ? "item.animaniax.pet_seller.kitten" : female ? "item.animaniax.pet_seller.cat_female" : "item.animaniax.pet_seller.cat_male"
                        : child ? "item.animaniax.pet_seller.puppy" : female ? "item.animaniax.pet_seller.dog_female" : "item.animaniax.pet_seller.dog_male";
                result.set(DataComponents.ITEM_NAME, Component.translatable(label, Component.translatable(variantName)));
                return new MerchantOffer(new ItemCost(Items.EMERALD, price), result, 12, 1, 0.05F);
            });
        }
    }

    private record BreedTrade(boolean cat, String name, int level, int min, int max) {}
}
