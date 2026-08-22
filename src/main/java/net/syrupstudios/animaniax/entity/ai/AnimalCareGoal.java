package net.syrupstudios.animaniax.entity.ai;

import java.util.Comparator;
import java.util.EnumSet;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.tags.BiomeTags;
import net.syrupstudios.animaniax.block.MudBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.syrupstudios.animaniax.entity.AnimalCareState;
import net.syrupstudios.animaniax.entity.AnimalProfile;
import net.syrupstudios.animaniax.entity.AnimalProfile.Capability;
import net.syrupstudios.animaniax.entity.AnimalSleepState;
import net.syrupstudios.animaniax.block.TroughBlock;
import net.syrupstudios.animaniax.config.AnimalCareConfig;
import net.syrupstudios.animaniax.block.TroughContent;
import net.syrupstudios.animaniax.block.SeedPileContent;
import net.syrupstudios.animaniax.item.TruffleContent;

public final class AnimalCareGoal extends Goal {
    public enum Need { WATER, GRASS, SEEDS, MUD, TRUFFLE }

    private final Mob mob;
    private final AnimalCareState care;
    private final Need need;
    private BlockPos target;
    private BlockPos waterSource;
    private BlockPos foodBlock;
    private ItemEntity seedItem;
    private ItemEntity truffleItem;
    private boolean finished;
    private int scanAtTick;
    private int careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
    private int mudChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
    private int mudContactTicks;

    public AnimalCareGoal(Mob mob, Need need) {
        this.mob = mob;
        this.care = (AnimalCareState) mob;
        this.need = need;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (need == Need.MUD) {
            if (!animaniax$needsCare() || mob.isVehicle() || mob.isPassenger() || mob.isSleeping()
                    || ((AnimalSleepState) mob).animaniax$isSleeping()) {
                mudChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
                return false;
            }
            if (mob.tickCount < scanAtTick) return false;
            if (mudChecksUntilSearch-- > 0) return false;
            mudChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
            if (mob.getRandom().nextInt(100) == 0) {
                if (mob instanceof PathfinderMob pathfinderMob) {
                    var wander = LandRandomPos.getPos(pathfinderMob, 20, 4);
                    if (wander != null) mob.getNavigation().moveTo(wander.x, wander.y, wander.z, 1.0);
                }
                return false;
            }
            target = animaniax$findMud();
            return target != null && mob.getRandom().nextInt(200) != 0;
        }
        if (need == Need.WATER || need == Need.GRASS || need == Need.SEEDS) {
            boolean blocked = !animaniax$needsCare() || mob.isVehicle() || mob.isPassenger()
                    || mob.isSleeping() || ((AnimalSleepState) mob).animaniax$isSleeping()
                    || AnimalCareConfig.requireAnimalInteractionForAI && !care.animaniax$wasInteractedWith();
            if (blocked) {
                careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
                return false;
            }
            if (mob.tickCount < scanAtTick) return false;
            if (careChecksUntilSearch > 0) {
                careChecksUntilSearch--;
                return false;
            }
            if (mob.getRandom().nextInt(3) != 0) return false;
            careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
            target = switch (need) {
                case WATER -> animaniax$findWaterApproach();
                case GRASS -> animaniax$findGrass();
                case SEEDS -> animaniax$findSeeds();
                    default -> throw new IllegalStateException();
            };
            if (need != Need.WATER) scanAtTick = mob.tickCount + 40;
            return target != null;
        }
        if (!animaniax$needsCare() || mob.isVehicle() || mob.isPassenger() || mob.isSleeping() || ((AnimalSleepState) mob).animaniax$isSleeping()) return false;
        if (mob.tickCount < scanAtTick) return false;
        target = switch (need) {
            case WATER -> animaniax$findWaterApproach();
            case GRASS -> animaniax$findGrass();
            case SEEDS -> animaniax$findSeeds();
            case MUD -> animaniax$findMud();
            case TRUFFLE -> animaniax$findTruffle();
        };
        scanAtTick = mob.tickCount + 40;
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return !finished && animaniax$needsCare() && target != null
                && !mob.isVehicle() && !mob.isPassenger() && !mob.isSleeping()
                && !((AnimalSleepState) mob).animaniax$isSleeping();
    }

