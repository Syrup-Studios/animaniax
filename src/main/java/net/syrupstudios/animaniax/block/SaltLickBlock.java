package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class SaltLickBlock extends Block implements EntityBlock {
    private static final VoxelShape SHAPE = Shapes.or(Block.box(4, 0, 4, 12, 2, 12), Block.box(5, 2, 5, 11, 8, 11));

    public SaltLickBlock(Properties properties) { super(properties); }

    @Override public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level,
            BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) { return SHAPE; }

    @Override public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SaltLickBlockEntity(pos, state);
    }

    @Override public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof SaltLickBlockEntity lick) {
            if (stack.getDamageValue() == 0) {
                lick.setUses(AnimalCareConfig.saltLickMaxUses);
            } else {
                lick.setUses(stack.getMaxDamage() - stack.getDamageValue());
            }
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (params.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof SaltLickBlockEntity lick) {
            for (ItemStack stack : drops) if (stack.is(SaltLickContent.ITEM)) {
                int uses = lick.getUses();
                stack.set(DataComponents.MAX_DAMAGE, AnimalCareConfig.saltLickMaxUses);
                stack.setDamageValue(AnimalCareConfig.saltLickMaxUses - uses);
            }
        }
        return drops;
    }

    public static boolean use(Level level, BlockPos pos, net.minecraft.world.entity.LivingEntity animal) {
        if (level.isClientSide || !(level.getBlockEntity(pos) instanceof SaltLickBlockEntity lick)
                || animal.getHealth() >= animal.getMaxHealth()) return false;
        if (!lick.use()) return false;
        animal.heal(2.0F);
        return true;
    }
}
