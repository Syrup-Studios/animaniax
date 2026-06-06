package net.syrupstudios.animaniax.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.animal.Animal;
import java.util.UUID;

public interface AnimalReproductionState {
    boolean animaniax$isFemale();
    void animaniax$setFemale(boolean female);
    boolean animaniax$isPregnant();
    int animaniax$fertilityCooldownRemaining();
    boolean animaniax$hasGivenBirth();
    void animaniax$setHasGivenBirth(boolean hasGivenBirth);
    void animaniax$setMotherUUID(UUID motherUUID);
    boolean animaniax$isLactating();
    void animaniax$setLactating(boolean lactating);
    int animaniax$gestationRemaining();
    void animaniax$beginPregnancy(CompoundTag baby, String mateId);
    void animaniax$beginPregnancy(ListTag babies, String mateId);
    UUID animaniax$courtshipPartner();
    void animaniax$setCourtshipPartner(UUID partner);
    UUID animaniax$matePartner();
    void animaniax$setMatePartner(UUID partner);
    boolean animaniax$canMateBonded(Animal other);
    boolean animaniax$canMateReserved(Animal other);
}
