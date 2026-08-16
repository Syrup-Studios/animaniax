package net.syrupstudios.animaniax.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.syrupstudios.animaniax.entity.ai.AnimalCareGoal;
import net.syrupstudios.animaniax.block.MudBlock;
import net.syrupstudios.animaniax.entity.ai.AnimalCourtshipGoal;
import net.syrupstudios.animaniax.entity.ai.AnimalSleepGoal;
import net.syrupstudios.animaniax.entity.ai.BullRetaliationGoal;
import net.syrupstudios.animaniax.entity.ai.SheepSparringGoal;
import net.syrupstudios.animaniax.entity.ai.GoatSparringGoal;
import net.syrupstudios.animaniax.entity.ai.NestingGoal;
import net.syrupstudios.animaniax.entity.ai.PigSnuffleGoal;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.entity.AnimalReproductionState;
import net.syrupstudios.animaniax.entity.AnimalSterilizationState;
import net.syrupstudios.animaniax.entity.ChickenSexState;
import net.syrupstudios.animaniax.entity.CowBreedState;
import net.syrupstudios.animaniax.entity.PigBreedState;
import net.syrupstudios.animaniax.entity.GoatBreedState;
import net.syrupstudios.animaniax.entity.SheepBreedState;
import net.syrupstudios.animaniax.entity.ChickenBreedState;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalProfile.Capability;
import net.syrupstudios.animaniax.entity.RabbitBreedState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
abstract class MobCareMixin implements AnimalCareState, AnimalSleepState, PigBreedState {
    @Unique private static final EntityDataAccessor<Boolean> ANIMANIAX_SLEEPING =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BOOLEAN);
    @Shadow @Final protected GoalSelector goalSelector;

    @Unique private int animaniax$hunger = 100;
    @Unique private int animaniax$thirst = 100;
    @Unique private int animaniax$mudEnrichment = 100;
    @Unique private int animaniax$careTicks;
    @Unique private int animaniax$hungerTimer;
    @Unique private int animaniax$thirstTimer;
    @Unique private int animaniax$starvationTimer;
    @Unique private int animaniax$hungerLimit = 12000;
    @Unique private int animaniax$thirstLimit = 12000;
    @Unique private boolean animaniax$careActivated;
    @Unique private boolean animaniax$playerInteracted;
    @Unique private boolean animaniax$handFed;
    @Unique private String animaniax$pigBreed = "Yorkshire";
    @Unique private float animaniax$lastHealth = -1.0F;

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void animaniax$defineSleepState(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(ANIMANIAX_SLEEPING, false);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void animaniax$decayCare(CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (!mob.level().isClientSide && animaniax$isSleepingAnimal(mob)) {
            float health = mob.getHealth();
            if (animaniax$lastHealth >= 0.0F && health < animaniax$lastHealth) animaniax$setSleeping(false);
            animaniax$lastHealth = health;
        }
        if (!animaniax$isFarmAnimal(mob) || mob.level().isClientSide) return;
        if (mob.getType() == EntityType.PIG) {
            boolean onMud = MudBlock.isMud(mob.level().getBlockState(mob.blockPosition()))
                    || MudBlock.isMud(mob.level().getBlockState(mob.blockPosition().below()));
            if (onMud) {
                mob.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 4, false, false));
                animaniax$mudEnrichment = 100;
            }
        }
        if (++animaniax$careTicks >= 1200) {
            animaniax$careTicks = 0;
            if (mob.getType() == EntityType.PIG) animaniax$mudEnrichment = Math.max(0, animaniax$mudEnrichment - 1);
        }
        if (AnimalCareConfig.ambianceMode) {
            animaniax$hunger = 100;
            animaniax$thirst = 100;
            return;
        }
        if (animaniax$careTicks % 60 == 0
                && AnimalCareConfig.showUnhappyParticles
                && !animaniax$isFed() && !animaniax$isWatered()
                && !animaniax$isSleeping() && !mob.isSleeping()
                && (!AnimalCareConfig.requireAnimalInteractionForAI || animaniax$playerInteracted)
                && mob.level() instanceof ServerLevel server) {
            var random = mob.getRandom();
            double width = mob.getBbWidth();
            server.sendParticles(ParticleTypes.SMOKE,
                    mob.getX() + random.nextDouble() * width - width,
                    mob.getY() + 1.5 + random.nextDouble() * mob.getBbHeight(),
                    mob.getZ() + random.nextDouble() * width - width,
                    1, random.nextGaussian() * 0.001, random.nextGaussian() * 0.001,
                    random.nextGaussian() * 0.001, 0.0);
        }
        boolean careActive = animaniax$careActivated || animaniax$playerInteracted
                || !AnimalCareConfig.requireAnimalInteractionForAI;
        if (careActive && animaniax$hunger >= 50 && ++animaniax$hungerTimer >= animaniax$hungerLimit)
            animaniax$hunger = 49;
        if (animaniax$thirstTimer < animaniax$thirstLimit) animaniax$thirstTimer++;
        if (careActive && animaniax$thirst >= 50 && animaniax$thirstTimer >= animaniax$thirstLimit)
            animaniax$thirst = 49;
        if (animaniax$hunger < 50 && animaniax$thirst < 50) {
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2, 1, false, false));
            if (AnimalCareConfig.animalsStarve) {
                if (!animaniax$isSleeping() && !mob.isSleeping()) {
                    if (animaniax$starvationTimer >= AnimalCareConfig.starvationTimer) {
                        mob.hurt(mob.damageSources().starve(), 4.0F);
                        animaniax$starvationTimer = 0;
                    }
                    animaniax$starvationTimer++;
                }
            }
        } else if (animaniax$hunger < 50 || animaniax$thirst < 50) {
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2, 0, false, false));
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void animaniax$addCareGoals(EntityType<? extends Mob> type, Level level, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (animaniax$isSleepingAnimal(mob)) goalSelector.addGoal(0, new AnimalSleepGoal(mob));
        if (!animaniax$isFarmAnimal(mob)) return;
        if (mob instanceof Animal animal && animaniax$isReproductiveAnimal(mob)
                && !AnimalProfile.has(mob.getType(), Capability.POULTRY_REPRODUCTION))
            goalSelector.addGoal(5, new AnimalCourtshipGoal(animal));
        animaniax$hungerLimit = AnimalCareConfig.feedTimer + mob.getRandom().nextInt(100);
        animaniax$thirstLimit = AnimalCareConfig.waterTimer + mob.getRandom().nextInt(100);
        if (mob.getType() == EntityType.COW) goalSelector.addGoal(0, new BullRetaliationGoal(mob));
        if (mob.getType() == EntityType.SHEEP)
            goalSelector.addGoal(8, new SheepSparringGoal((net.minecraft.world.entity.animal.Sheep) mob));
        if (mob.getType() == EntityType.GOAT)
            goalSelector.addGoal(8, new GoatSparringGoal((net.minecraft.world.entity.animal.goat.Goat) mob));
        if (AnimalProfile.has(mob.getType(), Capability.NESTING_GOAL)) {
            goalSelector.addGoal(4, new NestingGoal((Chicken) mob));
        }
        goalSelector.addGoal(6, new AnimalCareGoal(mob, AnimalCareGoal.Need.WATER));
        if (AnimalProfile.has(mob.getType(), Capability.PIG_ENRICHMENT_GOALS)) {
            goalSelector.addGoal(6, new AnimalCareGoal(mob, AnimalCareGoal.Need.TRUFFLE));
            goalSelector.addGoal(8, new PigSnuffleGoal(mob));
        }
        if (AnimalProfile.has(mob.getType(), Capability.SEED_GOAL)) {
            goalSelector.addGoal(7, new AnimalCareGoal(mob, AnimalCareGoal.Need.SEEDS));
        }
        if (AnimalProfile.has(mob.getType(), Capability.GRASS_GOAL)) {
            goalSelector.addGoal(7, new AnimalCareGoal(mob, AnimalCareGoal.Need.GRASS));
        }
        if (AnimalProfile.has(mob.getType(), Capability.PIG_ENRICHMENT_GOALS)) {
            goalSelector.addGoal(6, new AnimalCareGoal(mob, AnimalCareGoal.Need.MUD));
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$saveCare(CompoundTag tag, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (animaniax$isSleepingAnimal(mob)) tag.putBoolean("AnimaniaxSleeping", animaniax$isSleeping());
        if (!animaniax$isFarmAnimal(mob)) return;
        tag.putInt("AnimaniaxHunger", animaniax$hunger);
        tag.putInt("AnimaniaxThirst", animaniax$thirst);
        tag.putInt("AnimaniaxCareTicks", animaniax$careTicks);
        tag.putInt("AnimaniaxHungerTimer", animaniax$hungerTimer);
        tag.putInt("AnimaniaxThirstTimer", animaniax$thirstTimer);
        tag.putInt("AnimaniaxStarvationTimer", animaniax$starvationTimer);
        tag.putInt("AnimaniaxHungerLimit", animaniax$hungerLimit);
        tag.putInt("AnimaniaxThirstLimit", animaniax$thirstLimit);
        tag.putBoolean("AnimaniaxCareActivated", animaniax$careActivated);
        tag.putBoolean("AnimaniaxPlayerInteracted", animaniax$playerInteracted);
        tag.putBoolean("AnimaniaxHandFed", animaniax$handFed);
        if (mob.getType() == EntityType.PIG) tag.putInt("AnimaniaxMudEnrichment", animaniax$mudEnrichment);
        if (mob.getType() == EntityType.PIG) tag.putString("AnimaniaxPigBreed", animaniax$pigBreed);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void animaniax$loadCare(CompoundTag tag, CallbackInfo ci) {
        Mob mob = (Mob) (Object) this;
        if (animaniax$isSleepingAnimal(mob)) animaniax$setSleeping(tag.getBoolean("AnimaniaxSleeping"));
        if (!animaniax$isFarmAnimal(mob)) return;
        animaniax$hunger = tag.contains("AnimaniaxHunger") ? Math.max(0, Math.min(100, tag.getInt("AnimaniaxHunger"))) : 100;
        animaniax$thirst = tag.contains("AnimaniaxThirst") ? Math.max(0, Math.min(100, tag.getInt("AnimaniaxThirst"))) : 100;
        animaniax$careTicks = tag.getInt("AnimaniaxCareTicks");
        animaniax$hungerLimit = tag.contains("AnimaniaxHungerLimit")
                ? Math.max(AnimalCareConfig.feedTimer, Math.min(AnimalCareConfig.feedTimer + 99, tag.getInt("AnimaniaxHungerLimit")))
                : AnimalCareConfig.feedTimer;
        animaniax$thirstLimit = tag.contains("AnimaniaxThirstLimit")
                ? Math.max(AnimalCareConfig.waterTimer, Math.min(AnimalCareConfig.waterTimer + 99, tag.getInt("AnimaniaxThirstLimit")))
                : AnimalCareConfig.waterTimer;
        animaniax$hungerTimer = Math.max(0, Math.min(animaniax$hungerLimit, tag.getInt("AnimaniaxHungerTimer")));
        animaniax$thirstTimer = Math.max(0, Math.min(animaniax$thirstLimit, tag.getInt("AnimaniaxThirstTimer")));
        animaniax$starvationTimer = Math.max(0, Math.min(AnimalCareConfig.starvationTimer, tag.getInt("AnimaniaxStarvationTimer")));
        animaniax$careActivated = tag.getBoolean("AnimaniaxCareActivated");
        animaniax$playerInteracted = tag.getBoolean("AnimaniaxPlayerInteracted");
        animaniax$handFed = tag.getBoolean("AnimaniaxHandFed");
        if (mob.getType() == EntityType.PIG) {
            animaniax$mudEnrichment = tag.contains("AnimaniaxMudEnrichment")
                    ? Math.max(0, Math.min(100, tag.getInt("AnimaniaxMudEnrichment"))) : 100;
            if (tag.contains("AnimaniaxPigBreed")) animaniax$setPigBreed(tag.getString("AnimaniaxPigBreed"));
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void animaniax$careForAnimal(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Mob mob = (Mob) (Object) this;
        if (!animaniax$isFarmAnimal(mob)) return;

        ItemStack held = player.getItemInHand(hand);
        if (animaniax$isSleeping() && (held.is(Items.WATER_BUCKET)
                || mob instanceof Animal animal && animal.isFood(held))) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (!mob.level().isClientSide && animaniax$isReproductiveAnimal(mob))
            ((AnimalCareState) mob).animaniax$markPlayerInteraction(false);
        if (mob.getType() == EntityType.MOOSHROOM && held.is(Items.BOWL)) {
            if (!mob.level().isClientSide) {
                AnimalReproductionState reproduction = (AnimalReproductionState) mob;
                if (!mob.isBaby() && animaniax$isContent() && reproduction.animaniax$isFemale()
                        && reproduction.animaniax$isLactating()) {
                    if (!player.getAbilities().instabuild) held.shrink(1);
                    ItemStack stew = new ItemStack(Items.MUSHROOM_STEW);
                    if (!player.getInventory().add(stew)) player.drop(stew, false);
                    ((AnimalCareState) mob).animaniax$milk();
                } else {
                    String reason = !reproduction.animaniax$isFemale() ? "Only female mooshrooms can produce stew."
                            : !reproduction.animaniax$isLactating() ? "This mooshroom is not lactating."
                            : "This mooshroom is unhappy. Feed and water it before collecting stew.";
                    player.sendSystemMessage(net.minecraft.network.chat.Component.literal(reason));
                }
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(mob.level().isClientSide));
            return;
        }
        if ("animaniax:carving_knife".equals(net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(held.getItem()).toString())) {
            if (mob.level().isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            AnimalSterilizationState sterilization = (AnimalSterilizationState) mob;
            boolean male = mob instanceof Chicken chicken
                    ? !((ChickenSexState) chicken).animaniax$isHen()
                    : mob instanceof AnimalReproductionState reproduction && !reproduction.animaniax$isFemale();
            if (mob.isBaby() || !male) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Only adult male animals can be sterilized."));
            } else if (sterilization.animaniax$isSterilized()) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("This animal is already sterilized."));
            } else {
                sterilization.animaniax$sterilize();
                if (!player.getAbilities().instabuild) held.hurtAndBreak(1, player, hand == InteractionHand.MAIN_HAND
                        ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal("The animal is now sterilized."));
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (player.isShiftKeyDown() && player.getMainHandItem().isEmpty() && player.getOffhandItem().isEmpty()) {
            if (mob.level().isClientSide) {
                cir.setReturnValue(InteractionResult.SUCCESS);
                return;
            }
            String status = "Hunger: " + (AnimalCareConfig.ambianceMode ? 100 : animaniax$hunger)
                    + "/100 | Thirst: " + (AnimalCareConfig.ambianceMode ? 100 : animaniax$thirst) + "/100 | "
                    + (mob.getType() == EntityType.PIG ? "Breed: " + animaniax$pigBreed + " | Mud: " + animaniax$mudEnrichment + "/100 | " : "")
                    + (mob.getType() == EntityType.SHEEP ? "Breed: " + ((SheepBreedState) mob).animaniax$getSheepBreed() + " | " : "")
                    + (mob.getType() == EntityType.CHICKEN ? "Breed: " + ((ChickenBreedState) mob).animaniax$getChickenBreed() + " | " : "")
                    + (animaniax$isContent() ? "Content" : mob.getType() == EntityType.PIG
                            ? "Unhappy: feed, water, and mud access are needed for breeding"
                            : "Unhappy: feed and water to restore production");
            if (mob instanceof AnimalReproductionState reproduction && animaniax$isReproductiveAnimal(mob)) {
                if (mob.getType() == EntityType.COW) status += " | Breed: " + ((CowBreedState) mob).animaniax$getBreed();
                if (mob.getType() == EntityType.GOAT) status += " | Breed: " + ((GoatBreedState) mob).animaniax$getGoatBreed();
                if (mob.getType() == EntityType.RABBIT) status += " | Breed: " + ((RabbitBreedState) mob).animaniax$getRabbitBreed();
                boolean female = mob instanceof Chicken chicken
                        ? ((ChickenSexState) chicken).animaniax$isHen() : reproduction.animaniax$isFemale();
                status += " | " + (female ? "Female" : "Male");
                if (mob.getType() == EntityType.COW || mob.getType() == EntityType.MOOSHROOM
                        || mob.getType() == EntityType.GOAT || mob.getType() == EntityType.SHEEP) {
                    status += reproduction.animaniax$isLactating() ? " | Lactating" : " | Not lactating";
                }
                if (reproduction.animaniax$isPregnant()) {
                    status += " | Pregnant: " + reproduction.animaniax$gestationRemaining() + " ticks remaining";
                }
            }
            if (mob instanceof AnimalSterilizationState sterilization) {
                status += " | " + (sterilization.animaniax$isSterilized() ? "Sterilized" : "Fertile");
            }
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(status));
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (mob.level().isClientSide) {
            if (held.is(Items.WATER_BUCKET)
                    || ((Animal) mob).isFood(held)) {
                cir.setReturnValue(InteractionResult.SUCCESS);
            }
            return;
        }
        if (held.is(Items.WATER_BUCKET)) {
            if (!player.getAbilities().instabuild) {
                held.shrink(1);
                ItemStack bucket = new ItemStack(Items.BUCKET);
                if (held.isEmpty()) player.setItemInHand(hand, bucket);
                else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
            }
            animaniax$careActivated = true;
            animaniax$resetThirst(mob);
            Animal animal = (Animal) mob;
            boolean pregnant = mob instanceof AnimalReproductionState reproduction && reproduction.animaniax$isPregnant();
            boolean sterilized = mob instanceof AnimalSterilizationState sterilization && sterilization.animaniax$isSterilized();
            if (!animal.isBaby() && !animal.isInLove() && animal.canFallInLove() && !pregnant && !sterilized)
                animal.setInLove(player);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(animaniax$isContent()
                    ? "The animal is content and can produce again." : "The animal drank water."));
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        if (((Animal) mob).isFood(held)) {
            Animal animal = (Animal) mob;
            boolean needsCare = animaniax$hunger < 100;
            if (mob.isBaby() || needsCare) {
                animaniax$careActivated = true;
                animaniax$resetHunger(mob);
            }
            boolean blockedFromBreeding = mob instanceof AnimalSterilizationState sterilization
                    && sterilization.animaniax$isSterilized()
                    || mob instanceof AnimalReproductionState reproduction && reproduction.animaniax$isPregnant();
            if (mob.isBaby() || !blockedFromBreeding && animal.canFallInLove()) {
                animaniax$markHandFeedAfterSleepCheck(mob);
                return;
            }
            if (needsCare) {
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(animaniax$isContent()
                        ? "The animal is content and can produce again." : "The animal ate."));
                if (!player.getAbilities().instabuild) held.shrink(1);
                animaniax$markHandFeedAfterSleepCheck(mob);
            }
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

    }

    @Override
    @Unique
    public boolean animaniax$careBlocksBreeding() {
        Mob mob = (Mob) (Object) this;
        return animaniax$isFarmAnimal(mob) && (!animaniax$isFed() || !animaniax$isWatered()
                || mob.getType() == EntityType.PIG && animaniax$mudEnrichment < 50);
    }

    @Override
    @Unique
    public boolean animaniax$needsWater() {
        Mob mob = (Mob) (Object) this;
        return !AnimalCareConfig.ambianceMode && animaniax$isFarmAnimal(mob) && animaniax$thirst < 50;
    }

    @Override
    @Unique
    public boolean animaniax$needsSeeds() {
        Mob mob = (Mob) (Object) this;
        return !AnimalCareConfig.ambianceMode && AnimalProfile.has(mob.getType(), Capability.SEED_GOAL) && animaniax$hunger < 50;
    }

    @Override
    @Unique
    public boolean animaniax$needsMud() {
        Mob mob = (Mob) (Object) this;
        return AnimalProfile.has(mob.getType(), Capability.PIG_ENRICHMENT_GOALS) && animaniax$mudEnrichment < 50
                && !animaniax$needsWater() && !animaniax$needsGraze();
    }

    @Override
    @Unique
    public boolean animaniax$needsGraze() {
        Mob mob = (Mob) (Object) this;
        return !AnimalCareConfig.ambianceMode && AnimalProfile.has(mob.getType(), Capability.GRASS_GOAL) && animaniax$hunger < 50;
    }

    @Override
    @Unique
    public boolean animaniax$isContent() {
        Mob mob = (Mob) (Object) this;
        return animaniax$isFarmAnimal(mob) && (AnimalCareConfig.ambianceMode || animaniax$hunger >= 50 && animaniax$thirst >= 50)
                && (mob.getType() != EntityType.PIG || animaniax$mudEnrichment >= 50);
    }

    @Override
    @Unique
    public boolean animaniax$isFed() { return AnimalCareConfig.ambianceMode || animaniax$hunger >= 50; }

    @Override
    @Unique
    public boolean animaniax$isWatered() { return AnimalCareConfig.ambianceMode || animaniax$thirst >= 50; }

    @Override @Unique public boolean animaniax$wasInteractedWith() { return animaniax$playerInteracted; }
    @Override @Unique public boolean animaniax$wasHandFed() { return animaniax$handFed; }
    @Override @Unique public void animaniax$markPlayerInteraction(boolean handFed) {
        Mob mob = (Mob) (Object) this;
        if (mob.level().isClientSide || animaniax$isSleepingForBreeding(mob)) return;
        animaniax$playerInteracted = true;
        if (handFed) animaniax$handFed = true;
    }
    @Override @Unique public void animaniax$clearHandFed() { animaniax$handFed = false; }

    @Override
    @Unique
    public boolean animaniax$hasGrowthCare() {
        Mob mob = (Mob) (Object) this;
        return animaniax$isFarmAnimal(mob) && (AnimalCareConfig.ambianceMode || animaniax$hunger >= 50 && animaniax$thirst >= 50);
    }

    @Override
    @Unique
    public void animaniax$milk() {
        animaniax$thirst = Math.min(animaniax$thirst, 49);
    }

    @Override
    @Unique
    public void animaniax$drink() {
        animaniax$resetThirst((Mob) (Object) this);
    }

    @Override
    @Unique
    public boolean animaniax$feedForCare() {
        Mob mob = (Mob) (Object) this;
        if (!mob.isBaby() && animaniax$hunger >= 100) return false;
        animaniax$careActivated = true;
        animaniax$resetHunger(mob);
        return true;
    }

    @Override
    @Unique
    public void animaniax$waterForCare() {
        animaniax$careActivated = true;
        animaniax$resetThirst((Mob) (Object) this);
    }

    @Override
    @Unique
    public void animaniax$graze() {
        animaniax$resetHunger((Mob) (Object) this);
    }

    @Override
    @Unique
    public void animaniax$eatSlop() {
        animaniax$careActivated = true;
        animaniax$resetHunger((Mob) (Object) this);
    }

    @Override
    @Unique
    public void animaniax$useMud() {
        animaniax$mudEnrichment = 100;
    }

    @Unique
    private void animaniax$resetHunger(Mob mob) {
        animaniax$hunger = 100;
        animaniax$hungerTimer = 0;
        animaniax$hungerLimit = AnimalCareConfig.feedTimer + mob.getRandom().nextInt(100);
    }

    @Unique
    private void animaniax$resetThirst(Mob mob) {
        animaniax$thirst = 100;
        animaniax$thirstTimer = 0;
        animaniax$thirstLimit = AnimalCareConfig.waterTimer + mob.getRandom().nextInt(100);
    }

    @Override
    @Unique
    public String animaniax$getPigBreed() { return animaniax$pigBreed; }

    @Override
    @Unique
    public void animaniax$setPigBreed(String breed) {
        if (breed != null && PigBreedState.BREEDS.contains(breed)) {
            animaniax$pigBreed = breed;
            return;
        }
        animaniax$pigBreed = "Yorkshire";
    }

    @Unique
    private static boolean animaniax$isFarmAnimal(Mob mob) {
        return AnimalProfile.has(mob.getType(), Capability.CARE);
    }

    @Override @Unique public boolean animaniax$isSleeping() {
        return ((Mob) (Object) this).getEntityData().get(ANIMANIAX_SLEEPING);
    }

    @Override @Unique public void animaniax$setSleeping(boolean sleeping) {
        ((Mob) (Object) this).getEntityData().set(ANIMANIAX_SLEEPING, sleeping);
    }

    @Unique
    private static boolean animaniax$isSleepingAnimal(Mob mob) {
        return AnimalProfile.has(mob.getType(), Capability.SLEEP);
    }

    @Unique
    private boolean animaniax$isSleepingForBreeding(Mob mob) {
        return animaniax$isSleeping() || mob.isSleeping();
    }

    @Unique
    private void animaniax$markHandFeedAfterSleepCheck(Mob mob) {
        if (!mob.level().isClientSide && !animaniax$isSleepingForBreeding(mob))
            ((AnimalCareState) mob).animaniax$markPlayerInteraction(true);
    }

    @Unique
    private static boolean animaniax$isReproductiveAnimal(Mob mob) {
        return AnimalProfile.has(mob.getType(), Capability.REPRODUCTIVE_INTERACTIONS);
    }
}
