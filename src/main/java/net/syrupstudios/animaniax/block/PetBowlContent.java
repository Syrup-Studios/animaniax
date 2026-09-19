package net.syrupstudios.animaniax.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class PetBowlContent {
    public static final Block BLOCK = new PetBowlBlock(BlockBehaviour.Properties.of().strength(1.2F)
            .sound(SoundType.WOOD).noOcclusion());
    public static final Item ITEM = new BlockItem(BLOCK, new Item.Properties());
    public static final BlockEntityType<PetBowlBlockEntity> BLOCK_ENTITY =
            BlockEntityType.Builder.of(PetBowlBlockEntity::new, BLOCK).build(null);

    private PetBowlContent() {}

    public static void registerFabric() {
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, id("pet_bowl"), BLOCK);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("pet_bowl"), ITEM);
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("pet_bowl"), BLOCK_ENTITY);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
