package net.syrupstudios.animaniax.mixin;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
interface MobGoalSelectorAccessor {
    @Accessor("goalSelector") GoalSelector animaniax$getGoalSelector();
    @Accessor("targetSelector") GoalSelector animaniax$getTargetSelector();
}
