package net.syrupstudios.animaniax.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public final class TroughContent {
    public static final Block TROUGH = new TroughBlock(BlockBehaviour.Properties.of().strength(2.0F).randomTicks()
            .sound(net.minecraft.world.level.block.SoundType.WOOD).noOcclusion()
            .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK));
    public static final Item TROUGH_ITEM = new BlockItem(TROUGH, new Item.Properties());
    public static final BlockEntityType<TroughBlockEntity> TROUGH_ENTITY = BlockEntityType.Builder.of(TroughBlockEntity::new, TROUGH).build(null);
    private static boolean waterBucketBehaviorRegistered;

    private TroughContent() {}

    public static void registerFabric() {
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK, id("trough"), TROUGH);
        net.minecraft.core.Registry.register(BuiltInRegistries.ITEM, id("trough"), TROUGH_ITEM);
        net.minecraft.core.Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("trough"), TROUGH_ENTITY);
    }

    public static void registerWaterBucketBehavior() {
        if (waterBucketBehaviorRegistered) return;
        if (!DispenserBlock.DISPENSER_REGISTRY.containsKey(Items.WATER_BUCKET)) DispenseItemBehavior.bootStrap();
        DispenseItemBehavior vanilla = DispenserBlock.DISPENSER_REGISTRY.get(Items.WATER_BUCKET);
        DefaultDispenseItemBehavior troughBehavior = new DefaultDispenseItemBehavior() {
            @Override
            protected ItemStack execute(BlockSource source, ItemStack stack) {
                Direction facing = source.state().getValue(DispenserBlock.FACING);
                BlockPos target = source.pos().relative(facing);
                return TroughBlock.fillWater(source.level(), target)
                        ? consumeWithRemainder(source, stack, new ItemStack(Items.BUCKET)) : stack;
            }
        };
        DispenserBlock.registerBehavior(Items.WATER_BUCKET, new DispenseItemBehavior() {
            @Override
            public ItemStack dispense(BlockSource source, ItemStack stack) {
                BlockPos target = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                return source.level().getBlockState(target).is(TROUGH)
                        ? troughBehavior.dispense(source, stack) : vanilla.dispense(source, stack);
            }
        });
        waterBucketBehaviorRegistered = true;
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
