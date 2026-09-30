package net.syrupstudios.animaniax.loader;

//? if fabric {
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.block.HamsterWheelContent;
import net.syrupstudios.animaniax.block.FabricWheelEnergy;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.block.PetBowlContent;
import net.syrupstudios.animaniax.block.PetBedContent;
import net.syrupstudios.animaniax.block.DecorativePropContent;
import net.syrupstudios.animaniax.block.SeedPileContent;
import net.syrupstudios.animaniax.block.StrawContent;
import net.syrupstudios.animaniax.block.MudContent;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.block.CheeseMoldContent;
import net.syrupstudios.animaniax.item.TruffleContent;
import net.syrupstudios.animaniax.item.HamsterFoodContent;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.SlopContent;
import net.syrupstudios.animaniax.item.CheeseWedgeContent;
import net.syrupstudios.animaniax.item.PeafowlContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import net.syrupstudios.animaniax.item.PrimeChickenContent;
import net.syrupstudios.animaniax.item.PrimeMuttonContent;
import net.syrupstudios.animaniax.item.GoatMeatContent;
import net.syrupstudios.animaniax.item.PrimeRabbitContent;
import net.syrupstudios.animaniax.item.PrimeMeatContent;
import net.syrupstudios.animaniax.block.SheepWoolContent;
import net.syrupstudios.animaniax.item.HamsterCarryContent;
import net.syrupstudios.animaniax.item.HamsterBallContent;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.DogContent;
import net.syrupstudios.animaniax.entity.CatContent;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.item.RidingCropContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");

    @Override
    public void onInitialize() {
        net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.register();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries ->
                BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && !(item instanceof SpawnEggItem) && item != AnimalSpawnEggContent.ITEM).forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
                BuiltInRegistries.ITEM.stream().filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && item instanceof SpawnEggItem).forEach(entries::accept));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SPAWN_EGGS).register(entries ->
                AnimalSpawnEggContent.ITEM.creativeForms().forEach(entries::accept));
        TroughContent.registerFabric();
        PetBowlContent.registerFabric();
        PetBedContent.registerFabric();
        DecorativePropContent.registerFabric();
        SeedPileContent.registerFabric();
        StrawContent.registerFabric();
        MudContent.registerFabric();
        SaltLickContent.registerFabric();
        CheeseMoldContent.registerFabric();
        NestContent.registerFabric();
        HamsterWheelContent.registerFabric();
        FabricWheelEnergy.register();
        net.syrupstudios.animaniax.block.AnimalMilkFluids.registerFabric();
        FluidStorage.GENERAL_COMBINED_PROVIDER.register(context ->
                context.getItemVariant().getItem() == net.minecraft.world.item.Items.MILK_BUCKET
                        ? new FullItemFluidStorage(context, variant -> ItemVariant.of(net.minecraft.world.item.Items.BUCKET),
                                FluidVariant.of(net.syrupstudios.animaniax.item.MilkContainerTransfer.genericMilkFluid()),
                                FluidConstants.BUCKET)
                        : null);
        TruffleContent.registerFabric();
        net.syrupstudios.animaniax.item.OmeletteContent.registerFabric();
        HamsterFoodContent.registerFabric();
        AnimalMilkContent.registerFabric();
        AnimalSpawnEggContent.registerFabric();
        SheepWoolContent.registerFabric();
        SlopContent.registerFabric();
        CheeseWedgeContent.registerFabric();
        RidingCropContent.registerFabric();
        PeafowlContent.registerFabric();
        BrownEggContent.registerFabric();
        PrimeChickenContent.registerFabric();
        PrimeMuttonContent.registerFabric();
        GoatMeatContent.registerFabric();
        PrimeRabbitContent.registerFabric();
        PrimeMeatContent.registerFabric();
        HamsterCarryContent.registerFabric();
        HamsterBallContent.registerFabric();
        DraftHorseContent.registerFabric();
        PeafowlEntity.registerFabric();
        HamsterEntity.registerFabric();
        DogContent.registerFabric();
        CatContent.registerFabric();
        net.syrupstudios.animaniax.entity.PetSellerContent.registerFabric();
        SpawnPlacements.register(DraftHorseContent.DRAFT_HORSE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(PeafowlEntity.TYPE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(DogContent.TYPE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(CatContent.TYPE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(HamsterEntity.TYPE, SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Animal::checkAnimalSpawnRules);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath("animaniax", "cow_spawns"))),
                MobCategory.CREATURE, EntityType.COW, 8, 4, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME,
                ResourceLocation.fromNamespaceAndPath("animaniax", "goat_spawns"))),
                MobCategory.CREATURE, EntityType.GOAT, 8, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME,
                DraftHorseContent.id("draft_horse_spawns"))), MobCategory.CREATURE,
                DraftHorseContent.DRAFT_HORSE, 8, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME,
                PeafowlEntity.id("peafowl_spawns"))), MobCategory.CREATURE,
                PeafowlEntity.TYPE, 8, 1, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME, DogContent.id("dog_forest_spawns"))),
                MobCategory.CREATURE, DogContent.TYPE, 5, 2, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME, DogContent.id("dog_mountain_spawns"))),
                MobCategory.CREATURE, DogContent.TYPE, 4, 2, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME, DogContent.id("dog_snow_spawns"))),
                MobCategory.CREATURE, DogContent.TYPE, 5, 2, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME, CatContent.id("cat_spawns"))),
                MobCategory.CREATURE, CatContent.TYPE, 5, 2, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagKey.create(Registries.BIOME,
                HamsterEntity.id("hamster_spawns"))), MobCategory.CREATURE,
                HamsterEntity.TYPE, 6, 2, 4);
        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            AnimalCareConfig.load(server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/animaniax.properties"));
            TroughContent.registerWaterBucketBehavior();
            SeedPileContent.registerDispenserBehaviors();
        });
        LOGGER.info("Animaniax initialized on Fabric");
    }
}
//?}
