package net.syrupstudios.animaniax.entity.ai;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.syrupstudios.animaniax.block.MudBlock;
import net.minecraft.world.level.block.GrassBlock;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.item.TruffleContent;

public final class PigSnuffleGoal extends Goal {
    private static final int SNIFF_TICKS = 160;
    private final Mob pig;
    private int ticksRemaining;
    private boolean droppedTruffle;
    private boolean ateTruffle;
    private float healthAtStart;

    public PigSnuffleGoal(Mob pig) {
        this.pig = pig;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
    }

    @Override
    public boolean canUse() {
        if (MudBlock.isMud(pig.level().getBlockState(pig.blockPosition().below()))) return false;
        if (pig.isSleeping() || ((AnimalSleepState) pig).animaniax$isSleeping()
                || ((AnimalCareState) pig).animaniax$isFed()) {
            return false;
        }
        return pig.getRandom().nextInt(120) == 50;
    }

    @Override
    public boolean canContinueToUse() {
        return ticksRemaining > 0 && !pig.isSleeping()
                && !((AnimalSleepState) pig).animaniax$isSleeping()
                && !((AnimalCareState) pig).animaniax$isFed()
                && pig.getHealth() >= healthAtStart
                && !MudBlock.isMud(pig.level().getBlockState(pig.blockPosition().below()))
                && pig.level().getBlockState(pig.blockPosition().below()).getBlock() instanceof GrassBlock;
    }

    @Override
    public void start() {
        ticksRemaining = SNIFF_TICKS;
        droppedTruffle = false;
        ateTruffle = false;
        healthAtStart = pig.getHealth();
        pig.getNavigation().stop();
        pig.level().broadcastEntityEvent(pig, (byte) 10);
    }

    @Override
    public void tick() {
        ticksRemaining--;
        if (!droppedTruffle && ticksRemaining > 80
                && pig.level().getBlockState(pig.blockPosition().below()).getBlock() instanceof GrassBlock
                && !pig.isBaby()
                && pig.getLeashHolder() instanceof Player
                && pig.level().getBiome(pig.blockPosition()).is(BiomeTags.IS_FOREST)) {
            droppedTruffle = true;
            BlockPos groundPos = pig.blockPosition().below();
            pig.level().levelEvent(2001, groundPos, Block.getId(pig.level().getBlockState(groundPos)));
            pig.spawnAtLocation(new ItemStack(TruffleContent.TRUFFLE, pig.getRandom().nextInt(2) + 1));
        }
        if (ticksRemaining < 100 && !ateTruffle) {
            boolean consumedTruffle = false;
            for (ItemEntity item : pig.level().getEntitiesOfClass(ItemEntity.class, pig.getBoundingBox().inflate(3.0D))) {
                if (!item.getItem().is(TruffleContent.TRUFFLE)) continue;
                item.getItem().shrink(64);
                if (item.getItem().isEmpty()) item.discard();
                consumedTruffle = true;
            }
            if (consumedTruffle) {
                ((AnimalCareState) pig).animaniax$graze();
                ateTruffle = true;
            }
        }
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void stop() {
        ticksRemaining = 0;
        ateTruffle = false;
    }
}
