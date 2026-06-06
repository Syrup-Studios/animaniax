package net.syrupstudios.animaniax.entity;

import java.util.List;

public interface PigBreedState {
    List<String> BREEDS = List.of("Duroc", "Hampshire", "Large Black", "Large White", "Old Spot", "Yorkshire");

    String animaniax$getPigBreed();
    void animaniax$setPigBreed(String breed);
}
