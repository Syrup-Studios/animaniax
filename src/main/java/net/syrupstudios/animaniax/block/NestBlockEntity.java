package net.syrupstudios.animaniax.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.PeafowlEntity;
import net.syrupstudios.animaniax.entity.PeafowlMorphState;
import net.syrupstudios.animaniax.item.PeafowlContent;
import net.syrupstudios.animaniax.item.BrownEggContent;
import java.util.ArrayList;
import java.util.List;

public final class NestBlockEntity extends BlockEntity {
    private static final String UNKNOWN_BREED = "unknown";
    private final List<String> eggMothers = new ArrayList<>();
    private final List<String> eggFathers = new ArrayList<>();

    public NestBlockEntity(BlockPos pos, BlockState state) {
        super(NestContent.NEST_ENTITY, pos, state);
    }

    public void clearEggs() {
        eggMothers.clear();
        eggFathers.clear();
        setChanged();
    }

    public void syncEggMetadata(int eggCount) {
        int count = Math.max(0, Math.min(3, eggCount));
        boolean changed = false;
        while (eggMothers.size() > count) { eggMothers.remove(eggMothers.size() - 1); changed = true; }
        while (eggFathers.size() > count) { eggFathers.remove(eggFathers.size() - 1); changed = true; }
        while (eggMothers.size() < count) { eggMothers.add(UNKNOWN_BREED); changed = true; }
        while (eggFathers.size() < count) { eggFathers.add(""); changed = true; }
        if (changed) setChanged();
    }

    public void addEggParent(String breed) {
        if (eggMothers.size() < 3 && eggFathers.size() == eggMothers.size()) {
            eggMothers.add(breed);
            eggFathers.add("");
        }
        setChanged();
    }

    public boolean canAcceptEgg(String breed, int eggCount) {
        if (eggCount <= 0) return true;
        syncEggMetadata(eggCount);
        if (eggStack(0).getItem() != eggStackForBreed(breed).getItem()) return false;
        String existingBreed = eggMothers.get(0);
        return UNKNOWN_BREED.equals(existingBreed) || existingBreed.equals(breed);
    }

    public void removeEggs(int count) {
        for (int i = 0; i < count && !eggMothers.isEmpty(); i++) {
            eggMothers.remove(0);
            if (!eggFathers.isEmpty()) eggFathers.remove(0);
        }
        setChanged();
    }

    public ItemStack eggStack(int index) {
        String breed = index < eggMothers.size() ? eggMothers.get(index) : UNKNOWN_BREED;
        return eggStackForBreed(breed);
    }

    private static ItemStack eggStackForBreed(String breed) {
        if (BrownEggContent.isBrownBreed(breed)) return new ItemStack(BrownEggContent.BROWN_EGG);
        return PeafowlContent.eggForBreed(breed);
    }

    public void tryHatch(ServerLevel server, BlockPos pos, BlockState state, RandomSource random) {
        int eggCount = state.getValue(NestBlock.EGGS);
        if (eggCount == 0 || AnimalCareConfig.entityBreedingLimit <= 0) return;
        syncEggMetadata(eggCount);

        String mother = eggMothers.get(0);
        boolean peafowlEgg = mother.startsWith("peafowl:");
        AABB maleRange = new AABB(pos.getX() - 3, pos.getY() - 3, pos.getZ() - 3,
                pos.getX() + 3, pos.getY() + 3, pos.getZ() + 3);
        List<Chicken> males = server.getEntitiesOfClass(Chicken.class, maleRange, chicken ->
                (chicken.getType() == PeafowlEntity.TYPE) == peafowlEgg && !chicken.isBaby()
                        && !((ChickenSexState) chicken).animaniax$isHen()
                        && !((AnimalSterilizationState) chicken).animaniax$isSterilized());
        if (males.isEmpty()) return;

        AABB populationRange = new AABB(pos.getX() - 15, pos.getY() - 15, pos.getZ() - 15,
                pos.getX() + 15, pos.getY() + 15, pos.getZ() + 15);
        long population = server.getEntitiesOfClass(Chicken.class, populationRange,
                chicken -> (chicken.getType() == PeafowlEntity.TYPE) == peafowlEgg).size();
        if (population >= AnimalCareConfig.entityBreedingLimit) return;

        for (Chicken rooster : males) {
            if (random.nextInt(AnimalCareConfig.eggHatchChance) != 0) continue;
            String sire = peafowlEgg ? "peafowl:" + ((PeafowlMorphState) rooster).animaniax$getMorph()
                    : ((ChickenBreedState) rooster).animaniax$getChickenBreed();
            Chicken chick = peafowlEgg ? PeafowlEntity.TYPE.create(server) : EntityType.CHICKEN.create(server);
            if (chick == null) return;

            if (peafowlEgg) {
                String motherMorph = mother.substring("peafowl:".length());
                String fatherMorph = sire.substring("peafowl:".length());
                ((PeafowlMorphState) chick).animaniax$setMorph(random.nextBoolean() ? motherMorph : fatherMorph);
            } else if (UNKNOWN_BREED.equals(mother)) {
                ((ChickenBreedState) chick).animaniax$setChickenBreed(sire);
            } else {
                ((ChickenBreedState) chick).animaniax$setChickenBreed(random.nextBoolean() ? mother : sire);
            }
            chick.setBaby(true);
            chick.moveTo(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
                    random.nextFloat() * 360.0F, 0.0F);
            if (!server.addFreshEntity(chick)) continue;

            server.setBlock(pos, state.setValue(NestBlock.EGGS, eggCount - 1), 3);
            removeEggs(1);
            setChanged();
            return;
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        eggMothers.clear();
        eggFathers.clear();
        ListTag mothers = tag.getList("EggMothers", 8);
        ListTag fathers = tag.getList("EggFathers", 8);
        ListTag legacyParents = tag.getList("EggParents", 8);
        for (int i = 0; i < 3 && (i < mothers.size() || i < legacyParents.size()); i++) {
            eggMothers.add(i < mothers.size() ? mothers.getString(i) : legacyParents.getString(i));
            eggFathers.add(i < fathers.size() ? fathers.getString(i) : "");
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag mothers = new ListTag();
        ListTag fathers = new ListTag();
        eggMothers.forEach(breed -> mothers.add(StringTag.valueOf(breed)));
        eggFathers.forEach(breed -> fathers.add(StringTag.valueOf(breed)));
        tag.put("EggMothers", mothers);
        tag.put("EggFathers", fathers);
    }
}
