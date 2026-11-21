package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class DraftHorseContent {
    public static final EntityType<DraftHorseEntity> DRAFT_HORSE = EntityType.Builder
            .of(DraftHorseEntity::new, MobCategory.CREATURE)
            .sized(1.8F, 2.2F)
            .clientTrackingRange(10)
            .build("animaniax:draft_horse");
    public static final Item DRAFT_HORSE_SPAWN_EGG = new SpawnEggItem(DRAFT_HORSE, 0x6D4C35, 0xC6A77A, new Item.Properties());

    private DraftHorseContent() {}

    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("draft_horse"), DRAFT_HORSE);
        Registry.register(BuiltInRegistries.ITEM, id("draft_horse_spawn_egg"), DRAFT_HORSE_SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(
                DRAFT_HORSE, DraftHorseEntity.createAttributes());
        //?}
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath("animaniax", path);
    }
}
