package net.syrupstudios.animaniax.entity;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NonTameRandomTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.syrupstudios.animaniax.entity.ai.JuvenilePlayGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.util.RandomSource;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;

public class DogEntity extends Wolf {
    private JuvenilePlayGoal playGoal;
    private static final EntityDataAccessor<String> BREED = SynchedEntityData.defineId(DogEntity.class, EntityDataSerializers.STRING);

    public DogEntity(EntityType<? extends Wolf> type, Level level) { super(type, level); }

    @Override protected void registerGoals() {
        super.registerGoals();
        targetSelector.removeAllGoals(goal -> goal instanceof NonTameRandomTargetGoal<?>);
        targetSelector.addGoal(5, new NonTameRandomTargetGoal<Animal>(this, Animal.class, false,
                target -> target instanceof Sheep || target instanceof Rabbit) {
            @Override public boolean canUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canUse();
            }

            @Override public boolean canContinueToUse() {
                return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse();
            }
        });
        playGoal = new JuvenilePlayGoal(this, DogEntity.class);
        goalSelector.addGoal(7, playGoal);
    }

    public JuvenilePlayGoal animaniax$getPlayGoal() { return playGoal; }

    @Override protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(BREED, DogBreed.WOLF.id());
    }

    public DogBreed getBreed() { return DogBreed.byId(entityData.get(BREED)); }
    public void setBreed(DogBreed breed) { entityData.set(BREED, breed.id()); }

    @Override public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putString("AnimaniaxBreed", getBreed().id());
    }

    @Override public void readAdditionalSaveData(CompoundTag tag) {
        boolean hasSavedHealth = tag.contains("Health", 99);
        float savedHealth = hasSavedHealth ? tag.getFloat("Health") : 0.0F;
        super.readAdditionalSaveData(tag);
        setBreed(DogBreed.byId(tag.getString("AnimaniaxBreed")));
        animaniax$restoreHealthLimit();
        if (hasSavedHealth) setHealth(savedHealth);
    }

    @Override public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        if (reason == MobSpawnType.NATURAL) {
            setBreed(breedForBiome(level, blockPosition(), getRandom()));
            ((AnimalReproductionState) this).animaniax$setFemale(true);
        } else if (reason == MobSpawnType.SPAWN_EGG) {
            setBreed(DogBreed.values()[getRandom().nextInt(DogBreed.values().length)]);
        }
        return result;
    }

    private static DogBreed breedForBiome(LevelAccessor level, net.minecraft.core.BlockPos pos, RandomSource random) {
        boolean snowy = level.getBiome(pos).is(TagKey.create(Registries.BIOME, DogContent.id("dog_snow_spawns")));
        boolean forest = level.getBiome(pos).is(net.minecraft.tags.BiomeTags.IS_FOREST);
        boolean mountain = level.getBiome(pos).is(net.minecraft.tags.BiomeTags.IS_MOUNTAIN);
        return mountain || !snowy && !forest || !random.nextBoolean() ? DogBreed.WOLF : DogBreed.FOX;
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        boolean sleeping = ((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping();
        if (isFood(held) && sleeping)
            return InteractionResult.SUCCESS;
        if (sleeping && net.syrupstudios.animaniax.item.WaterContainerTransfer.isWaterContainer(player, hand))
            return InteractionResult.SUCCESS;
        if (isTame() && !isFood(held) && held.is(ItemTags.WOLF_FOOD) && getHealth() < 20.0F) {
            FoodProperties food = AnimalCareConfig.foodProperties(held);
            if (food != null) {
                if (level().isClientSide) return InteractionResult.sidedSuccess(true);
                if (!player.getAbilities().instabuild) held.shrink(1);
                heal(food.nutrition());
                return InteractionResult.sidedSuccess(false);
            }
        }
        if (isFood(held)) {
            if (level().isClientSide) return InteractionResult.sidedSuccess(true);
            if (!isTame()) tame(player);
            ItemStack feed = held.copy();
            ((AnimalCareState) this).animaniax$feedForCare();
            AnimalReproductionState reproduction = (AnimalReproductionState) this;
            AnimalSterilizationState sterilization = (AnimalSterilizationState) this;
            if (!isBaby() && !isInLove() && canFallInLove() && !reproduction.animaniax$isPregnant()
                    && !sterilization.animaniax$isSterilized()) setInLove(player);
            if (!player.getAbilities().instabuild) held.shrink(1);
            if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feed);
            ((AnimalCareState) this).animaniax$markPlayerInteraction(true);
            return InteractionResult.sidedSuccess(false);
        }
        boolean waterContainer = !sleeping && (level().isClientSide
                ? net.syrupstudios.animaniax.item.WaterContainerTransfer.isWaterContainer(player, hand)
                : net.syrupstudios.animaniax.item.WaterContainerTransfer.water(player, hand));
        if (waterContainer) {
            if (!level().isClientSide) {
                ((net.syrupstudios.animaniax.entity.AnimalCareState) this).animaniax$waterForCare();
                AnimalReproductionState reproduction = (AnimalReproductionState) this;
                if (!isBaby() && !isInLove() && canFallInLove() && !reproduction.animaniax$isPregnant()
                        && !((AnimalSterilizationState) this).animaniax$isSterilized()) setInLove(player);
                ((AnimalCareState) this).animaniax$markPlayerInteraction(false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override public boolean isFood(ItemStack stack) { return stack.is(AnimalProfile.DOG_FOODS); }

    @Override public Wolf getBreedOffspring(ServerLevel level, AgeableMob mate) {
        DogEntity child = new DogEntity(DogContent.TYPE, level);
        if (mate instanceof DogEntity other) {
            boolean inheritThis = getRandom().nextBoolean();
            child.setBreed(inheritThis ? getBreed() : other.getBreed());
            child.setVariant(inheritThis ? getVariant() : other.getVariant());
            if (isTame()) {
                child.setOwnerUUID(getOwnerUUID());
                child.setTame(true, true);
            }
        }
        return child;
    }

    @Override public boolean canMate(Animal other) {
        if (!((AnimalReproductionState) this).animaniax$canMateReserved(other)
                || !(other instanceof DogEntity) || other.getType() != getType() || !super.canMate(other)
                || !(this instanceof AnimalReproductionState first) || !(other instanceof AnimalReproductionState second)
                || !(this instanceof AnimalSterilizationState firstSterilization)
                || !(other instanceof AnimalSterilizationState secondSterilization)
                || !(this instanceof AnimalCareState firstCare) || !(other instanceof AnimalCareState secondCare)) return false;
        return first.animaniax$isFemale() != second.animaniax$isFemale()
                && !first.animaniax$isPregnant() && !second.animaniax$isPregnant()
                && first.animaniax$fertilityCooldownRemaining() == 0
                && second.animaniax$fertilityCooldownRemaining() == 0
                && !firstSterilization.animaniax$isSterilized() && !secondSterilization.animaniax$isSterilized()
                && !firstCare.animaniax$careBlocksBreeding() && !secondCare.animaniax$careBlocksBreeding();
    }

    @Override protected void applyTamingSideEffects() {
        super.applyTamingSideEffects();
        animaniax$restoreHealthLimit();
    }

    private void animaniax$restoreHealthLimit() {
        getAttribute(Attributes.MAX_HEALTH).setBaseValue(isTame() ? 20.0 : 8.0);
        setHealth(Math.min(getHealth(), getMaxHealth()));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes().add(Attributes.ATTACK_DAMAGE, 2.5);
    }
}
