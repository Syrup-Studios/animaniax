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

public final class SaltLickContent {
    public static final Block BLOCK = new SaltLickBlock(BlockBehaviour.Properties.of().strength(1.2F).sound(SoundType.STONE).noOcclusion());
    public static final Item ITEM = new BlockItem(BLOCK, new Item.Properties().durability(SaltLickBlockEntity.MAX_USES));
    public static final BlockEntityType<SaltLickBlockEntity> ENTITY = BlockEntityType.Builder.of(SaltLickBlockEntity::new, BLOCK).build(null);

    private SaltLickContent() {}
    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id("salt_lick"), BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("salt_lick"), ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("salt_lick"), ENTITY);
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}
