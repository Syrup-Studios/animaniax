package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;

public final class HamsterWheelContent {
    public static final Block WHEEL = new HamsterWheelBlock(BlockBehaviour.Properties.of().strength(2).sound(SoundType.WOOD).noOcclusion());
    public static final Item ITEM = new BlockItem(WHEEL, new Item.Properties());
    public static final BlockEntityType<HamsterWheelBlockEntity> ENTITY = BlockEntityType.Builder.of(HamsterWheelBlockEntity::new, WHEEL).build(null);
    private HamsterWheelContent() {}
    public static void registerFabric() {
        Registry.register(BuiltInRegistries.BLOCK, id("hamster_wheel"), WHEEL);
        Registry.register(BuiltInRegistries.ITEM, id("hamster_wheel"), ITEM);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("hamster_wheel"), ENTITY);
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}
