package net.syrupstudios.animaniax.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.syrupstudios.animaniax.item.CartItem;

public final class CartContent {
    public static final EntityType<PullCartEntity> CART = EntityType.Builder
            .<PullCartEntity>of(PullCartEntity::new, MobCategory.MISC)
            .sized(1.25F, 0.8F)
            .clientTrackingRange(8)
            .updateInterval(2)
            .build("animaniax:cart");
    public static final Item CART_ITEM = new CartItem(new Item.Properties().stacksTo(1));
    public static final Item WHEEL = new Item(new Item.Properties());

    private CartContent() { }

    public static void registerFabric() {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id("cart"), CART);
        Registry.register(BuiltInRegistries.ITEM, id("cart"), CART_ITEM);
        Registry.register(BuiltInRegistries.ITEM, id("wheel"), WHEEL);
    }

    public static ResourceLocation id(String path) { return ResourceLocation.fromNamespaceAndPath("animaniax", path); }
}
