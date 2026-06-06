package net.syrupstudios.animaniax.entity;

import java.util.UUID;

public interface SheepSparringState {
    UUID animaniax$sparringPartner();
    long animaniax$sparringEndTick();
    long animaniax$sparringStartDeadline();
    boolean animaniax$isSparringActive();
    void animaniax$setSparring(UUID partner, long startDeadline, long endTick);
    void animaniax$setSparringActive(boolean active);
    void animaniax$clearSparring();
}
