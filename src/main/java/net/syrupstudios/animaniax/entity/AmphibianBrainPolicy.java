package net.syrupstudios.animaniax.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

public final class AmphibianBrainPolicy {
    private static final TagKey<EntityType<?>> ANIMANIA_AMPHIBIANS = TagKey.create(
            Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("animaniax", "amphibians"));

    private AmphibianBrainPolicy() { }

    public static boolean applies(Entity entity) {
        return entity.getType().is(ANIMANIA_AMPHIBIANS);
    }
}
