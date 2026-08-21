package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class CheeseWheelBlock extends Block {
    public static final IntegerProperty BITES = IntegerProperty.create("bites", 0, 3);
    private static final VoxelShape[] SHAPES = {
            Block.box(1, 2, 1, 15, 6, 15),
            Shapes.or(Block.box(8, 2, 1, 15, 6, 8), Block.box(1, 2, 8, 8, 6, 15),
                    Block.box(8, 2, 8, 15, 6, 15)),
            Block.box(1, 2, 8, 15, 6, 15),
            Block.box(8, 2, 8, 15, 6, 15)
    };

    public CheeseWheelBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(BITES, 0));
    }

    @Override protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BITES);
    }

    @Override protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(BITES)];
    }

    @Override protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hit) {
        if (!player.canEat(false)) return InteractionResult.PASS;
        if (!level.isClientSide) {
            if (AnimalCareConfig.foodsGiveBonusEffects) {
                if (this == CheeseMoldContent.FRIESIAN_CHEESE_BLOCK)
                    player.addEffect(new MobEffectInstance(MobEffects.HEAL, 6, 2));
                else if (this == CheeseMoldContent.GOAT_CHEESE_BLOCK)
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 0));
                else if (this == CheeseMoldContent.SHEEP_CHEESE_BLOCK)
                    player.addEffect(new MobEffectInstance(MobEffects.HEAL, 10, 0));
                else
                    player.addEffect(new MobEffectInstance(MobEffects.HEAL, 12, 2));
            }
            player.getFoodData().eat(2, 1.2F);
            player.awardStat(Stats.ITEM_USED.get(CheeseMoldContent.itemFor(this)));
            if (state.getValue(BITES) == 3) level.removeBlock(pos, false);
            else level.setBlock(pos, state.setValue(BITES, state.getValue(BITES) + 1), 3);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
