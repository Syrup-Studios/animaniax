package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class HedgehogContent {
    public static final EntityType<HedgehogEntity> HEDGEHOG = EntityType.Builder.of(HedgehogEntity::new, MobCategory.CREATURE)
            .sized(0.45F, 0.35F).clientTrackingRange(8).build("animaniax:hedgehog");
    public static final EntityType<HedgehogEntity> HEDGEHOG_ALBINO = EntityType.Builder.of(HedgehogEntity::new, MobCategory.CREATURE)
            .sized(0.45F, 0.35F).clientTrackingRange(8).build("animaniax:hedgehog_albino");
    public static final Item HEDGEHOG_SPAWN_EGG = new SpawnEggItem(HEDGEHOG, 0x8C6749, 0xE4D2AD, new Item.Properties());
    public static final Item HEDGEHOG_ALBINO_SPAWN_EGG = new SpawnEggItem(HEDGEHOG_ALBINO, 0xE5D3C1, 0xF1E8D9, new Item.Properties());

    private HedgehogContent() {}
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hedgehog"), HEDGEHOG);
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("hedgehog_albino"), HEDGEHOG_ALBINO);
        Registry.register(BuiltInRegistries.ITEM, id("hedgehog_spawn_egg"), HEDGEHOG_SPAWN_EGG);
        Registry.register(BuiltInRegistries.ITEM, id("hedgehog_albino_spawn_egg"), HEDGEHOG_ALBINO_SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(HEDGEHOG, HedgehogEntity.createAttributes());
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(HEDGEHOG_ALBINO, HedgehogEntity.createAttributes());
        //?}
    }
}
