package net.syrupstudios.animaniax.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class NestContent {
    public static final Block NEST = new NestBlock(BlockBehaviour.Properties.of().strength(1.0F)
            .sound(SoundType.GRASS).noOcclusion().randomTicks());
    public static final Item NEST_ITEM = new BlockItem(NEST, new Item.Properties());
    public static final BlockEntityType<NestBlockEntity> NEST_ENTITY =
            net.minecraft.world.level.block.entity.BlockEntityType.Builder.of(NestBlockEntity::new, NEST).build(null);

    private NestContent() {}

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id("nest"), NEST);
        Registry.register(BuiltInRegistries.ITEM, id("nest"), NEST_ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("nest"), NEST_ENTITY);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
