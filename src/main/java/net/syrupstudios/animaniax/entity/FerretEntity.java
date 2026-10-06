package net.syrupstudios.animaniax.entity;

import java.util.EnumSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.Blocks;
import net.syrupstudios.animaniax.block.TroughBlock;
import net.syrupstudios.animaniax.block.TroughBlockEntity;
import net.syrupstudios.animaniax.block.TroughContent;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.syrupstudios.animaniax.block.NestBlock;
import net.syrupstudios.animaniax.block.NestBlockEntity;
import net.syrupstudios.animaniax.block.NestContent;
import net.syrupstudios.animaniax.advancement.FeedAnimalTrigger;
import net.syrupstudios.animaniax.config.AnimalCareConfig;

public final class FerretEntity extends TamableAnimal {
    private static final TagKey<Item> FERRET_FOODS = TagKey.create(Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath("animaniax", "ferret_foods"));
    private int fedTicks;
    private int wateredTicks;
    private int starvationTimer;
    private int nestForageCooldown;
    private boolean careInteracted;

    public FerretEntity(EntityType<? extends TamableAnimal> type, Level level) {
        super(type, level);
        fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
        wateredTicks = (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + getRandom().nextInt(200));
    }

    @Override public void tick() {
        super.tick();
        if (!level().isClientSide) {
            if (nestForageCooldown > 0) nestForageCooldown--;
            boolean careActive = !AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI);
            if (careActive) {
                if (!AnimalCareConfig.ambianceMode && fedTicks > 0) fedTicks--;
                if (wateredTicks > 0) wateredTicks--;
            }
            if (careActive) {
                if (fedTicks == 0 && wateredTicks == 0) {
                    addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 2, 1, false, false));
                    if (AnimalCareConfig.animalsStarve && !(((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping())) {
                        if (starvationTimer >= AnimalCareConfig.starvationTimer) {
                            hurt(damageSources().starve(), 4.0F);
                            starvationTimer = 0;
                        }
                        starvationTimer++;
                    }
                } else if (fedTicks == 0 || wateredTicks == 0) {
                    addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.WEAKNESS, 2, 0, false, false));
                }
            }
        }
    }

    @Override protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 5.0F, 2.0F));
        goalSelector.addGoal(4, new FerretWaterGoal());
        goalSelector.addGoal(5, new NestForageGoal());
        goalSelector.addGoal(6, new TroughForageGoal());
        goalSelector.addGoal(6, new EatGrassGoal());
        goalSelector.addGoal(7, new RandomStrollGoal(this, 0.8));
        goalSelector.addGoal(5, new TemptGoal(this, 1.2, Ingredient.of(FERRET_FOODS), false));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 6.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this, Silverfish.class, true) {
            @Override public boolean canUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canUse(); }
            @Override public boolean canContinueToUse() { return AnimalCareConfig.animalsCanAttackOthers && super.canContinueToUse(); }
        });
        targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(this,
                Chicken.class, 10, false, false,
                chicken -> chicken instanceof Chicken chick && chick.isBaby()) {
            @Override public boolean canUse() {
                return canAttackChicks() && super.canUse();
            }
            @Override public boolean canContinueToUse() {
                return canAttackChicks() && getTarget() instanceof Chicken chick && chick.isBaby()
                        && super.canContinueToUse();
            }
        });
    }

    private boolean canAttackChicks() {
        return AnimalCareConfig.animalsCanAttackOthers && !isTame() && !isOrderedToSit() && !isSleeping()
                && !((AnimalSleepState) (Object) this).animaniax$isSleeping();
    }

    @Override public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean sleeping = ((AnimalSleepState) (Object) this).animaniax$isSleeping() || isSleeping();
        if (sleeping && (isFood(stack)
                || stack.is(net.minecraft.world.item.Items.WATER_BUCKET)))
            return InteractionResult.sidedSuccess(level().isClientSide);
        boolean waterContainer = !sleeping && (level().isClientSide
                ? stack.is(net.minecraft.world.item.Items.WATER_BUCKET)
                : giveWaterBucket(player, hand));
        if (waterContainer) {
            if (!level().isClientSide) {
                careInteracted = true;
                wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("Ferret drank water."), true);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isFood(stack)) {
            if (!level().isClientSide) {
                if (!isTame()) tame(player);
                careInteracted = true;
                fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                ItemStack feedItem = stack.copy();
                if (!player.getAbilities().instabuild) stack.shrink(1);
                if (player instanceof ServerPlayer serverPlayer) FeedAnimalTrigger.INSTANCE.trigger(serverPlayer, this, feedItem);
                if (isOwnedBy(player)) setOrderedToSit(false);
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (isTame() && isOwnedBy(player) && stack.isEmpty()) {
            if (!level().isClientSide) {
                setOrderedToSit(!isOrderedToSit());
                careInteracted = true;
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    private boolean giveWaterBucket(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.is(net.minecraft.world.item.Items.WATER_BUCKET)) return false;
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            ItemStack bucket = new ItemStack(net.minecraft.world.item.Items.BUCKET);
            if (stack.isEmpty()) player.setItemInHand(hand, bucket);
            else if (!player.getInventory().add(bucket)) player.drop(bucket, false);
        }
        return true;
    }

    @Override public boolean isFood(ItemStack stack) {
        return stack.is(FERRET_FOODS);
    }

    @Override public void addAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("FedTicks", fedTicks);
        tag.putInt("WateredTicks", wateredTicks);
        tag.putInt("PetStarvationTimer", starvationTimer);
        tag.putInt("NestForageCooldown", nestForageCooldown);
        tag.putBoolean("CareInteracted", careInteracted);
    }

    @Override public void readAdditionalSaveData(net.minecraft.nbt.CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        fedTicks = tag.contains("FedTicks")
                ? Math.max(0, Math.min(AnimalCareConfig.feedTimer + 99, tag.getInt("FedTicks")))
                : AnimalCareConfig.feedTimer + getRandom().nextInt(100);
        wateredTicks = tag.contains("WateredTicks")
                ? Math.max(0, Math.min((int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + 199), tag.getInt("WateredTicks")))
                : (int) Math.min(Integer.MAX_VALUE, (long) AnimalCareConfig.waterTimer * 2 + getRandom().nextInt(200));
        starvationTimer = Math.max(0, Math.min(AnimalCareConfig.starvationTimer, tag.getInt("PetStarvationTimer")));
        nestForageCooldown = Math.max(0, Math.min(12000, tag.getInt("NestForageCooldown")));
        careInteracted = tag.contains("CareInteracted") ? tag.getBoolean("CareInteracted") : isTame();
    }

    @Override public boolean canFallInLove() { return false; }
    @Override public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob mate) { return null; }

    private final class FerretWaterGoal extends Goal {
        private BlockPos approach;
        private BlockPos source;
        private int nextScan;

        FerretWaterGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!(!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) || wateredTicks > 0 || isOrderedToSit() || tickCount < nextScan) return false;
            approach = source = null;
            BlockPos origin = FerretEntity.this.blockPosition();
            int range = AnimalCareConfig.aiBlockSearchRange;
            BlockPos bestSource = null;
            BlockPos bestApproach = null;
            double bestDist = Double.MAX_VALUE;
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        if (dx * dx + dz * dz > range * range) continue;
                        BlockPos candidatePos = origin.offset(dx, dy, dz);
                        if (!FerretEntity.this.level().hasChunkAt(candidatePos)) continue;
                        boolean trough = level().getBlockState(candidatePos).is(TroughContent.TROUGH)
                                && level().getBlockState(candidatePos).getValue(TroughBlock.CONTENT) == 1
                                && level().getBlockState(candidatePos).getValue(TroughBlock.LEVEL) > 0;
                        if (!trough && (!level().getFluidState(candidatePos).is(Fluids.WATER)
                                || level().getBiome(candidatePos).is(BiomeTags.IS_OCEAN)
                                || level().getBiome(candidatePos).is(BiomeTags.IS_BEACH))) continue;
                        boolean inReach = distanceToSqr(candidatePos.getX() + 0.5, candidatePos.getY(), candidatePos.getZ() + 0.5) <= 2.25;
                        for (Direction dir : Direction.Plane.HORIZONTAL) {
                            BlockPos candidate = trough ? candidatePos.relative(dir) : candidatePos.relative(dir).above();
                            if (!level().hasChunkAt(candidate) || !level().getBlockState(candidate).isAir()
                                    || !level().getBlockState(candidate.below()).isFaceSturdy(level(), candidate.below(), Direction.UP)) continue;
                            if (!inReach) {
                                var path = getNavigation().createPath(candidate, 0);
                                if (path == null || !path.canReach()) continue;
                            }
                            double distance = distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                            if (distance < bestDist) { bestDist = distance; bestApproach = candidate; bestSource = candidatePos; }
                        }
                    }
                }
            }
            source = bestSource;
            approach = bestApproach;
            nextScan = tickCount + 40;
            return approach != null;
        }

        @Override public boolean canContinueToUse() { return approach != null && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && wateredTicks == 0 && !isOrderedToSit(); }
        @Override public void start() { getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0); }
        @Override public void tick() {
            if (distanceToSqr(source.getX() + 0.5, source.getY(), source.getZ() + 0.5) <= 2.25) {
                if (level().getFluidState(source).is(Fluids.WATER)
                        || TroughBlock.consumeWater(level(), source, getType()))
                    wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                getNavigation().stop();
                approach = source = null;
            } else if (getNavigation().isDone()) { approach = source = null; nextScan = tickCount + 100; }
        }
        @Override public void stop() { getNavigation().stop(); approach = source = null; }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    private final class NestForageGoal extends Goal {
        private BlockPos nest;
        private int nextSearch;

        NestForageGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!canForageNest() || isOrderedToSit() || tickCount < nextSearch) return false;
            nest = findNest();
            nextSearch = tickCount + 100;
            return nest != null;
        }

        @Override public boolean canContinueToUse() {
            return nest != null && canForageNest() && !isOrderedToSit() && level().getBlockState(nest).is(NestContent.NEST)
                    && level().getBlockState(nest).getValue(NestBlock.EGGS) > 0;
        }

        @Override public void start() { moveToNest(); }
        @Override public void tick() {
            if (nest == null) return;
            if (distanceToSqr(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5) <= 2.25) {
                BlockState state = level().getBlockState(nest);
                if (state.is(NestContent.NEST) && state.getValue(NestBlock.EGGS) > 0
                        && level().getBlockEntity(nest) instanceof NestBlockEntity blockEntity) {
                    int eggs = state.getValue(NestBlock.EGGS);
                    blockEntity.syncEggMetadata(eggs);
                    blockEntity.removeEggs(1);
                    level().setBlock(nest, state.setValue(NestBlock.EGGS, eggs - 1), 3);
                    nestForageCooldown = 12000;
                    if ((!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI))) {
                        fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                        wateredTicks = AnimalCareConfig.waterTimer + getRandom().nextInt(100);
                    }
                }
                getNavigation().stop();
                nest = null;
            } else if (getNavigation().isDone()) moveToNest();
        }
        @Override public void stop() { getNavigation().stop(); nest = null; }

        private BlockPos findNest() {
            BlockPos origin = blockPosition();
            BlockPos nearest = null;
            double distance = Double.MAX_VALUE;
            for (int y = -2; y <= 2; y++) for (int x = -12; x <= 12; x++) for (int z = -12; z <= 12; z++) {
                BlockPos pos = origin.offset(x, y, z);
                if (x * x + z * z > 144 || !level().hasChunkAt(pos)) continue;
                BlockState state = level().getBlockState(pos);
                if (!state.is(NestContent.NEST) || state.getValue(NestBlock.EGGS) == 0) continue;
                double d = distanceToSqr(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                if (d < distance) { nearest = pos; distance = d; }
            }
            return nearest;
        }
        private void moveToNest() {
            if (nest != null) getNavigation().moveTo(nest.getX() + 0.5, nest.getY(), nest.getZ() + 0.5, 1.0);
        }
    }

    private boolean canForageNest() {
        return (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) ? fedTicks == 0 : nestForageCooldown == 0;
    }

    private final class TroughForageGoal extends Goal {
        private BlockPos trough;
        private BlockPos approach;
        private int nextSearch;

        TroughForageGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK)); }

        @Override public boolean canUse() {
            if (!(!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) || fedTicks > 0 || isOrderedToSit() || tickCount < nextSearch) return false;
            trough = approach = null;
            BlockPos origin = FerretEntity.this.blockPosition();
            int range = AnimalCareConfig.aiBlockSearchRange;
            BlockPos bestSource = null;
            BlockPos bestApproach = null;
            double bestDist = Double.MAX_VALUE;
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        if (dx * dx + dz * dz > range * range) continue;
                        BlockPos candidatePos = origin.offset(dx, dy, dz);
                        if (!FerretEntity.this.level().hasChunkAt(candidatePos)) continue;
                        if (!level().getBlockState(candidatePos).is(TroughContent.TROUGH)
                                || !(level().getBlockEntity(candidatePos) instanceof TroughBlockEntity entity)
                                || !entity.canFeed(getType())) continue;
                        boolean inReach = distanceToSqr(candidatePos.getX() + 0.5, candidatePos.getY(), candidatePos.getZ() + 0.5) <= 2.25;
                        for (Direction dir : Direction.Plane.HORIZONTAL) {
                            BlockPos candidate = candidatePos.relative(dir);
                            if (!level().hasChunkAt(candidate) || !level().getBlockState(candidate).isAir()
                                    || !level().getBlockState(candidate.below()).isFaceSturdy(level(), candidate.below(), Direction.UP)) continue;
                            if (!inReach) {
                                var path = getNavigation().createPath(candidate, 0);
                                if (path == null || !path.canReach()) continue;
                            }
                            double distance = distanceToSqr(candidate.getX() + 0.5, candidate.getY(), candidate.getZ() + 0.5);
                            if (distance < bestDist) { bestDist = distance; bestApproach = candidate; bestSource = candidatePos; }
                        }
                    }
                }
            }
            trough = bestSource;
            approach = bestApproach;
            nextSearch = tickCount + 40;
            return trough != null;
        }

        @Override public boolean canContinueToUse() {
            return trough != null && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && fedTicks == 0 && !isOrderedToSit()
                    && level().getBlockState(trough).is(TroughContent.TROUGH)
                    && level().getBlockEntity(trough) instanceof TroughBlockEntity entity
                    && entity.canFeed(getType());
        }

        @Override public void start() { moveToTrough(); }
        @Override public void tick() {
            if (trough == null) return;
            if (distanceToSqr(trough.getX() + 0.5, trough.getY(), trough.getZ() + 0.5) <= 2.25) {
                if (TroughBlock.consumeFeed(level(), trough, getType()))
                    fedTicks = AnimalCareConfig.feedTimer + getRandom().nextInt(100);
                getNavigation().stop();
                trough = approach = null;
            } else if (getNavigation().isDone()) moveToTrough();
        }
        @Override public void stop() { getNavigation().stop(); trough = approach = null; }

        private void moveToTrough() {
            if (approach != null) getNavigation().moveTo(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5, 1.0);
        }
    }

    private final class EatGrassGoal extends Goal {
        private int eatTimer;
        private boolean ate;

        EatGrassGoal() { setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP)); }

        @Override public boolean canUse() {
            if (!(!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) || fedTicks > 0 || isOrderedToSit()
                    || isSleeping()
                    || ((AnimalSleepState) (Object) FerretEntity.this).animaniax$isSleeping()
                    || getRandom().nextInt(isBaby() ? 50 : 150) != 0) return false;
            BlockPos pos = blockPosition();
            return level().getBlockState(pos).is(Blocks.SHORT_GRASS)
                    || level().getBlockState(pos.below()).is(Blocks.GRASS_BLOCK)
                    || level().getBlockState(pos.below()).is(Blocks.DIRT);
        }

        @Override public boolean canContinueToUse() {
            return eatTimer > 0 && (!AnimalCareConfig.ambianceMode && (careInteracted || !AnimalCareConfig.requireAnimalInteractionForAI)) && fedTicks == 0 && !isOrderedToSit()
                    && !isSleeping()
                    && !((AnimalSleepState) (Object) FerretEntity.this).animaniax$isSleeping();
        }

        @Override public boolean requiresUpdateEveryTick() { return true; }

        @Override public void start() {
            eatTimer = 80;
            ate = false;
            getNavigation().stop();
            level().broadcastEntityEvent(FerretEntity.this, (byte) 10);
        }

        @Override public void tick() {
            eatTimer = Math.max(0, eatTimer - 1);
            if (eatTimer != 4 || ate) return;
            ate = true;
            BlockPos pos = blockPosition();
            if (level().getBlockState(pos).is(Blocks.SHORT_GRASS))
                level().destroyBlock(pos, false);
            if (isBaby()) ageUp(60);
        }

        @Override public void stop() { eatTimer = 0; ate = false; }
    }

}
