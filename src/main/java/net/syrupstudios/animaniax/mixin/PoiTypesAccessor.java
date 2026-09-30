package net.syrupstudios.animaniax.mixin;

import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PoiTypes.class)
public interface PoiTypesAccessor {
    @Invoker("register")
    static PoiType animaniax$register(Registry<PoiType> registry, ResourceKey<PoiType> key,
            Set<BlockState> states, int maxTickets, int validRange) {
        throw new AssertionError();
    }
}
