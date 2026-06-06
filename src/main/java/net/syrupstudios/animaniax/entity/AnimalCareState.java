package net.syrupstudios.animaniax.entity;

public interface AnimalCareState {
    boolean animaniax$careBlocksBreeding();
    boolean animaniax$needsWater();
    boolean animaniax$needsGraze();
    boolean animaniax$needsSeeds();
    boolean animaniax$needsMud();
    boolean animaniax$isContent();
    boolean animaniax$isFed();
    boolean animaniax$isWatered();
    boolean animaniax$wasInteractedWith();
    boolean animaniax$wasHandFed();
    void animaniax$markPlayerInteraction(boolean handFed);
    void animaniax$clearHandFed();
    boolean animaniax$hasGrowthCare();
    void animaniax$milk();
    void animaniax$drink();
    boolean animaniax$feedForCare();
    void animaniax$waterForCare();
    void animaniax$graze();
    void animaniax$eatSlop();
    void animaniax$useMud();
}
