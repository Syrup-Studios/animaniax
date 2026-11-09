package net.syrupstudios.animaniax.client;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.syrupstudios.animaniax.entity.ShoulderCarryState;
import net.syrupstudios.animaniax.entity.ShoulderCarryState.ShoulderCarrier;

public final class ShoulderCarryRenderCache {
    private ShoulderCarryRenderCache() {}

    public static Entity get(AbstractClientPlayer player, String type) {
        ShoulderCarrier cache = (ShoulderCarrier) player;
        if (type.isEmpty()) {
            cache.animaniax$setRenderPet("", null);
            return null;
        }
        Entity cached = cache.animaniax$getRenderPet();
        if (cached != null && cache.animaniax$getRenderPetType().equals(type) && cached.level() == player.level()) return cached;
        Entity entity = ShoulderCarryState.createVisual(player.level(), type);
        cache.animaniax$setRenderPet(type, entity);
        return entity;
    }
}
