package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.DogContent;
import net.syrupstudios.animaniax.entity.DogEntity;
import net.syrupstudios.animaniax.entity.CatContent;
import net.syrupstudios.animaniax.entity.CompanionCatEntity;
import net.syrupstudios.animaniax.entity.DraftHorseEntity;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.HamsterEntity;
import net.syrupstudios.animaniax.entity.FerretContent;
import net.syrupstudios.animaniax.entity.FerretEntity;
import net.syrupstudios.animaniax.entity.HedgehogContent;
import net.syrupstudios.animaniax.entity.HedgehogEntity;
import net.syrupstudios.animaniax.entity.FrogContent;
import net.syrupstudios.animaniax.item.FrogLegContent;
import net.syrupstudios.animaniax.item.AnimalSpawnEggContent;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.syrupstudios.animaniax.block.HamsterWheelBlockEntity;
import net.syrupstudios.animaniax.block.HamsterWheelContent;
import net.syrupstudios.animaniax.block.BeeHiveContent;
import net.syrupstudios.animaniax.block.BeeHiveNeoForgeFluidHandler;
import net.syrupstudios.animaniax.worldgen.BeeHiveFeature;
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
import net.syrupstudios.animaniax.item.OmeletteContent;
import net.syrupstudios.animaniax.item.HamsterFoodContent;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.SlopContent;
import net.syrupstudios.animaniax.item.SlopRecipe;
import net.syrupstudios.animaniax.item.CheeseWedgeContent;
import net.syrupstudios.animaniax.item.PeafowlContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import net.syrupstudios.animaniax.item.PrimeChickenContent;
import net.syrupstudios.animaniax.item.PrimeMuttonContent;
import net.syrupstudios.animaniax.item.GoatMeatContent;
import net.syrupstudios.animaniax.item.PrimeRabbitContent;
import net.syrupstudios.animaniax.item.PrimeMeatContent;
import net.syrupstudios.animaniax.block.SheepWoolContent;
import net.syrupstudios.animaniax.item.PrimeMeatCuttingRecipe;
import net.syrupstudios.animaniax.item.HamsterCarryContent;
import net.syrupstudios.animaniax.item.FerretCarryContent;
import net.syrupstudios.animaniax.item.HedgehogCarryContent;
import net.syrupstudios.animaniax.item.HamsterBallContent;
import net.minecraft.world.item.DyeColor;
import net.syrupstudios.animaniax.item.CheeseCuttingRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("animaniax")
public final class NeoForgeEntrypoint {
    private static final Logger LOGGER = LoggerFactory.getLogger("animaniax");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(Registries.BLOCK, "animaniax");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(Registries.ITEM, "animaniax");
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "animaniax");
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, "animaniax");
    public static final DeferredRegister<net.minecraft.world.level.material.Fluid> FLUIDS = DeferredRegister.create(Registries.FLUID, "animaniax");
    public static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> HONEY_TYPE = FLUID_TYPES.register("honey",
            () -> new FluidType(FluidType.Properties.create().density(3000).viscosity(7000)) {
                @Override public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
                        @Override public net.minecraft.resources.ResourceLocation getStillTexture() { return net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still"); }
                        @Override public net.minecraft.resources.ResourceLocation getFlowingTexture() { return net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_flow"); }
                        @Override public int getTintColor() { return 0xFFFFC943; }
                    });
                }
            });
    public static net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> milkType(String kind) {
        return switch (kind) {
            case "friesian" -> FRIESIAN_MILK_TYPE;
            case "holstein" -> HOLSTEIN_MILK_TYPE;
            case "jersey" -> JERSEY_MILK_TYPE;
            case "goat" -> GOAT_MILK_TYPE;
            case "sheep" -> SHEEP_MILK_TYPE;
            default -> throw new IllegalArgumentException(kind);
        };
    }
    private static net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> registerMilkType(String kind) {
        return FLUID_TYPES.register("milk_" + kind, () -> new FluidType(FluidType.Properties.create().density(1030).viscosity(1800)) {
            @Override public void initializeClient(java.util.function.Consumer<net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions> consumer) {
                consumer.accept(new net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions() {
                    @Override public net.minecraft.resources.ResourceLocation getStillTexture() { return net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_still"); }
                    @Override public net.minecraft.resources.ResourceLocation getFlowingTexture() { return net.minecraft.resources.ResourceLocation.withDefaultNamespace("block/water_flow"); }
                    @Override public int getTintColor() { return net.syrupstudios.animaniax.block.AnimalMilkFluids.tint(kind); }
                });
            }
        });
    }
    private static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> FRIESIAN_MILK_TYPE = registerMilkType("friesian");
    private static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> HOLSTEIN_MILK_TYPE = registerMilkType("holstein");
    private static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> JERSEY_MILK_TYPE = registerMilkType("jersey");
    private static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> GOAT_MILK_TYPE = registerMilkType("goat");
    private static final net.neoforged.neoforge.registries.DeferredHolder<FluidType, FluidType> SHEEP_MILK_TYPE = registerMilkType("sheep");
    private static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "animaniax");
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "animaniax");
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "animaniax");
    private static final DeferredRegister<net.minecraft.advancements.CriterionTrigger<?>> TRIGGERS = DeferredRegister.create(Registries.TRIGGER_TYPE, "animaniax");

    public NeoForgeEntrypoint(IEventBus modBus) {
        TRIGGERS.register("feed_animal", () -> net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.INSTANCE);
        TRIGGERS.register(modBus);
        net.neoforged.neoforge.common.NeoForgeMod.enableMilkFluid();
        FLUID_TYPES.register(modBus);
        FLUIDS.register("honey", () -> BeeHiveContent.HONEY_SOURCE);
        FLUIDS.register("flowing_honey", () -> BeeHiveContent.HONEY_FLOWING);
        registerMilkFluid("friesian");
        registerMilkFluid("holstein");
        registerMilkFluid("jersey");
        registerMilkFluid("goat");
        registerMilkFluid("sheep");
        FLUIDS.register(modBus);
        FEATURES.register("wild_hive", () -> BeeHiveFeature.INSTANCE);
        ENTITY_TYPES.register("draft_horse", () -> DraftHorseContent.DRAFT_HORSE);
        ITEMS.register("draft_horse_spawn_egg", () -> DraftHorseContent.DRAFT_HORSE_SPAWN_EGG);
        ENTITY_TYPES.register("peafowl", () -> PeafowlEntity.TYPE);
        ITEMS.register("peafowl_spawn_egg", () -> PeafowlEntity.SPAWN_EGG);
        ENTITY_TYPES.register("hamster", () -> HamsterEntity.TYPE);
        ENTITY_TYPES.register("ferret_grey", () -> FerretContent.GREY);
        ITEMS.register("ferret_grey_spawn_egg", () -> FerretContent.GREY_SPAWN_EGG);
        ENTITY_TYPES.register("ferret_white", () -> FerretContent.WHITE);
        ITEMS.register("ferret_white_spawn_egg", () -> FerretContent.WHITE_SPAWN_EGG);
        ENTITY_TYPES.register("dog", () -> DogContent.TYPE);
        ITEMS.register("dog_spawn_egg", () -> DogContent.SPAWN_EGG);
        ENTITY_TYPES.register("cat", () -> CatContent.TYPE);
        ITEMS.register("cat_spawn_egg", () -> CatContent.SPAWN_EGG);
        ENTITY_TYPES.register("hedgehog", () -> HedgehogContent.HEDGEHOG);
        ITEMS.register("hedgehog_spawn_egg", () -> HedgehogContent.HEDGEHOG_SPAWN_EGG);
        ENTITY_TYPES.register("hedgehog_albino", () -> HedgehogContent.HEDGEHOG_ALBINO);
        ITEMS.register("hedgehog_albino_spawn_egg", () -> HedgehogContent.HEDGEHOG_ALBINO_SPAWN_EGG);
        ITEMS.register("hamster_spawn_egg", () -> HamsterEntity.SPAWN_EGG);
        ENTITY_TYPES.register("common_frog", () -> FrogContent.COMMON_FROG);
        ITEMS.register("common_frog_spawn_egg", () -> FrogContent.COMMON_FROG_SPAWN_EGG);
        ENTITY_TYPES.register("dart_frog", () -> FrogContent.DART_FROG);
        ITEMS.register("dart_frog_spawn_egg", () -> FrogContent.DART_FROG_SPAWN_EGG);
        ENTITY_TYPES.register("toad", () -> FrogContent.TOAD);
        ITEMS.register("toad_spawn_egg", () -> FrogContent.TOAD_SPAWN_EGG);
        ITEMS.register("animal_spawn_egg", () -> AnimalSpawnEggContent.ITEM);
        ITEMS.register("carried_hamster", () -> HamsterCarryContent.CARRIED_HAMSTER);
        ITEMS.register("carried_ferret", () -> FerretCarryContent.CARRIED_FERRET);
        ITEMS.register("carried_hedgehog", () -> HedgehogCarryContent.CARRIED_HEDGEHOG);
        ITEMS.register("hamster_ball", () -> HamsterBallContent.CLEAR);
        for (DyeColor color : DyeColor.values()) ITEMS.register("hamster_ball_" + color.getName(), () -> HamsterBallContent.COLORED[color.getId()]);
        ITEMS.register("hamster_food", () -> HamsterFoodContent.HAMSTER_FOOD);
        ITEMS.register("raw_frog_legs", () -> FrogLegContent.RAW_FROG_LEGS);
        ITEMS.register("cooked_frog_legs", () -> FrogLegContent.COOKED_FROG_LEGS);
        modBus.addListener(this::registerEntityAttributes);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(this::addCreativeItems);
        BLOCKS.register("salt_lick", () -> SaltLickContent.BLOCK);
        BLOCKS.register("dorset_brown_wool", () -> SheepWoolContent.DORSET_BROWN_BLOCK);
        BLOCKS.register("friesian_black_wool", () -> SheepWoolContent.FRIESIAN_BLACK_BLOCK);
        BLOCKS.register("friesian_brown_wool", () -> SheepWoolContent.FRIESIAN_BROWN_BLOCK);
        BLOCKS.register("jacob_wool", () -> SheepWoolContent.JACOB_BLOCK);
        BLOCKS.register("merino_brown_wool", () -> SheepWoolContent.MERINO_BROWN_BLOCK);
        BLOCKS.register("merino_white_wool", () -> SheepWoolContent.MERINO_WHITE_BLOCK);
        BLOCKS.register("suffolk_brown_wool", () -> SheepWoolContent.SUFFOLK_BROWN_BLOCK);
        ITEMS.register("salt_lick", () -> SaltLickContent.ITEM);
        BLOCK_ENTITIES.register("salt_lick", () -> SaltLickContent.ENTITY);
        BLOCKS.register("bee_hive", () -> BeeHiveContent.PLAYER_HIVE);
        BLOCKS.register("wild_hive", () -> BeeHiveContent.WILD);
        BLOCKS.register("honey", () -> BeeHiveContent.HONEY_BLOCK);
        ITEMS.register("bee_hive", () -> BeeHiveContent.PLAYER_HIVE_ITEM);
        ITEMS.register("wild_hive", () -> BeeHiveContent.WILD_ITEM);
        ITEMS.register("honey_bottle", () -> BeeHiveContent.HONEY_BOTTLE);
        ITEMS.register("honey_bucket", () -> BeeHiveContent.HONEY_BUCKET);
        BLOCK_ENTITIES.register("bee_hive", () -> BeeHiveContent.ENTITY);
        BLOCKS.register("cheese_mold", () -> CheeseMoldContent.MOLD);
        BLOCKS.register("friesian_cheese", () -> CheeseMoldContent.FRIESIAN_CHEESE_BLOCK);
        BLOCKS.register("holstein_cheese", () -> CheeseMoldContent.HOLSTEIN_CHEESE_BLOCK);
        BLOCKS.register("jersey_cheese", () -> CheeseMoldContent.JERSEY_CHEESE_BLOCK);
        BLOCKS.register("goat_cheese", () -> CheeseMoldContent.GOAT_CHEESE_BLOCK);
        BLOCKS.register("sheep_cheese", () -> CheeseMoldContent.SHEEP_CHEESE_BLOCK);
        ITEMS.register("cheese_mold", () -> CheeseMoldContent.MOLD_ITEM);
        ITEMS.register("friesian_cheese", () -> CheeseMoldContent.FRIESIAN_CHEESE);
        ITEMS.register("holstein_cheese", () -> CheeseMoldContent.HOLSTEIN_CHEESE);
        ITEMS.register("jersey_cheese", () -> CheeseMoldContent.JERSEY_CHEESE);
        ITEMS.register("goat_cheese", () -> CheeseMoldContent.GOAT_CHEESE);
        ITEMS.register("sheep_cheese", () -> CheeseMoldContent.SHEEP_CHEESE);
        ITEMS.register("salt", () -> CheeseMoldContent.SALT);
        ITEMS.register("slop_bucket", () -> SlopContent.SLOP_BUCKET);
        ITEMS.register("sheep_milk_bucket", () -> AnimalMilkContent.SHEEP_MILK_BUCKET);
        ITEMS.register("goat_milk_bucket", () -> AnimalMilkContent.GOAT_MILK_BUCKET);
        ITEMS.register("friesian_milk_bucket", () -> AnimalMilkContent.FRIESIAN_MILK_BUCKET);
        ITEMS.register("holstein_milk_bucket", () -> AnimalMilkContent.HOLSTEIN_MILK_BUCKET);
        ITEMS.register("jersey_milk_bucket", () -> AnimalMilkContent.JERSEY_MILK_BUCKET);
        ITEMS.register("milk_bottle", () -> AnimalMilkContent.MILK_BOTTLE);
        ITEMS.register("dorset_brown_wool", () -> SheepWoolContent.DORSET_BROWN);
        ITEMS.register("friesian_black_wool", () -> SheepWoolContent.FRIESIAN_BLACK);
        ITEMS.register("friesian_brown_wool", () -> SheepWoolContent.FRIESIAN_BROWN);
        ITEMS.register("jacob_wool", () -> SheepWoolContent.JACOB);
        ITEMS.register("merino_brown_wool", () -> SheepWoolContent.MERINO_BROWN);
        ITEMS.register("merino_white_wool", () -> SheepWoolContent.MERINO_WHITE);
        ITEMS.register("suffolk_brown_wool", () -> SheepWoolContent.SUFFOLK_BROWN);
        BLOCK_ENTITIES.register("cheese_mold", () -> CheeseMoldContent.MOLD_ENTITY);
        BLOCKS.register("trough", () -> TroughContent.TROUGH);
        BLOCKS.register("pet_bowl", () -> PetBowlContent.BLOCK);
        BLOCKS.register("cat_bed_1", () -> PetBedContent.CAT_BED_1);
        BLOCKS.register("cat_bed_2", () -> PetBedContent.CAT_BED_2);
        BLOCKS.register("dog_pillow", () -> PetBedContent.DOG_PILLOW);
        BLOCKS.register("cat_tower", () -> DecorativePropContent.CAT_TOWER);
        BLOCKS.register("dog_house", () -> DecorativePropContent.DOG_HOUSE);
        BLOCKS.register("litter_box", () -> DecorativePropContent.LITTER_BOX);
        BLOCKS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT);
        BLOCKS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN);
        BLOCKS.register("melon_seed_pile", () -> SeedPileContent.MELON);
        BLOCKS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT);
        BLOCKS.register("straw", () -> StrawContent.BLOCK);
        BLOCKS.register("mud", () -> MudContent.BLOCK);
        ITEMS.register("trough", () -> TroughContent.TROUGH_ITEM);
        ITEMS.register("pet_bowl", () -> PetBowlContent.ITEM);
        ITEMS.register("cat_bed_1", () -> PetBedContent.CAT_BED_1_ITEM);
        ITEMS.register("cat_bed_2", () -> PetBedContent.CAT_BED_2_ITEM);
        ITEMS.register("dog_pillow", () -> PetBedContent.DOG_PILLOW_ITEM);
        ITEMS.register("cat_tower", () -> DecorativePropContent.CAT_TOWER_ITEM);
        ITEMS.register("dog_house", () -> DecorativePropContent.DOG_HOUSE_ITEM);
        ITEMS.register("litter_box", () -> DecorativePropContent.LITTER_BOX_ITEM);
        ITEMS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT_ITEM);
        ITEMS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN_ITEM);
        ITEMS.register("melon_seed_pile", () -> SeedPileContent.MELON_ITEM);
        ITEMS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT_ITEM);
        ITEMS.register("straw", () -> StrawContent.ITEM);
        ITEMS.register("mud", () -> MudContent.ITEM);
        BLOCK_ENTITIES.register("trough", () -> TroughContent.TROUGH_ENTITY);
        BLOCK_ENTITIES.register("pet_bowl", () -> PetBowlContent.BLOCK_ENTITY);
        BLOCK_ENTITIES.register("nest", () -> NestContent.NEST_ENTITY);
        BLOCKS.register("hamster_wheel", () -> HamsterWheelContent.WHEEL);
        ITEMS.register("hamster_wheel", () -> HamsterWheelContent.ITEM);
        BLOCK_ENTITIES.register("hamster_wheel", () -> HamsterWheelContent.ENTITY);
        BLOCKS.register("nest", () -> NestContent.NEST);
        ITEMS.register("nest", () -> NestContent.NEST_ITEM);
        ITEMS.register("truffle", () -> TruffleContent.TRUFFLE);
        ITEMS.register("truffle_soup", () -> TruffleContent.TRUFFLE_SOUP);
        ITEMS.register("chocolate_truffle", () -> TruffleContent.CHOCOLATE_TRUFFLE);
        ITEMS.register("plain_omelette", () -> OmeletteContent.PLAIN);
        ITEMS.register("cheese_omelette", () -> OmeletteContent.CHEESE);
        ITEMS.register("bacon_omelette", () -> OmeletteContent.BACON);
        ITEMS.register("truffle_omelette", () -> OmeletteContent.TRUFFLE);
        ITEMS.register("super_omelette", () -> OmeletteContent.SUPER);
        RECIPE_SERIALIZERS.register("friesian_cheese_cutting", () -> CheeseCuttingRecipe.FRIESIAN_SERIALIZER);
        RECIPE_SERIALIZERS.register("holstein_cheese_cutting", () -> CheeseCuttingRecipe.HOLSTEIN_SERIALIZER);
        RECIPE_SERIALIZERS.register("jersey_cheese_cutting", () -> CheeseCuttingRecipe.JERSEY_SERIALIZER);
        RECIPE_SERIALIZERS.register("goat_cheese_cutting", () -> CheeseCuttingRecipe.GOAT_SERIALIZER);
        RECIPE_SERIALIZERS.register("sheep_cheese_cutting", () -> CheeseCuttingRecipe.SHEEP_SERIALIZER);
        RECIPE_SERIALIZERS.register("prime_beef_cutting", () -> PrimeMeatCuttingRecipe.BEEF_SERIALIZER);
        RECIPE_SERIALIZERS.register("prime_pork_cutting", () -> PrimeMeatCuttingRecipe.PORK_SERIALIZER);
        RECIPE_SERIALIZERS.register("straw_cutting", () -> PrimeMeatCuttingRecipe.STRAW_SERIALIZER);
        RECIPE_SERIALIZERS.register("slop", () -> SlopRecipe.SERIALIZER);
        RECIPE_SERIALIZERS.register("milk_conversion", () -> net.syrupstudios.animaniax.item.MilkConversionRecipe.SERIALIZER);
        ITEMS.register("carving_knife", () -> CheeseWedgeContent.KNIFE);
        ITEMS.register("riding_crop", () -> net.syrupstudios.animaniax.item.RidingCropContent.RIDING_CROP);
        ITEMS.register("friesian_cheese_wedge", () -> CheeseWedgeContent.FRIESIAN_WEDGE);
        ITEMS.register("holstein_cheese_wedge", () -> CheeseWedgeContent.HOLSTEIN_WEDGE);
        ITEMS.register("jersey_cheese_wedge", () -> CheeseWedgeContent.JERSEY_WEDGE);
        ITEMS.register("goat_cheese_wedge", () -> CheeseWedgeContent.GOAT_WEDGE);
        ITEMS.register("sheep_cheese_wedge", () -> CheeseWedgeContent.SHEEP_WEDGE);
        ITEMS.register("raw_peacock", () -> PeafowlContent.RAW_PEACOCK);
        ITEMS.register("cooked_peacock", () -> PeafowlContent.COOKED_PEACOCK);
        ITEMS.register("raw_prime_peacock", () -> PeafowlContent.RAW_PRIME_PEACOCK);
        ITEMS.register("cooked_prime_peacock", () -> PeafowlContent.COOKED_PRIME_PEACOCK);
        ITEMS.register("peacock_egg_blue", () -> PeafowlContent.PEACOCK_EGG_BLUE);
        ITEMS.register("peacock_egg_white", () -> PeafowlContent.PEACOCK_EGG_WHITE);
        ITEMS.register("brown_egg", () -> BrownEggContent.BROWN_EGG);
        ITEMS.register("blue_peafowl_feather", () -> PeafowlContent.FEATHERS[0]);
        ITEMS.register("white_peafowl_feather", () -> PeafowlContent.FEATHERS[1]);
        ITEMS.register("taupe_peafowl_feather", () -> PeafowlContent.FEATHERS[2]);
        ITEMS.register("purple_peafowl_feather", () -> PeafowlContent.FEATHERS[3]);
        ITEMS.register("peach_peafowl_feather", () -> PeafowlContent.FEATHERS[4]);
        ITEMS.register("opal_peafowl_feather", () -> PeafowlContent.FEATHERS[5]);
        ITEMS.register("charcoal_peafowl_feather", () -> PeafowlContent.FEATHERS[6]);
        ITEMS.register("raw_prime_chicken", () -> PrimeChickenContent.RAW_PRIME_CHICKEN);
        ITEMS.register("cooked_prime_chicken", () -> PrimeChickenContent.COOKED_PRIME_CHICKEN);
        ITEMS.register("raw_prime_mutton", () -> PrimeMuttonContent.RAW_PRIME_MUTTON);
        ITEMS.register("cooked_prime_mutton", () -> PrimeMuttonContent.COOKED_PRIME_MUTTON);
        ITEMS.register("raw_chevon", () -> GoatMeatContent.RAW_CHEVON);
        ITEMS.register("cooked_chevon", () -> GoatMeatContent.COOKED_CHEVON);
        ITEMS.register("raw_prime_chevon", () -> GoatMeatContent.RAW_PRIME_CHEVON);
        ITEMS.register("cooked_prime_chevon", () -> GoatMeatContent.COOKED_PRIME_CHEVON);
        ITEMS.register("raw_prime_rabbit", () -> PrimeRabbitContent.RAW_PRIME_RABBIT);
        ITEMS.register("cooked_prime_rabbit", () -> PrimeRabbitContent.COOKED_PRIME_RABBIT);
        ITEMS.register("raw_prime_beef", () -> PrimeMeatContent.RAW_PRIME_BEEF);
        ITEMS.register("cooked_prime_beef", () -> PrimeMeatContent.COOKED_PRIME_BEEF);
        ITEMS.register("raw_prime_steak", () -> PrimeMeatContent.RAW_PRIME_STEAK);
        ITEMS.register("cooked_prime_steak", () -> PrimeMeatContent.COOKED_PRIME_STEAK);
        ITEMS.register("raw_prime_pork", () -> PrimeMeatContent.RAW_PRIME_PORK);
        ITEMS.register("cooked_prime_pork", () -> PrimeMeatContent.COOKED_PRIME_PORK);
        ITEMS.register("raw_prime_bacon", () -> PrimeMeatContent.RAW_PRIME_BACON);
        ITEMS.register("cooked_prime_bacon", () -> PrimeMeatContent.COOKED_PRIME_BACON);
        ITEMS.register("raw_horse", () -> PrimeMeatContent.RAW_HORSE);
        ITEMS.register("cooked_horse", () -> PrimeMeatContent.COOKED_HORSE);
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BLOCK_ENTITIES.register(modBus);
        FEATURES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        ENTITY_TYPES.register(modBus);
        modBus.addListener((net.neoforged.neoforge.registries.RegisterEvent event) -> {
            if (event.getRegistryKey().equals(Registries.POINT_OF_INTEREST_TYPE))
                event.register(Registries.POINT_OF_INTEREST_TYPE, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("animaniax", "pet_seller"),
                        () -> new net.minecraft.world.entity.ai.village.poi.PoiType(
                                java.util.Set.of(net.syrupstudios.animaniax.block.PetBedContent.CAT_BED_1.defaultBlockState(), net.syrupstudios.animaniax.block.PetBedContent.CAT_BED_2.defaultBlockState()), 1, 1));
            if (event.getRegistryKey().equals(Registries.VILLAGER_PROFESSION))
                event.register(Registries.VILLAGER_PROFESSION, net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("animaniax", "pet_seller"),
                        () -> net.syrupstudios.animaniax.entity.PetSellerContent.PROFESSION);
        });
        NeoForge.EVENT_BUS.addListener((VillagerTradesEvent event) -> {
            if (event.getType() != net.syrupstudios.animaniax.entity.PetSellerContent.PROFESSION) return;
            for (int level = 1; level <= 3; level++)
                event.getTrades().get(level).addAll(net.syrupstudios.animaniax.entity.PetSellerContent.listingsForLevel(level));
        });
        NeoForge.EVENT_BUS.addListener((ServerAboutToStartEvent event) -> {
            AnimalCareConfig.load(event.getServer().getWorldPath(LevelResource.ROOT).resolve("serverconfig/animaniax.properties"));
        });
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            TroughContent.registerWaterBucketBehavior();
            SeedPileContent.registerDispenserBehaviors();
        });
        LOGGER.info("Animaniax initialized on NeoForge");
    }

    private static void registerMilkFluid(String kind) {
        FLUIDS.register("milk_" + kind, () -> net.syrupstudios.animaniax.block.AnimalMilkFluids.source(kind));
        FLUIDS.register("flowing_milk_" + kind, () -> net.syrupstudios.animaniax.block.AnimalMilkFluids.flowing(kind));
        BLOCKS.register("milk_" + kind, () -> net.syrupstudios.animaniax.block.AnimalMilkFluids.block(kind));
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, context) -> new net.syrupstudios.animaniax.block.HoneyBottleNeoForgeFluidHandler(stack),
                BeeHiveContent.HONEY_BOTTLE);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, BeeHiveContent.ENTITY,
                (hive, side) -> new BeeHiveNeoForgeFluidHandler(hive));
        event.registerBlockEntity(Capabilities.EnergyStorage.BLOCK, HamsterWheelContent.ENTITY, (wheel, side) ->
                new IEnergyStorage() {
                    @Override public int getEnergyStored() { return wheel.getEnergy(); }
                    @Override public int getMaxEnergyStored() { return wheel.getEnergyCapacity(); }
                    @Override public boolean canExtract() { return true; }
                    @Override public boolean canReceive() { return false; }
                    @Override public int receiveEnergy(int amount, boolean simulate) { return 0; }
                    @Override public int extractEnergy(int amount, boolean simulate) {
                        int extracted = Math.min(Math.max(0, Math.min(amount, 400)), wheel.getEnergy());
                        return simulate ? extracted : wheel.extractEnergy(extracted);
                    }
                });
    }

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DraftHorseContent.DRAFT_HORSE, DraftHorseEntity.createAttributes().build());
        event.put(PeafowlEntity.TYPE, PeafowlEntity.createAttributes().build());
        event.put(HamsterEntity.TYPE, HamsterEntity.createAttributes().build());
        event.put(FerretContent.GREY, FerretEntity.createAttributes().build());
        event.put(FerretContent.WHITE, FerretEntity.createAttributes().build());
        event.put(DogContent.TYPE, DogEntity.createAttributes().build());
        event.put(CatContent.TYPE, CompanionCatEntity.createAttributes().build());
        event.put(HedgehogContent.HEDGEHOG, HedgehogEntity.createAttributes().build());
        event.put(HedgehogContent.HEDGEHOG_ALBINO, HedgehogEntity.createAttributes().build());
        event.put(FrogContent.COMMON_FROG, net.minecraft.world.entity.animal.frog.Frog.createAttributes().build());
        event.put(FrogContent.DART_FROG, net.minecraft.world.entity.animal.frog.Frog.createAttributes().build());
        event.put(FrogContent.TOAD, net.syrupstudios.animaniax.entity.ToadEntity.createAttributes().build());
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        boolean spawnEggs = event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS);
        if (!spawnEggs && !event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) return;
        BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && (item instanceof SpawnEggItem) == spawnEggs
                        && (spawnEggs || item != AnimalSpawnEggContent.ITEM))
                .forEach(event::accept);
        if (spawnEggs) AnimalSpawnEggContent.ITEM.creativeForms().forEach(event::accept);
    }
}
*/
//?}
