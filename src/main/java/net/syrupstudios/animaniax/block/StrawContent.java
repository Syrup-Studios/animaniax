package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class StrawContent {
    public static final Block BLOCK = new StrawBlock(BlockBehaviour.Properties.of().strength(0.1F)
            .sound(SoundType.GRASS).noOcclusion().ignitedByLava());
    public static final Item ITEM = new BlockItem(BLOCK, new Item.Properties());

    private StrawContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id(), BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id(), ITEM);
    }

    public static ResourceLocation id() { return ResourceLocation.fromNamespaceAndPath("animaniax", "straw"); }
}