    @Override
    public void start() {
        mudContactTicks = 0;
        boolean inReach = need == Need.GRASS
                && mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) <= 2.25;
        if (inReach) mob.getNavigation().stop();
        finished = !inReach && !(need == Need.MUD || need == Need.GRASS
                ? mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 0, 1.0)
                : mob.getNavigation().moveTo(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0));
        if (finished) scanAtTick = mob.tickCount + 100;
    }

    @Override
    public void tick() {
        if (target == null) return;
        if (mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) <= 2.25
                && (need != Need.MUD || mob.onGround() && (mob.blockPosition().equals(target)
                        || mob.blockPosition().equals(target.below())))) {
            if (need == Need.WATER) {
                if (waterSource != null && animaniax$drinkFrom(waterSource)) {
                    care.animaniax$drink();
                }
                careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
            } else if (need == Need.GRASS && animaniax$eatGrass()) {
                if (foodBlock != null && mob.level().getBlockState(foodBlock).is(TroughContent.TROUGH)
                        && mob.level().getBlockState(foodBlock).getValue(TroughBlock.CONTENT) == 3) care.animaniax$eatSlop();
                else care.animaniax$graze();
                careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
            } else if (need == Need.SEEDS && animaniax$eatSeed()) {
                care.animaniax$graze();
                careChecksUntilSearch = AnimalCareConfig.ticksBetweenAIFirings;
            } else if (need == Need.TRUFFLE && animaniax$eatTruffle()) {
                care.animaniax$graze();
            } else if (need == Need.MUD) {
                care.animaniax$useMud();
            }
            finished = true;
        } else if (mob.getNavigation().isDone()) {
            if (need == Need.MUD && mob.blockPosition().getY() == target.getY()
                    && mob.distanceToSqr(target.getX() + 0.5, target.getY(), target.getZ() + 0.5) <= 2.25
                    && mudContactTicks < 40) {
                mudContactTicks++;
                mob.getMoveControl().setWantedPosition(target.getX() + 0.5, target.getY(), target.getZ() + 0.5, 1.0);
                return;
            }
            finished = true;
            scanAtTick = mob.tickCount + 100;
        }
    }

    @Override
    public void stop() {
        mob.getNavigation().stop();
        target = null;
        waterSource = null;
        foodBlock = null;
        seedItem = null;
        truffleItem = null;
        mudContactTicks = 0;
        finished = false;
    }

    private boolean animaniax$needsCare() {
        return switch (need) {
            case WATER -> care.animaniax$needsWater();
            case GRASS -> care.animaniax$needsGraze();
            case SEEDS -> care.animaniax$needsSeeds();
            case MUD -> mob.level().isDay() && care.animaniax$needsMud();
            case TRUFFLE -> care.animaniax$needsGraze() && AnimalProfile.has(mob.getType(), Capability.PIG_ENRICHMENT_GOALS);
        };
    }

    private boolean animaniax$drinkFrom(BlockPos source) {
        if (mob.level().getBlockState(source).is(TroughContent.TROUGH))
            return TroughBlock.consumeWater(mob.level(), source, mob.getType());
        if (!mob.level().getBlockState(source).is(Blocks.WATER) || animaniax$isOceanOrBeach(source)) return false;
        if (AnimalCareConfig.waterRemovedAfterDrinking
                && AnimalProfile.has(mob.getType(), Capability.REMOVE_NATURAL_WATER))
            mob.level().setBlock(source, Blocks.AIR.defaultBlockState(), 3);
        return true;
    }

    private boolean animaniax$isOceanOrBeach(BlockPos pos) {
        return mob.level().getBiome(pos).is(BiomeTags.IS_OCEAN)
                || mob.level().getBiome(pos).is(BiomeTags.IS_BEACH);
    }

    private BlockPos animaniax$findMud() {
        BlockPos origin = mob.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dy = -2; dy < 2; dy++) {
            for (int dx = -10; dx < 10; dx++) {
                for (int dz = -10; dz < 10; dz++) {
                    BlockPos mud = origin.offset(dx, dy, dz);
                    BlockPos approach = mud.above();
                    if (!mob.level().hasChunkAt(approach) || !MudBlock.isMud(mob.level().getBlockState(mud))
                            || !mob.level().getBlockState(approach).getCollisionShape(mob.level(), approach).isEmpty()
                            || mob.level().getEntitiesOfClass(Pig.class,
                                    new AABB(mud.getX() - 2, mud.getY() - 2, mud.getZ() - 2,
                                            mud.getX() + 2, mud.getY() + 2, mud.getZ() + 2)).size() >= 2) continue;
                    double distance = mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5);
                    if (distance < nearestDistance) {
                        nearest = approach;
                        nearestDistance = distance;
                    }
                }
            }
        }
        return nearest;
    }

    private BlockPos animaniax$findWaterApproach() {
            BlockPos origin = mob.blockPosition();
            int range = AnimalCareConfig.aiBlockSearchRange;
            BlockPos bestSource = null;
            BlockPos bestApproach = null;
            double bestDist = Double.MAX_VALUE;
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        if (dx * dx + dz * dz > range * range) continue;
                        BlockPos candidatePos = origin.offset(dx, dy, dz);
                        if (!mob.level().hasChunkAt(candidatePos)) continue;
                        boolean trough = mob.level().getBlockState(candidatePos).is(TroughContent.TROUGH)
                                && mob.level().getBlockState(candidatePos).getValue(TroughBlock.CONTENT) == 1
                                && mob.level().getBlockState(candidatePos).getValue(TroughBlock.LEVEL) > 0;
                        boolean petBowl = false;
                        if (!trough && !petBowl && (!mob.level().getBlockState(candidatePos).is(Blocks.WATER)
                                || animaniax$isOceanOrBeach(candidatePos))) continue;
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            BlockPos approach = trough || petBowl ? candidatePos.relative(direction) : candidatePos.relative(direction).above();
                            if (!mob.level().hasChunkAt(approach) || !mob.level().getBlockState(approach).isAir()
                                    || !mob.level().getBlockState(approach.below()).isFaceSturdy(mob.level(), approach.below(), Direction.UP)) continue;
                            boolean inReach = mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5) <= 2.25;
                            if (!inReach) {
                                var path = mob.getNavigation().createPath(approach, 0);
                                if (path == null || !path.canReach()) continue;
                            }
                            double distance = mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5);
                            if (distance < bestDist) { bestDist = distance; bestApproach = approach; bestSource = candidatePos; }
                        }
                    }
                }
            }
        waterSource = bestSource;
        return bestApproach;
    }

    private BlockPos animaniax$findGrass() {
        BlockPos troughApproach = animaniax$findTroughFood();
        if (troughApproach != null) return troughApproach;
        if (!AnimalProfile.has(mob.getType(), Capability.NATURAL_GRAZING_GOAL)) return null;
        BlockPos origin = mob.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (int dy = -2; dy <= 2; dy++) {
            for (int dx = -6; dx <= 6; dx++) {
                for (int dz = -6; dz <= 6; dz++) {
                    BlockPos plant = origin.offset(dx, dy, dz);
                    if (!mob.level().hasChunkAt(plant) || !animaniax$isGrazeableGrass(plant)) continue;
                    BlockPos approach = mob.level().getBlockState(plant).is(Blocks.GRASS_BLOCK) ? plant.above() : plant;
                    if (!mob.level().getBlockState(approach).getCollisionShape(mob.level(), approach).isEmpty()
                            || !mob.level().getBlockState(approach.below()).isFaceSturdy(mob.level(), approach.below(), Direction.UP)) continue;
                    double distance = mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5);
                    if (distance < nearestDistance) {
                        nearest = approach;
                        nearestDistance = distance;
                        foodBlock = plant;
                    }
                }
            }
        }
        return nearest;
    }

    private BlockPos animaniax$findTroughFood() {
            BlockPos origin = mob.blockPosition();
            int range = AnimalCareConfig.aiBlockSearchRange;
            BlockPos bestSource = null;
            BlockPos bestApproach = null;
            double bestDist = Double.MAX_VALUE;
            for (int dy = -2; dy <= 2; dy++) {
                for (int dx = -range; dx <= range; dx++) {
                    for (int dz = -range; dz <= range; dz++) {
                        if (dx * dx + dz * dz > range * range) continue;
                        BlockPos candidatePos = origin.offset(dx, dy, dz);
                        if (!mob.level().hasChunkAt(candidatePos)) continue;
                        if (!mob.level().getBlockState(candidatePos).is(TroughContent.TROUGH)
                                || !(mob.level().getBlockEntity(candidatePos) instanceof net.syrupstudios.animaniax.block.TroughBlockEntity entity)
                                || !entity.canFeed(mob.getType())) continue;
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            BlockPos approach = candidatePos.relative(direction);
                            if (!mob.level().hasChunkAt(approach) || !mob.level().getBlockState(approach).isAir()
                                    || !mob.level().getBlockState(approach.below()).isFaceSturdy(mob.level(), approach.below(), Direction.UP)) continue;
                            if (mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5) > 2.25) {
                                var path = mob.getNavigation().createPath(approach, 0);
                                if (path == null || !path.canReach()) continue;
                            }
                            double distance = mob.distanceToSqr(approach.getX() + 0.5, approach.getY(), approach.getZ() + 0.5);
                            if (distance < bestDist) { bestDist = distance; bestApproach = approach; bestSource = candidatePos; }
                        }
                    }
                }
            }
        foodBlock = bestSource;
        return bestApproach;
    }

    private BlockPos animaniax$findSeeds() {
        BlockPos trough = animaniax$findTroughFood();
        if (trough != null) return trough;
        if (mob.level().isClientSide) return null;
        BlockPos origin = mob.blockPosition();
        BlockPos nearestPile = null;
        double nearestPileDistance = Double.MAX_VALUE;
        for (int dy = -2; dy <= 2; dy++) for (int dx = -6; dx <= 6; dx++) for (int dz = -6; dz <= 6; dz++) {
            BlockPos pile = origin.offset(dx, dy, dz);
            if (!mob.level().hasChunkAt(pile) || !animaniax$isSeedPile(mob.level().getBlockState(pile))) continue;
            double distance = mob.distanceToSqr(pile.getX() + 0.5, pile.getY(), pile.getZ() + 0.5);
            if (distance < nearestPileDistance) { nearestPile = pile; nearestPileDistance = distance; }
        }
        if (nearestPile != null) { foodBlock = nearestPile; return nearestPile; }
        seedItem = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(8), item ->
                item.isAlive() && !item.hasPickUpDelay() && mob.level().hasChunkAt(item.blockPosition()) && animaniax$isSeed(item.getItem()))
                .stream().min(Comparator.comparingDouble(mob::distanceToSqr)).orElse(null);
        return seedItem == null ? null : seedItem.blockPosition();
    }

    private BlockPos animaniax$findTruffle() {
        if (mob.level().isClientSide) return null;
        truffleItem = mob.level().getEntitiesOfClass(ItemEntity.class, mob.getBoundingBox().inflate(6), item ->
                item.isAlive() && !item.hasPickUpDelay() && item.getItem().is(TruffleContent.TRUFFLE))
                .stream().min(Comparator.comparingDouble(mob::distanceToSqr)).orElse(null);
        return truffleItem == null ? null : truffleItem.blockPosition();
    }

    private boolean animaniax$eatTruffle() {
        if (truffleItem == null || !truffleItem.isAlive() || truffleItem.hasPickUpDelay()
                || mob.distanceToSqr(truffleItem) > 2.25 || !mob.level().hasChunkAt(truffleItem.blockPosition())
                || !truffleItem.getItem().is(TruffleContent.TRUFFLE)) return false;
        ItemStack remainder = truffleItem.getItem().copy();
        remainder.shrink(1);
        if (remainder.isEmpty()) truffleItem.discard();
        else truffleItem.setItem(remainder);
        return true;
    }

    private boolean animaniax$eatSeed() {
        if (foodBlock != null && animaniax$isSeedPile(mob.level().getBlockState(foodBlock))) {
            mob.level().setBlock(foodBlock, Blocks.AIR.defaultBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
            return true;
        }
        if (foodBlock != null && mob.level().getBlockState(foodBlock).is(TroughContent.TROUGH))
            return TroughBlock.consumeFeed(mob.level(), foodBlock, mob.getType());
        if (seedItem == null || !seedItem.isAlive() || seedItem.hasPickUpDelay() || mob.distanceToSqr(seedItem) > 2.25
                || !mob.level().hasChunkAt(seedItem.blockPosition()) || !animaniax$isSeed(seedItem.getItem())) return false;
        ItemStack remainder = seedItem.getItem().copy();
        remainder.shrink(1);
        if (remainder.isEmpty()) seedItem.discard();
        else seedItem.setItem(remainder);
        return true;
    }

    private boolean animaniax$isSeedPile(net.minecraft.world.level.block.state.BlockState state) {
        return state.is(SeedPileContent.WHEAT) || state.is(SeedPileContent.PUMPKIN)
                || state.is(SeedPileContent.MELON) || state.is(SeedPileContent.BEETROOT);
    }

    private boolean animaniax$isSeed(ItemStack stack) {
        return stack.is(AnimalProfile.POULTRY_FOODS);
    }

    private boolean animaniax$eatGrass() {
        BlockPos plant = foodBlock;
        if (mob.level().getBlockState(plant).is(TroughContent.TROUGH)) {
            return TroughBlock.consumeFeed(mob.level(), plant, mob.getType());
        }
        if (!animaniax$isGrazeableGrass(plant)) return false;
        if (AnimalCareConfig.plantsRemovedAfterEating) {
            if (mob.level().getBlockState(plant).is(Blocks.GRASS_BLOCK)) {
                mob.level().setBlockAndUpdate(plant, Blocks.DIRT.defaultBlockState());
            } else if (mob.level().getBlockState(plant).hasProperty(net.minecraft.world.level.block.DoublePlantBlock.HALF)) {
                var state = mob.level().getBlockState(plant);
                DoubleBlockHalf half = state.getValue(net.minecraft.world.level.block.DoublePlantBlock.HALF);
                BlockPos otherHalf = half == DoubleBlockHalf.LOWER
                        ? plant.above() : plant.below();
                if (mob.level().hasChunkAt(otherHalf)
                        && mob.level().getBlockState(otherHalf).is(state.getBlock())
                        && mob.level().getBlockState(otherHalf).hasProperty(net.minecraft.world.level.block.DoublePlantBlock.HALF)
                        && mob.level().getBlockState(otherHalf).getValue(net.minecraft.world.level.block.DoublePlantBlock.HALF) != half) {
                    mob.level().destroyBlock(otherHalf, false);
                }
                mob.level().destroyBlock(plant, false);
            } else {
                mob.level().destroyBlock(plant, false);
            }
        }
        return true;
    }

    private boolean animaniax$isGrass(BlockPos pos) {
        return mob.level().getBlockState(pos).is(Blocks.GRASS_BLOCK)
                || mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS)
                || mob.level().getBlockState(pos).is(Blocks.TALL_GRASS)
                || mob.level().getBlockState(pos).is(Blocks.FERN)
                || mob.level().getBlockState(pos).is(Blocks.LARGE_FERN);
    }

    private boolean animaniax$isGrazeableGrass(BlockPos pos) {
        if (mob.getType() == EntityType.PIG) {
            return mob.level().getBlockState(pos).is(Blocks.SHORT_GRASS)
                    || mob.level().getBlockState(pos).is(Blocks.TALL_GRASS);
        }
        return animaniax$isGrass(pos);
    }
}
