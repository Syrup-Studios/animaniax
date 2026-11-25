package net.syrupstudios.animaniax.entity;

/**
 * Synced entity visual metadata exposed to client renderers across vanilla and custom mobs.
 */
public interface AnimalVisualSyncState {
    String animaniax$getSyncedBreed();
    void animaniax$setSyncedBreed(String breed);

    boolean animaniax$isSyncedFemale();
    void animaniax$setSyncedFemale(boolean female);

    boolean animaniax$isSyncedMuddy();
    void animaniax$setSyncedMuddy(boolean muddy);

    int animaniax$getSyncedVariant();
    void animaniax$setSyncedVariant(int variant);
}
