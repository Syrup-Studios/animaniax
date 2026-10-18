package net.syrupstudios.animaniax.entity;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.pathfinder.Path;

public final class ProviderSearch {
    public record Candidate(BlockPos provider, BlockPos approach) {}

    private ProviderSearch() {}

    public static Candidate findNearest(Mob mob, BlockPos origin, int range, BiFunction<BlockPos, Predicate<BlockPos>, Candidate> candidateAt, ToDoubleFunction<BlockPos> distance) {
        Predicate<BlockPos> canReach = approach -> {
            Path path = mob.getNavigation().createPath(approach, 0);
            return path != null && path.canReach();
        };
        for (int radius = 0; radius < range; radius++) {
            Candidate nearest = null;
            double nearestDistance = Double.MAX_VALUE;
            for (int y = -range / 2 - 1; y <= range / 2 - 1; y++) {
                for (int x = -radius; x <= radius; x++) {
                    Candidate candidate = find(mob, origin.offset(x, y, -radius), candidateAt, canReach);
                    if (candidate != null && distance.applyAsDouble(candidate.approach()) < nearestDistance) {
                        nearest = candidate;
                        nearestDistance = distance.applyAsDouble(candidate.approach());
                    }
                    if (radius > 0) {
                        candidate = find(mob, origin.offset(x, y, radius), candidateAt, canReach);
                        if (candidate != null && distance.applyAsDouble(candidate.approach()) < nearestDistance) {
                            nearest = candidate;
                            nearestDistance = distance.applyAsDouble(candidate.approach());
                        }
                    }
                }
                for (int z = -radius + 1; z < radius; z++) {
                    for (int x : new int[]{-radius, radius}) {
                        Candidate candidate = find(mob, origin.offset(x, y, z), candidateAt, canReach);
                        if (candidate != null && distance.applyAsDouble(candidate.approach()) < nearestDistance) {
                            nearest = candidate;
                            nearestDistance = distance.applyAsDouble(candidate.approach());
                        }
                    }
                }
            }
            if (nearest != null) return nearest;
        }
        return null;
    }

    private static Candidate find(Mob mob, BlockPos pos, BiFunction<BlockPos, Predicate<BlockPos>, Candidate> candidateAt, Predicate<BlockPos> canReach) {
        return mob.level().hasChunkAt(pos) ? candidateAt.apply(pos, canReach) : null;
    }
}
