package net.syrupstudios.animaniax.mixin;

import java.util.UUID;
import net.minecraft.world.entity.animal.goat.Goat;
import net.syrupstudios.animaniax.entity.GoatSparringState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Goat.class)
abstract class GoatSparringMixin implements GoatSparringState {
    @Unique private UUID animaniax$sparringPartner;
    @Unique private long animaniax$sparringStartDeadline;
    @Unique private long animaniax$sparringEndTick;
    @Unique private boolean animaniax$sparringActive;

    @Override public UUID animaniax$sparringPartner() { return animaniax$sparringPartner; }
    @Override public long animaniax$sparringStartDeadline() { return animaniax$sparringStartDeadline; }
    @Override public long animaniax$sparringEndTick() { return animaniax$sparringEndTick; }
    @Override public boolean animaniax$isSparringActive() { return animaniax$sparringActive; }

    @Override
    public void animaniax$setSparring(UUID partner, long startDeadline, long endTick) {
        animaniax$sparringPartner = partner;
        animaniax$sparringStartDeadline = startDeadline;
        animaniax$sparringEndTick = endTick;
        animaniax$sparringActive = false;
    }

    @Override public void animaniax$setSparringActive(boolean active) { animaniax$sparringActive = active; }

    @Override
    public void animaniax$clearSparring() {
        animaniax$sparringPartner = null;
        animaniax$sparringStartDeadline = 0;
        animaniax$sparringEndTick = 0;
        animaniax$sparringActive = false;
    }
}
