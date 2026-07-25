package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.loot.LootParams;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class TroughBlock extends Block implements EntityBlock {
    public static final IntegerProperty CONTENT = IntegerProperty.create("content", 0, 3);
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final Set<GlobalPos> PAIR_REMOVALS = new HashSet<>();
    private static final VoxelShape MAIN_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 4, 0, 16, 10, 3), Block.box(0, 4, 13, 16, 10, 16),
            Block.box(0, 4, 3, 3, 10, 13));
    private static final VoxelShape END_SHAPE = Shapes.or(Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 4, 0, 16, 10, 3), Block.box(0, 4, 13, 16, 10, 16),
            Block.box(13, 4, 3, 16, 10, 13));

    public enum Part implements StringRepresentable {
        MAIN, END;
        @Override public String getSerializedName() { return name().toLowerCase(Locale.ROOT); }
    }

    public TroughBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(CONTENT, 0).setValue(LEVEL, 0)
                .setValue(FACING, Direction.NORTH).setValue(PART, Part.MAIN));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CONTENT, LEVEL, FACING, PART);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos pos = context.getClickedPos();
        BlockPos end = endPos(pos, facing);
        if (!context.getLevel().hasChunkAt(end) || !context.getLevel().getWorldBorder().isWithinBounds(end)
                || !context.getLevel().getBlockState(end).canBeReplaced(context)) return null;
        return defaultBlockState().setValue(FACING, facing).setValue(PART, Part.MAIN);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        BlockPos end = endPos(pos, state.getValue(FACING));
        if (!level.isClientSide && level.hasChunkAt(end) && level.getBlockState(end).canBeReplaced()) {
            level.setBlock(end, state.setValue(PART, Part.END), Block.UPDATE_ALL);
        } else if (!level.isClientSide) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            if (placer instanceof Player player && !player.getAbilities().instabuild)
                Block.popResource(level, pos, stack.copyWithCount(1));
        }
    }

    public static BlockPos endPos(BlockPos main, Direction facing) { return main.relative(facing.getClockWise()); }
    public static BlockPos controllerPos(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!state.is(TroughContent.TROUGH) || state.getValue(PART) != Part.END) return pos;
        BlockPos main = pos.relative(state.getValue(FACING).getCounterClockWise());
        return isMatchingMate(level, state, pos, main) ? main : pos;
    }

    private static GlobalPos globalPos(Level level, BlockPos pos) { return GlobalPos.of(level.dimension(), pos); }
    private static boolean isMatchingMate(Level level, BlockState state, BlockPos pos, BlockPos mate) {
        if (!level.hasChunkAt(mate)) return false;
        BlockState other = level.getBlockState(mate);
        if (!other.is(TroughContent.TROUGH) || other.getValue(FACING) != state.getValue(FACING)) return false;
        return state.getValue(PART) == Part.MAIN
                ? other.getValue(PART) == Part.END && mate.equals(endPos(pos, state.getValue(FACING)))
                : other.getValue(PART) == Part.MAIN && mate.equals(pos.relative(state.getValue(FACING).getCounterClockWise()));
    }
    private static boolean removingPair(Level level, BlockPos pos) { return PAIR_REMOVALS.contains(globalPos(level, pos)); }
    private static void removePair(Level level, BlockPos first, BlockPos second, BlockPos remove) {
        PAIR_REMOVALS.add(globalPos(level, first));
        PAIR_REMOVALS.add(globalPos(level, second));
        try { level.setBlock(remove, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL); }
        finally {
            PAIR_REMOVALS.remove(globalPos(level, first));
            PAIR_REMOVALS.remove(globalPos(level, second));
        }
    }

    @Override
    protected VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        VoxelShape shape = state.getValue(PART) == Part.MAIN ? MAIN_SHAPE : END_SHAPE;
        if (state.getValue(FACING) == Direction.NORTH) return shape;
        VoxelShape rotated = Shapes.empty();
        for (AABB box : shape.toAabbs()) {
            double x1, x2, z1, z2;
            switch (state.getValue(FACING)) {
                case SOUTH -> { x1 = 16 - box.maxX * 16; x2 = 16 - box.minX * 16; z1 = 16 - box.maxZ * 16; z2 = 16 - box.minZ * 16; }
                case WEST -> { x1 = box.minZ * 16; x2 = box.maxZ * 16; z1 = 16 - box.maxX * 16; z2 = 16 - box.minX * 16; }
                case EAST -> { x1 = 16 - box.maxZ * 16; x2 = 16 - box.minZ * 16; z1 = box.minX * 16; z2 = box.maxX * 16; }
                default -> { x1 = box.minX * 16; x2 = box.maxX * 16; z1 = box.minZ * 16; z2 = box.maxZ * 16; }
            }
            rotated = Shapes.or(rotated, Block.box(x1, box.minY * 16, z1, x2, box.maxY * 16, z2));
        }
        return rotated;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, net.minecraft.world.level.Level level, BlockPos pos) {
        if (state.getValue(CONTENT) == 2) return state.getValue(LEVEL) * 5;
        if (level.getBlockEntity(pos) instanceof TroughBlockEntity trough) {
            int fluid = switch (state.getValue(CONTENT)) {
                case 1 -> trough.getWaterAmount();
                case 3 -> trough.getSlopAmount();
                default -> 0;
            };
            return fluid / 66;
        }
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TroughBlockEntity(pos, state);
    }

    @Override public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(PART) == Part.MAIN && state.getValue(CONTENT) != 3 && level.getBiome(pos).value().getBaseTemperature() >= 0.15F && level.canSeeSky(pos.above())
                && level.isRainingAt(pos.above()) && level.getBlockEntity(pos) instanceof TroughBlockEntity trough)
            trough.addWater(100);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        BlockPos controller = controllerPos(level, pos);
        if (player.isCreative() && level.getBlockEntity(controller) instanceof TroughBlockEntity entity) {
            entity.markCreativeDestruction();
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!level.isClientSide && !state.is(newState.getBlock())) {
            if (!removingPair(level, pos)) {
                BlockPos mate = state.getValue(PART) == Part.MAIN
                        ? endPos(pos, state.getValue(FACING))
                        : pos.relative(state.getValue(FACING).getCounterClockWise());
                if (isMatchingMate(level, state, pos, mate)) {
                    if (state.getValue(PART) == Part.END) {
                        BlockPos main = pos.relative(state.getValue(FACING).getCounterClockWise());
                        boolean creative = level.hasChunkAt(main) && level.getBlockEntity(main) instanceof TroughBlockEntity trough
                                && trough.isCreativeDestruction();
                        if (!creative) popResource(level, pos, new ItemStack(TroughContent.TROUGH_ITEM));
                    }
                    removePair(level, mate, pos, mate);
                }
            }
            BlockEntity entity = level.getBlockEntity(pos);
            if (state.getValue(PART) == Part.MAIN && entity instanceof TroughBlockEntity trough) {
                if (!trough.isCreativeDestruction()) {
                    ItemStack feed = trough.getFeed();
                    if (!feed.isEmpty()) popResource(level, pos, feed);
                }
            } else if (state.getValue(CONTENT) == 2 && state.getValue(LEVEL) > 0) {
                popResource(level, pos, new ItemStack(Items.WHEAT, state.getValue(LEVEL)));
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        return interact(state, level, pos, player, player.getItemInHand(InteractionHand.MAIN_HAND), InteractionHand.MAIN_HAND)
                ? InteractionResult.SUCCESS : InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (interact(state, level, pos, player, stack, hand)) return ItemInteractionResult.SUCCESS;
        return stack.is(Items.WATER_BUCKET) || stack.is(Items.BUCKET) || stack.is(net.syrupstudios.animaniax.item.SlopContent.SLOP_BUCKET) || TroughBlockEntity.isFeed(stack)
                ? ItemInteractionResult.FAIL : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private boolean interact(BlockState state, Level level, BlockPos pos, Player player, ItemStack stack, InteractionHand hand) {
        pos = controllerPos(level, pos);
        state = level.getBlockState(pos);
        int content = state.getValue(CONTENT);
        int amount = state.getValue(LEVEL);
        if (stack.isEmpty() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty() && content == 2) {
            if (level.isClientSide) return true;
            if (!(level.getBlockEntity(pos) instanceof TroughBlockEntity trough)) return false;
            ItemStack extracted = trough.takeFeed();
            if (extracted.isEmpty()) return false;
            if (!player.getInventory().add(extracted)) player.drop(extracted, false);
            return true;
        }
        if (stack.is(Items.WATER_BUCKET) && (content == 0 || content == 1)) {
            if (!level.isClientSide) {
                if (!fillWater(level, pos)) return false;
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack bucket = new ItemStack(Items.BUCKET);
                    if (stack.isEmpty()) player.setItemInHand(hand, bucket);
                    else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
                }
            }
            return true;
        }
        if (stack.is(Items.BUCKET) && content == 1 && amount == 3) {
            if (!level.isClientSide) {
                if (!(level.getBlockEntity(pos) instanceof TroughBlockEntity trough)
                        || trough.getWaterAmount() != TroughBlockEntity.WATER_CAPACITY) return false;
                trough.setWaterAmount(0);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                    ItemStack bucket = new ItemStack(Items.WATER_BUCKET);
                    if (stack.isEmpty()) player.setItemInHand(hand, bucket);
                    else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
                }
            }
            return true;
        }
        if (stack.is(net.syrupstudios.animaniax.item.SlopContent.SLOP_BUCKET) && content == 0 && amount == 0) {
            if (level.isClientSide) return true;
            if (!(level.getBlockEntity(pos) instanceof TroughBlockEntity trough) || !trough.addSlop()) return false;
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (stack.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
            }
            return true;
        }
        if (TroughBlockEntity.isFeed(stack) && (content == 0 || content == 2) && amount < 3) {
            if (level.isClientSide) return true;
            if (level.getBlockEntity(pos) instanceof TroughBlockEntity trough
                    && trough.addFeed(stack.copyWithCount(1)) > 0) {
                if (!player.getAbilities().instabuild) stack.shrink(1);
                return true;
            }
        }
        return false;
    }

    public static boolean fillWater(Level level, BlockPos pos) {
        pos = controllerPos(level, pos);
        BlockState state = level.getBlockState(pos);
        if (level.isClientSide || !state.is(TroughContent.TROUGH)
                || (state.getValue(CONTENT) != 0 && state.getValue(CONTENT) != 1)
                || !(level.getBlockEntity(pos) instanceof TroughBlockEntity trough)
                || trough.getWaterAmount() >= TroughBlockEntity.WATER_CAPACITY) return false;
        return trough.addWater(TroughBlockEntity.WATER_CAPACITY);
    }

    public static boolean consumeWater(Level level, BlockPos pos) {
        return consumeWater(level, pos, null);
    }

    public static boolean consumeWater(Level level, BlockPos pos, net.minecraft.world.entity.EntityType<?> animal) {
        pos = controllerPos(level, pos);
        BlockState state = level.getBlockState(pos);
        return !level.isClientSide && state.is(TroughContent.TROUGH) && state.getValue(CONTENT) == 1
                && level.getBlockEntity(pos) instanceof TroughBlockEntity trough
                && trough.consumeWater(AnimalProfile.troughWaterCost(animal));
    }

    public static boolean consumeFeed(Level level, BlockPos pos) {
        pos = controllerPos(level, pos);
        BlockState state = level.getBlockState(pos);
        if (!state.is(TroughContent.TROUGH) || state.getValue(CONTENT) != 2 || level.isClientSide) return false;
        return level.getBlockEntity(pos) instanceof TroughBlockEntity trough && trough.consumeFeed(null);
    }

    public static boolean consumeFeed(Level level, BlockPos pos, net.minecraft.world.entity.EntityType<?> animal) {
        pos = controllerPos(level, pos);
        BlockState state = level.getBlockState(pos);
        if (!state.is(TroughContent.TROUGH) || level.isClientSide
                || state.getValue(CONTENT) != 2 && !(animal == net.minecraft.world.entity.EntityType.PIG && state.getValue(CONTENT) == 3)
                || !(level.getBlockEntity(pos) instanceof TroughBlockEntity trough)) return false;
        return trough.consumeFeed(animal);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        if (entity instanceof ItemEntity item && !level.isClientSide && level.getBlockEntity(pos) instanceof TroughBlockEntity trough) {
            int amount = trough.addFeed(item.getItem());
            if (amount > 0) {
                ItemStack remainder = item.getItem().copy();
                remainder.shrink(amount);
                if (remainder.isEmpty()) item.discard(); else item.setItem(remainder);
            }
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return state.getValue(PART) == Part.END ? List.of() : super.getDrops(state, builder);
    }
}
