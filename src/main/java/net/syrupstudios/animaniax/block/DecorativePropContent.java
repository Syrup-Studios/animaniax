package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;

public final class DecorativePropContent {
    public static final Block CAT_TOWER = new DecorativePropBlock(
            BlockBehaviour.Properties.of().strength(1.4F, 0.8F).sound(SoundType.WOOD),
            Block.box(0, 0, 0, 16, 24, 16));
    public static final Block DOG_HOUSE = new DecorativePropBlock(
            BlockBehaviour.Properties.of().strength(1.5F, 1.4F).sound(SoundType.STONE), Shapes.block());
    public static final Block LITTER_BOX = new DecorativePropBlock(
            BlockBehaviour.Properties.of().strength(1.5F, 0.8F).sound(SoundType.STONE),
            Block.box(1, 0, 1, 15, 3, 15));
    public static final Item CAT_TOWER_ITEM = new BlockItem(CAT_TOWER, new Item.Properties());
    public static final Item DOG_HOUSE_ITEM = new BlockItem(DOG_HOUSE, new Item.Properties());
    public static final Item LITTER_BOX_ITEM = new BlockItem(LITTER_BOX, new Item.Properties());

    private DecorativePropContent() {}

    public static void registerFabric() {
        register("cat_tower", CAT_TOWER, CAT_TOWER_ITEM);
        register("dog_house", DOG_HOUSE, DOG_HOUSE_ITEM);
        register("litter_box", LITTER_BOX, LITTER_BOX_ITEM);
    }

    private static void register(String path, Block block, Item item) {
        Registry.register(BuiltInRegistries.BLOCK, id(path), block);
        Registry.register(BuiltInRegistries.ITEM, id(path), item);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
