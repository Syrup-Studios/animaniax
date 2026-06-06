package net.syrupstudios.animaniax.entity;

public interface SheepBreedState {
    String animaniax$getSheepBreed();
    void animaniax$setSheepBreed(String breed);
    int animaniax$getSheepColorNumber();
    void animaniax$setSheepColorNumber(int colorNumber);
    void animaniax$randomizeSheepColorNumber();
    int animaniax$getWoolRegrowthTimer();
    void animaniax$setWoolRegrowthTimer(int ticks);
    void animaniax$startWoolRegrowthTimer();
}
