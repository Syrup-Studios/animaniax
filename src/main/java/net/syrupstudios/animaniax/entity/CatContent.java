package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

public final class CatContent {
    public static final EntityType<CompanionCatEntity> TYPE = EntityType.Builder.of(CompanionCatEntity::new, MobCategory.CREATURE)
            .sized(0.6F, 0.7F).clientTrackingRange(8).build("animaniax:cat");
    public static final Item SPAWN_EGG = new SpawnEggItem(TYPE, 0xD9B96E, 0x57463B, new Item.Properties());

    private CatContent() {}
    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
    public static void registerFabric() {
        //? if fabric {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("cat"), TYPE);
        Registry.register(BuiltInRegistries.ITEM, id("cat_spawn_egg"), SPAWN_EGG);
        net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry.register(TYPE, CompanionCatEntity.createAttributes());
        //?}
    }
}
