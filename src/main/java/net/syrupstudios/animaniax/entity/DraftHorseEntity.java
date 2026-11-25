package net.syrupstudios.animaniax.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.util.RandomSource;
import net.syrupstudios.animaniax.sound.AnimaniaSounds;
import org.jetbrains.annotations.Nullable;

public final class DraftHorseEntity extends Horse {
    private boolean animaniax$boosting;
    private int animaniax$boostTime;
    private int animaniax$totalBoostTime;

    public DraftHorseEntity(EntityType<? extends Horse> type, Level level) {
        super(type, level);
        setTamed(true);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, groupData);
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.CHUNK_GENERATION) {
            LivestockFamilySpawner.spawn(level, difficulty, this);
        }
        return result;
    }

    @Override
    protected void randomizeAttributes(RandomSource random) {
        super.randomizeAttributes(random);
        getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Horse.createBaseHorseAttributes().add(Attributes.MOVEMENT_SPEED, 0.2D).add(Attributes.STEP_HEIGHT, 1.2D);
    }

    @Override
    public float maxUpStep() {
        return isBaby() ? Math.max(0.0F, super.maxUpStep() - 0.1F) : super.maxUpStep();
    }

    public boolean animaniax$boost() {
        if (animaniax$boosting) return false;
        animaniax$boosting = true;
        animaniax$boostTime = 0;
        animaniax$totalBoostTime = getRandom().nextInt(100) + 20;
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!animaniax$boosting) return;
        if (!level().isClientSide) addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1, 3, false, false));
        if (++animaniax$boostTime >= animaniax$totalBoostTime) {
            animaniax$boosting = false;
            animaniax$boostTime = 0;
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setTamed(true);
    }

    @Override
    protected void doPlayerRide(Player player) {
        AnimalCareState care = (AnimalCareState) (Object) this;
        if (!isSaddled() || isSleeping() || ((AnimalSleepState) (Object) this).animaniax$isSleeping()
                || !care.animaniax$isFed() || !care.animaniax$isWatered()) return;
        super.doPlayerRide(player);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack held = player.getItemInHand(hand);
        ItemStack feedItem = held.copy();
        AnimalCareState care = (AnimalCareState) (Object) this;
        if (held.is(Items.WATER_BUCKET)) {
            if (level().isClientSide) return InteractionResult.SUCCESS;
            care.animaniax$waterForCare();
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (held.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
            }
            return InteractionResult.SUCCESS;
        }
        boolean careFood = held.is(AnimalProfile.HORSE_FOODS);
        if (careFood && (((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping()))
            return InteractionResult.SUCCESS;
        if (level().isClientSide && careFood) return InteractionResult.SUCCESS;
        boolean needsCare = careFood && (isBaby() || care.animaniax$needsGraze());
        if (held.isEmpty() && !player.isSecondaryUseActive()
                && (isBaby() || !isSaddled() || !care.animaniax$hasGrowthCare()))
            return InteractionResult.sidedSuccess(level().isClientSide);
        int before = held.getCount();
        InteractionResult result = super.mobInteract(player, hand);
        boolean feedHandledByAnimal = isBaby() || isVehicle();
        boolean consumed = held.getCount() < before;
        if (needsCare && !consumed && !player.getAbilities().instabuild) {
            held.shrink(1);
            consumed = true;
        }
        if (needsCare && consumed) care.animaniax$feedForCare();
        if (careFood && consumed) care.animaniax$markPlayerInteraction(true);
        boolean feedTriggered = careFood && consumed && (!feedHandledByAnimal || isBaby());
        if (feedTriggered && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
            net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feedItem);
        if (careFood && canStartBreeding()) {
            if (!consumed && !player.getAbilities().instabuild) {
                held.shrink(1);
                consumed = true;
            }
            if (consumed || player.getAbilities().instabuild) {
                if (!feedTriggered && !feedHandledByAnimal && player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)
                    net.syrupstudios.animaniax.advancement.FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feedItem);
                care.animaniax$markPlayerInteraction(true);
                setInLove(player);
                return InteractionResult.SUCCESS;
            }
        }
        return needsCare && consumed && result == InteractionResult.PASS ? InteractionResult.SUCCESS : result;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return super.isFood(stack) || stack.is(Items.WHEAT) || stack.is(Items.APPLE) || stack.is(Items.CARROT);
    }

    private boolean canStartBreeding() {
        AnimalReproductionState reproduction = (AnimalReproductionState) (Object) this;
        AnimalSterilizationState sterilization = (AnimalSterilizationState) (Object) this;
        AnimalCareState care = (AnimalCareState) (Object) this;
        return !isBaby() && getAge() == 0 && canFallInLove() && getHealth() >= getMaxHealth()
                && !isVehicle() && !isPassenger() && !reproduction.animaniax$isPregnant()
                && reproduction.animaniax$fertilityCooldownRemaining() == 0 && !sterilization.animaniax$isSterilized()
                && !care.animaniax$careBlocksBreeding();
    }

    @Override
    public boolean canMate(net.minecraft.world.entity.animal.Animal other) {
        if (!(other instanceof DraftHorseEntity) || !super.canMate(other)) return false;
        AnimalReproductionState reproduction = (AnimalReproductionState) (Object) this;
        AnimalReproductionState mate = (AnimalReproductionState) (Object) other;
        if (!reproduction.animaniax$canMateReserved(other)) return false;
        AnimalCareState care = (AnimalCareState) (Object) this;
        AnimalCareState mateCare = (AnimalCareState) (Object) other;
        AnimalSterilizationState sterilization = (AnimalSterilizationState) (Object) this;
        AnimalSterilizationState mateSterilization = (AnimalSterilizationState) (Object) other;
        return reproduction.animaniax$isFemale() != mate.animaniax$isFemale()
                && !reproduction.animaniax$isPregnant() && !mate.animaniax$isPregnant()
                && reproduction.animaniax$fertilityCooldownRemaining() == 0
                && mate.animaniax$fertilityCooldownRemaining() == 0
                && !sterilization.animaniax$isSterilized() && !mateSterilization.animaniax$isSterilized()
                && !care.animaniax$careBlocksBreeding() && !mateCare.animaniax$careBlocksBreeding();
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return AnimaniaSounds.DRAFT_HORSE_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return AnimaniaSounds.DRAFT_HORSE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return AnimaniaSounds.DRAFT_HORSE_HURT;
    }

    @Override
    protected SoundEvent getAngrySound() {
        return AnimaniaSounds.DRAFT_HORSE_HURT;
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) {
        if (!(mate instanceof DraftHorseEntity)) return null;
        DraftHorseEntity foal = DraftHorseContent.DRAFT_HORSE.create(level);
        if (foal != null) {
            foal.setVariant(getVariant());
            setOffspringAttributes(mate, foal);
        }
        return foal;
    }
}
