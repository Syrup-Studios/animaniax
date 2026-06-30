package net.syrupstudios.animaniax.block;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class SeedPileContent {
    public static final Block WHEAT = block();
    public static final Block PUMPKIN = block();
    public static final Block MELON = block();
    public static final Block BEETROOT = block();
    public static final Item WHEAT_ITEM = new BlockItem(WHEAT, new Item.Properties());
    public static final Item PUMPKIN_ITEM = new BlockItem(PUMPKIN, new Item.Properties());
    public static final Item MELON_ITEM = new BlockItem(MELON, new Item.Properties());
    public static final Item BEETROOT_ITEM = new BlockItem(BEETROOT, new Item.Properties());
    private static boolean dispenserBehaviorsRegistered;

    private SeedPileContent() {}
    private static Block block() {
        return new SeedPileBlock(BlockBehaviour.Properties.of().strength(0.1F).sound(net.minecraft.world.level.block.SoundType.GRASS).noOcclusion());
    }

    public static boolean place(Level level, BlockPos support, Block block) {
        BlockPos pos = support.above();
        return canPlace(level, support) && level.setBlock(pos, block.defaultBlockState(), Block.UPDATE_ALL);
    }

    public static boolean canPlace(Level level, BlockPos support) { return SeedPileBlock.canPlace(level, support); }

    public static void registerFabric() {
        register("wheat_seed_pile", WHEAT, WHEAT_ITEM); register("pumpkin_seed_pile", PUMPKIN, PUMPKIN_ITEM);
        register("melon_seed_pile", MELON, MELON_ITEM); register("beetroot_seed_pile", BEETROOT, BEETROOT_ITEM);
    }
    private static void register(String path, Block block, Item item) {
        Registry.register(BuiltInRegistries.BLOCK, id(path), block); Registry.register(BuiltInRegistries.ITEM, id(path), item);
    }
    public static void registerDispenserBehaviors() {
        if (dispenserBehaviorsRegistered) return;
        for (Item seed : new Item[]{Items.WHEAT_SEEDS, Items.PUMPKIN_SEEDS, Items.MELON_SEEDS, Items.BEETROOT_SEEDS}) {
            DispenseItemBehavior vanilla = DispenserBlock.DISPENSER_REGISTRY.get(seed);
            if (vanilla == null) { DispenseItemBehavior.bootStrap(); vanilla = DispenserBlock.DISPENSER_REGISTRY.get(seed); }
            final DispenseItemBehavior fallback = vanilla;
            DefaultDispenseItemBehavior pile = new DefaultDispenseItemBehavior() {
                @Override protected ItemStack execute(BlockSource source, ItemStack stack) {
                    Direction direction = source.state().getValue(DispenserBlock.FACING);
                    BlockPos pilePos = source.pos().relative(direction);
                    BlockPos support = pilePos.below();
                    Block block = seed == Items.WHEAT_SEEDS ? WHEAT : seed == Items.PUMPKIN_SEEDS ? PUMPKIN
                            : seed == Items.MELON_SEEDS ? MELON : BEETROOT;
                    if (!SeedPileContent.canPlace(source.level(), support)
                            || !source.level().setBlock(pilePos, block.defaultBlockState(), Block.UPDATE_ALL)) return stack;
                    stack.shrink(1);
                    return stack;
                }
            };
            DispenserBlock.registerBehavior(seed, new DispenseItemBehavior() {
                @Override public ItemStack dispense(BlockSource source, ItemStack stack) {
                    if (!AnimalCareConfig.allowSeedDispenserPlacement) return fallback.dispense(source, stack);
                    BlockPos pilePos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
                    return SeedPileBlock.canPlace(source.level(), pilePos.below())
                            && source.level().getBlockState(pilePos).canBeReplaced()
                            ? pile.dispense(source, stack) : fallback.dispense(source, stack);
                }
            });
        }
        dispenserBehaviorsRegistered = true;
    }
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}
