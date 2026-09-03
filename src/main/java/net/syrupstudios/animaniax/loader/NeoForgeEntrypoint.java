package net.syrupstudios.animaniax.loader;

//? if neoforge {
/*
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.LevelResource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.DraftHorseContent;
import net.syrupstudios.animaniax.entity.DraftHorseEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.block.SeedPileContent;
import net.syrupstudios.animaniax.block.StrawContent;
import net.syrupstudios.animaniax.block.MudContent;
import net.syrupstudios.animaniax.block.SaltLickContent;
import net.syrupstudios.animaniax.block.CheeseMoldContent;
import net.syrupstudios.animaniax.item.TruffleContent;
import net.syrupstudios.animaniax.item.OmeletteContent;
import net.syrupstudios.animaniax.item.AnimalMilkContent;
import net.syrupstudios.animaniax.item.SlopContent;
import net.syrupstudios.animaniax.item.SlopRecipe;
import net.syrupstudios.animaniax.item.CheeseWedgeContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import net.syrupstudios.animaniax.item.PrimeChickenContent;
import net.syrupstudios.animaniax.item.PrimeMuttonContent;
import net.syrupstudios.animaniax.item.GoatMeatContent;
import net.syrupstudios.animaniax.item.PrimeRabbitContent;
import net.syrupstudios.animaniax.item.PrimeMeatContent;
import net.syrupstudios.animaniax.block.SheepWoolContent;
import net.syrupstudios.animaniax.item.PrimeMeatCuttingRecipe;
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
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, "animaniax");
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, "animaniax");

    public NeoForgeEntrypoint(IEventBus modBus) {
        net.neoforged.neoforge.common.NeoForgeMod.enableMilkFluid();
        FLUID_TYPES.register(modBus);
        registerMilkFluid("friesian");
        registerMilkFluid("holstein");
        registerMilkFluid("jersey");
        registerMilkFluid("goat");
        registerMilkFluid("sheep");
        FLUIDS.register(modBus);
        ENTITY_TYPES.register("draft_horse", () -> DraftHorseContent.DRAFT_HORSE);
        ITEMS.register("draft_horse_spawn_egg", () -> DraftHorseContent.DRAFT_HORSE_SPAWN_EGG);
        modBus.addListener(this::registerEntityAttributes);
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
        BLOCKS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT);
        BLOCKS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN);
        BLOCKS.register("melon_seed_pile", () -> SeedPileContent.MELON);
        BLOCKS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT);
        BLOCKS.register("straw", () -> StrawContent.BLOCK);
        BLOCKS.register("mud", () -> MudContent.BLOCK);
        ITEMS.register("trough", () -> TroughContent.TROUGH_ITEM);
        ITEMS.register("wheat_seed_pile", () -> SeedPileContent.WHEAT_ITEM);
        ITEMS.register("pumpkin_seed_pile", () -> SeedPileContent.PUMPKIN_ITEM);
        ITEMS.register("melon_seed_pile", () -> SeedPileContent.MELON_ITEM);
        ITEMS.register("beetroot_seed_pile", () -> SeedPileContent.BEETROOT_ITEM);
        ITEMS.register("straw", () -> StrawContent.ITEM);
        ITEMS.register("mud", () -> MudContent.ITEM);
        BLOCK_ENTITIES.register("trough", () -> TroughContent.TROUGH_ENTITY);
        BLOCK_ENTITIES.register("nest", () -> NestContent.NEST_ENTITY);
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
        ITEMS.register("brown_egg", () -> BrownEggContent.BROWN_EGG);
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
        RECIPE_SERIALIZERS.register(modBus);
        ENTITY_TYPES.register(modBus);
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

    private void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(DraftHorseContent.DRAFT_HORSE, DraftHorseEntity.createAttributes().build());
    }

    private void addCreativeItems(BuildCreativeModeTabContentsEvent event) {
        boolean spawnEggs = event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS);
        if (!spawnEggs && !event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) return;
        BuiltInRegistries.ITEM.stream()
                .filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals("animaniax")
                        && (item instanceof SpawnEggItem) == spawnEggs)
                .forEach(event::accept);
    }
}
*/
//?}
