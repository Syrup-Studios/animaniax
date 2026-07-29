package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.syrupstudios.animaniax.entity.SheepBreedState;

public final class SheepWoolContent {
    public static final Block DORSET_BROWN_BLOCK = wool(MapColor.COLOR_BROWN);
    public static final Block FRIESIAN_BLACK_BLOCK = wool(MapColor.COLOR_BLACK);
    public static final Block FRIESIAN_BROWN_BLOCK = wool(MapColor.COLOR_BROWN);
    public static final Block JACOB_BLOCK = wool(MapColor.SNOW);
    public static final Block MERINO_BROWN_BLOCK = wool(MapColor.COLOR_BROWN);
    public static final Block MERINO_WHITE_BLOCK = wool(MapColor.SNOW);
    public static final Block SUFFOLK_BROWN_BLOCK = wool(MapColor.COLOR_BROWN);

    public static final Item DORSET_BROWN = new BlockItem(DORSET_BROWN_BLOCK, new Item.Properties());
    public static final Item FRIESIAN_BLACK = new BlockItem(FRIESIAN_BLACK_BLOCK, new Item.Properties());
    public static final Item FRIESIAN_BROWN = new BlockItem(FRIESIAN_BROWN_BLOCK, new Item.Properties());
    public static final Item JACOB = new BlockItem(JACOB_BLOCK, new Item.Properties());
    public static final Item MERINO_BROWN = new BlockItem(MERINO_BROWN_BLOCK, new Item.Properties());
    public static final Item MERINO_WHITE = new BlockItem(MERINO_WHITE_BLOCK, new Item.Properties());
    public static final Item SUFFOLK_BROWN = new BlockItem(SUFFOLK_BROWN_BLOCK, new Item.Properties());

    private SheepWoolContent() {}

    public static void registerFabric() {
        register("dorset_brown_wool", DORSET_BROWN_BLOCK, DORSET_BROWN);
        register("friesian_black_wool", FRIESIAN_BLACK_BLOCK, FRIESIAN_BLACK);
        register("friesian_brown_wool", FRIESIAN_BROWN_BLOCK, FRIESIAN_BROWN);
        register("jacob_wool", JACOB_BLOCK, JACOB);
        register("merino_brown_wool", MERINO_BROWN_BLOCK, MERINO_BROWN);
        register("merino_white_wool", MERINO_WHITE_BLOCK, MERINO_WHITE);
        register("suffolk_brown_wool", SUFFOLK_BROWN_BLOCK, SUFFOLK_BROWN);
    }

    public static Item forShearing(Sheep sheep, SheepBreedState breedState) {
        int colorNumber = breedState.animaniax$getSheepColorNumber();
        String breed = breedState.animaniax$getSheepBreed();
        return switch (breed) {
            case "Dorset" -> colorNumber == 1 ? DORSET_BROWN : null;
            case "Friesian" -> colorNumber == 0 ? FRIESIAN_BLACK : colorNumber == 2 ? FRIESIAN_BROWN : null;
            case "Jacob" -> JACOB;
            case "Merino" -> colorNumber == 1 ? MERINO_BROWN
                    : sheep.getColor() == DyeColor.WHITE ? MERINO_WHITE : null;
            case "Suffolk" -> colorNumber == 1 ? SUFFOLK_BROWN : null;
            default -> null;
        };
    }

    public static boolean canDye(SheepBreedState sheep) {
        int colorNumber = sheep.animaniax$getSheepColorNumber();
        return switch (sheep.animaniax$getSheepBreed()) {
            case "Dorper" -> true;
            case "Dorset", "Merino" -> colorNumber == 0;
            case "Friesian" -> colorNumber == 1;
            case "Suffolk" -> colorNumber == 0;
            default -> false;
        };
    }

    private static Block wool(MapColor color) {
        return new Block(BlockBehaviour.Properties.of().mapColor(color).strength(0.8F).sound(SoundType.WOOL));
    }

    private static void register(String path, Block block, Item item) {
        Registry.register(BuiltInRegistries.BLOCK, id(path), block);
        Registry.register(BuiltInRegistries.ITEM, id(path), item);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
