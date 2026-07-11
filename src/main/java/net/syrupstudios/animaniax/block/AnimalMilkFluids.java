package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.syrupstudios.animaniax.item.AnimalMilkContent;

public final class AnimalMilkFluids {
    public static final AnimalMilkFluid FRIESIAN_SOURCE = new AnimalMilkFluid.Source("friesian");
    public static final AnimalMilkFluid FRIESIAN_FLOWING = new AnimalMilkFluid.Flowing("friesian");
    public static final Block FRIESIAN_BLOCK = block(FRIESIAN_FLOWING);
    public static final AnimalMilkFluid HOLSTEIN_SOURCE = new AnimalMilkFluid.Source("holstein");
    public static final AnimalMilkFluid HOLSTEIN_FLOWING = new AnimalMilkFluid.Flowing("holstein");
    public static final Block HOLSTEIN_BLOCK = block(HOLSTEIN_FLOWING);
    public static final AnimalMilkFluid JERSEY_SOURCE = new AnimalMilkFluid.Source("jersey");
    public static final AnimalMilkFluid JERSEY_FLOWING = new AnimalMilkFluid.Flowing("jersey");
    public static final Block JERSEY_BLOCK = block(JERSEY_FLOWING);
    public static final AnimalMilkFluid GOAT_SOURCE = new AnimalMilkFluid.Source("goat");
    public static final AnimalMilkFluid GOAT_FLOWING = new AnimalMilkFluid.Flowing("goat");
    public static final Block GOAT_BLOCK = block(GOAT_FLOWING);
    public static final AnimalMilkFluid SHEEP_SOURCE = new AnimalMilkFluid.Source("sheep");
    public static final AnimalMilkFluid SHEEP_FLOWING = new AnimalMilkFluid.Flowing("sheep");
    public static final Block SHEEP_BLOCK = block(SHEEP_FLOWING);
    //? if fabric {
    public static final AnimalMilkFluid GENERIC_SOURCE = new AnimalMilkFluid.Source("generic");
    public static final AnimalMilkFluid GENERIC_FLOWING = new AnimalMilkFluid.Flowing("generic");
    public static final Block GENERIC_BLOCK = block(GENERIC_FLOWING);
    //?}

    private AnimalMilkFluids() {}

    private static Block block(AnimalMilkFluid fluid) {
        return new LiquidBlock(fluid, BlockBehaviour.Properties.of().replaceable().noCollission()
                .strength(100.0F).noLootTable());
    }

    public static Fluid source(String kind) {
        return switch (kind) {
            case "friesian" -> FRIESIAN_SOURCE;
            case "holstein" -> HOLSTEIN_SOURCE;
            case "jersey" -> JERSEY_SOURCE;
            case "goat" -> GOAT_SOURCE;
            case "sheep" -> SHEEP_SOURCE;
            //? if fabric {
            case "generic" -> GENERIC_SOURCE;
            //?}
            default -> throw new IllegalArgumentException(kind);
        };
    }

    public static Fluid flowing(String kind) {
        return switch (kind) {
            case "friesian" -> FRIESIAN_FLOWING;
            case "holstein" -> HOLSTEIN_FLOWING;
            case "jersey" -> JERSEY_FLOWING;
            case "goat" -> GOAT_FLOWING;
            case "sheep" -> SHEEP_FLOWING;
            //? if fabric {
            case "generic" -> GENERIC_FLOWING;
            //?}
            default -> throw new IllegalArgumentException(kind);
        };
    }

    public static Block block(String kind) {
        return switch (kind) {
            case "friesian" -> FRIESIAN_BLOCK;
            case "holstein" -> HOLSTEIN_BLOCK;
            case "jersey" -> JERSEY_BLOCK;
            case "goat" -> GOAT_BLOCK;
            case "sheep" -> SHEEP_BLOCK;
            //? if fabric {
            case "generic" -> GENERIC_BLOCK;
            //?}
            default -> throw new IllegalArgumentException(kind);
        };
    }

    public static net.minecraft.world.item.Item bucket(String kind) {
        return switch (kind) {
            case "friesian" -> AnimalMilkContent.FRIESIAN_MILK_BUCKET;
            case "holstein" -> AnimalMilkContent.HOLSTEIN_MILK_BUCKET;
            case "jersey" -> AnimalMilkContent.JERSEY_MILK_BUCKET;
            case "goat" -> AnimalMilkContent.GOAT_MILK_BUCKET;
            case "sheep" -> AnimalMilkContent.SHEEP_MILK_BUCKET;
            //? if fabric {
            case "generic" -> net.minecraft.world.item.Items.MILK_BUCKET;
            //?}
            default -> throw new IllegalArgumentException(kind);
        };
    }

    public static int tint(String kind) {
        return switch (kind) {
            case "friesian" -> 0xFFF2F2EF;
            case "holstein" -> 0xFFF8F8F4;
            case "jersey" -> 0xFFFFE8B2;
            case "goat" -> 0xFFFAFAF4;
            case "sheep" -> 0xFFFFF5D8;
            //? if fabric {
            case "generic" -> 0xFFF8F8F4;
            //?}
            default -> 0xFFFFFFFF;
        };
    }

    private static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }

    public static void registerFabric() {
        register("friesian", FRIESIAN_SOURCE, FRIESIAN_FLOWING, FRIESIAN_BLOCK);
        register("holstein", HOLSTEIN_SOURCE, HOLSTEIN_FLOWING, HOLSTEIN_BLOCK);
        register("jersey", JERSEY_SOURCE, JERSEY_FLOWING, JERSEY_BLOCK);
        register("goat", GOAT_SOURCE, GOAT_FLOWING, GOAT_BLOCK);
        register("sheep", SHEEP_SOURCE, SHEEP_FLOWING, SHEEP_BLOCK);
        //? if fabric {
        register("generic", GENERIC_SOURCE, GENERIC_FLOWING, GENERIC_BLOCK);
        //?}
    }

    private static void register(String kind, Fluid source, Fluid flowing, Block block) {
        Registry.register(BuiltInRegistries.FLUID, id("milk_" + kind), source);
        Registry.register(BuiltInRegistries.FLUID, id("flowing_milk_" + kind), flowing);
        Registry.register(BuiltInRegistries.BLOCK, id("milk_" + kind), block);
    }
}
