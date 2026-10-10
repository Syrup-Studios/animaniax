package net.syrupstudios.animaniax.entity;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;

final class AmphibianAvoidPlayerGoal extends AvoidEntityGoal<Player> {
    private final PathfinderMob amphibian;

    AmphibianAvoidPlayerGoal(PathfinderMob amphibian) {
        super(amphibian, Player.class, 6.0F, 1.5D, 1.5D);
        this.amphibian = amphibian;
    }

    @Override public boolean canUse() {
        return !isPepe() && super.canUse();
    }

    @Override public boolean canContinueToUse() {
        return !isPepe() && super.canContinueToUse();
    }

    private boolean isPepe() {
        return amphibian instanceof CommonFrogEntity frog && frog.isPepe();
    }
}
