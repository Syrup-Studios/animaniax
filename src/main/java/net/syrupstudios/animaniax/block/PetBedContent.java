package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PetBedContent {
    public static final Block CAT_BED_1 = bed(2, 14, 1);
    public static final Block CAT_BED_2 = bed(2, 14, 2);
    public static final Block DOG_PILLOW = bed(1, 15, 1);
    public static final Item CAT_BED_1_ITEM = new BlockItem(CAT_BED_1, new Item.Properties());
    public static final Item CAT_BED_2_ITEM = new BlockItem(CAT_BED_2, new Item.Properties());
    public static final Item DOG_PILLOW_ITEM = new BlockItem(DOG_PILLOW, new Item.Properties());

    private PetBedContent() {}

    private static Block bed(int min, int max, int height) {
        return new PetBedBlock(BlockBehaviour.Properties.of().strength(1.0F, 0.8F)
                .sound(SoundType.WOOL).noOcclusion(), min, max, height);
    }

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id("cat_bed_1"), CAT_BED_1);
        Registry.register(BuiltInRegistries.BLOCK, id("cat_bed_2"), CAT_BED_2);
        Registry.register(BuiltInRegistries.BLOCK, id("dog_pillow"), DOG_PILLOW);
        Registry.register(BuiltInRegistries.ITEM, id("cat_bed_1"), CAT_BED_1_ITEM);
        Registry.register(BuiltInRegistries.ITEM, id("cat_bed_2"), CAT_BED_2_ITEM);
        Registry.register(BuiltInRegistries.ITEM, id("dog_pillow"), DOG_PILLOW_ITEM);
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
