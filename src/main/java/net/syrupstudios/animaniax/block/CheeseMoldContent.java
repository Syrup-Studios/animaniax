package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class CheeseMoldContent {
    public static final Block MOLD = new CheeseMoldBlock(BlockBehaviour.Properties.of().strength(1.5F)
            .sound(SoundType.WOOD).noOcclusion());
    public static final Item MOLD_ITEM = new BlockItem(MOLD, new Item.Properties());
    private static CheeseWheelBlock wheel() {
        return new CheeseWheelBlock(BlockBehaviour.Properties.of().strength(0.5F).sound(SoundType.HONEY_BLOCK).noOcclusion());
    }
    public static final CheeseWheelBlock FRIESIAN_CHEESE_BLOCK = wheel();
    public static final CheeseWheelBlock HOLSTEIN_CHEESE_BLOCK = wheel();
    public static final CheeseWheelBlock JERSEY_CHEESE_BLOCK = wheel();
    public static final CheeseWheelBlock GOAT_CHEESE_BLOCK = wheel();
    public static final CheeseWheelBlock SHEEP_CHEESE_BLOCK = wheel();
    public static final Item FRIESIAN_CHEESE = new BlockItem(FRIESIAN_CHEESE_BLOCK, new Item.Properties());
    public static final Item HOLSTEIN_CHEESE = new BlockItem(HOLSTEIN_CHEESE_BLOCK, new Item.Properties());
    public static final Item JERSEY_CHEESE = new BlockItem(JERSEY_CHEESE_BLOCK, new Item.Properties());
    public static final Item GOAT_CHEESE = new BlockItem(GOAT_CHEESE_BLOCK, new Item.Properties());
    public static final Item SHEEP_CHEESE = new BlockItem(SHEEP_CHEESE_BLOCK, new Item.Properties());
    public static final Item SALT = new Item(new Item.Properties());
    public static Item itemFor(CheeseWheelBlock block) {
        if (block == FRIESIAN_CHEESE_BLOCK) return FRIESIAN_CHEESE;
        if (block == HOLSTEIN_CHEESE_BLOCK) return HOLSTEIN_CHEESE;
        if (block == JERSEY_CHEESE_BLOCK) return JERSEY_CHEESE;
        if (block == GOAT_CHEESE_BLOCK) return GOAT_CHEESE;
        return SHEEP_CHEESE;
    }
    public static Item cheeseFor(String type) {
        return switch (type) {
            case "friesian_cheese" -> FRIESIAN_CHEESE;
            case "holstein_cheese" -> HOLSTEIN_CHEESE;
            case "jersey_cheese" -> JERSEY_CHEESE;
            case "goat_cheese" -> GOAT_CHEESE;
            case "sheep_cheese" -> SHEEP_CHEESE;
            default -> FRIESIAN_CHEESE;
        };
    }
    public static final BlockEntityType<CheeseMoldBlockEntity> MOLD_ENTITY =
            BlockEntityType.Builder.of(CheeseMoldBlockEntity::new, MOLD).build(null);

    private CheeseMoldContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id("cheese_mold"), MOLD);
        Registry.register(BuiltInRegistries.ITEM, id("cheese_mold"), MOLD_ITEM);
        Registry.register(BuiltInRegistries.BLOCK, id("friesian_cheese"), FRIESIAN_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("holstein_cheese"), HOLSTEIN_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("jersey_cheese"), JERSEY_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("goat_cheese"), GOAT_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("sheep_cheese"), SHEEP_CHEESE_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("friesian_cheese"), FRIESIAN_CHEESE);
        Registry.register(BuiltInRegistries.ITEM, id("holstein_cheese"), HOLSTEIN_CHEESE);
        Registry.register(BuiltInRegistries.ITEM, id("jersey_cheese"), JERSEY_CHEESE);
        Registry.register(BuiltInRegistries.ITEM, id("goat_cheese"), GOAT_CHEESE);
        Registry.register(BuiltInRegistries.ITEM, id("sheep_cheese"), SHEEP_CHEESE);
        Registry.register(BuiltInRegistries.ITEM, id("salt"), SALT);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("cheese_mold"), MOLD_ENTITY);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
