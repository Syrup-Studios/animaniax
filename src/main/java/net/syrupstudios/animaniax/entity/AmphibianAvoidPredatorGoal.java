package net.syrupstudios.animaniax.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

public final class AmphibianAvoidPredatorGoal extends AvoidEntityGoal<LivingEntity> {
    private static final TagKey<EntityType<?>> PREDATORS = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("animaniax", "amphibian_predators"));

    private final PathfinderMob amphibian;

    public AmphibianAvoidPredatorGoal(PathfinderMob amphibian) {
        super(amphibian, LivingEntity.class, 10.0F, 3.0D, 3.5D,
                entity -> entity.getType().is(PREDATORS));
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
