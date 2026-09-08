package net.syrupstudios.animaniax.advancement;

import com.mojang.serialization.Codec;
import net.minecraft.advancements.critereon.PlayerInteractTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public final class FeedAnimalTrigger extends SimpleCriterionTrigger<PlayerInteractTrigger.TriggerInstance> {
    public static final FeedAnimalTrigger INSTANCE = new FeedAnimalTrigger();

    private FeedAnimalTrigger() {}

    public static void register() {
        Registry.register(BuiltInRegistries.TRIGGER_TYPES, ResourceLocation.fromNamespaceAndPath("animaniax", "feed_animal"), INSTANCE);
    }

    @Override
    public Codec<PlayerInteractTrigger.TriggerInstance> codec() {
        return PlayerInteractTrigger.TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, Entity entity, ItemStack item) {
        LootContext context = net.minecraft.advancements.critereon.EntityPredicate.createContext(player, entity);
        trigger(player, instance -> instance.matches(item, context));
    }
}
