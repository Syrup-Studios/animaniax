package net.syrupstudios.animaniax.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.syrupstudios.animaniax.item.HoneyBottleItem;

public final class BeeHiveContent {
    public static final Block PLAYER_HIVE = new BeeHiveBlock(BlockBehaviour.Properties.of().strength(1.3F)
            .sound(SoundType.WOOD).noOcclusion(), false);
    public static final Block WILD = new BeeHiveBlock(BlockBehaviour.Properties.of().strength(1.3F)
            .sound(SoundType.GRASS).noOcclusion(), true);
    public static final Item PLAYER_HIVE_ITEM = new BlockItem(PLAYER_HIVE, new Item.Properties());
    public static final Item WILD_ITEM = new BlockItem(WILD, new Item.Properties());
    public static final Item HONEY_BOTTLE = new HoneyBottleItem();
    public static final HoneyFluid HONEY_SOURCE = new HoneyFluid.Source();
    public static final HoneyFluid HONEY_FLOWING = new HoneyFluid.Flowing();
    public static final Item HONEY_BUCKET = new BucketItem(HONEY_SOURCE, new Item.Properties().stacksTo(1)
            .craftRemainder(net.minecraft.world.item.Items.BUCKET));
    public static final Block HONEY_BLOCK = new HoneyFluidBlock(HONEY_FLOWING,
            BlockBehaviour.Properties.of().mapColor(net.minecraft.world.level.material.MapColor.COLOR_YELLOW)
                    .replaceable().noCollission().strength(100.0F).noLootTable());
    public static final BlockEntityType<BeeHiveBlockEntity> ENTITY = BlockEntityType.Builder
            .of(BeeHiveBlockEntity::new, PLAYER_HIVE, WILD).build(null);

    private BeeHiveContent() {}

    public static void registerFabric() {
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, id("bee_hive"), PLAYER_HIVE);
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, id("wild_hive"), WILD);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("bee_hive"), PLAYER_HIVE_ITEM);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("wild_hive"), WILD_ITEM);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("honey_bottle"), HONEY_BOTTLE);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("honey_bucket"), HONEY_BUCKET);
        net.minecraft.core.Registry.register(BuiltInRegistries.FLUID, id("honey"), HONEY_SOURCE);
        net.minecraft.core.Registry.register(BuiltInRegistries.FLUID, id("flowing_honey"), HONEY_FLOWING);
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, id("honey"), HONEY_BLOCK);
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("bee_hive"), ENTITY);
    }

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}
