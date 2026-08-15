package net.syrupstudios.animaniax.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Animal.class)
abstract class CowSpawnPlacementMixin {
    private static final TagKey<Biome> ANIMANIAX_COW_SPAWN_BIOMES = TagKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("animaniax", "cow_spawns"));

    @Redirect(method = "checkAnimalSpawnRules", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;is(Lnet/minecraft/tags/TagKey;)Z"))
    private static boolean animaniax$allowSuitableCowTerrain(BlockState state, TagKey<Block> spawnableBlocks,
            EntityType<? extends Animal> type, LevelAccessor level, MobSpawnType reason,
            BlockPos pos, RandomSource random) {
        if (state.is(spawnableBlocks)) return true;
        if (type != EntityType.COW || !level.getBiome(pos).is(ANIMANIAX_COW_SPAWN_BIOMES)) return false;
        String biome = level.getBiome(pos).unwrapKey().map(key -> key.location().toString()).orElse("");
        return switch (biome) {
            case "minecraft:badlands", "minecraft:eroded_badlands", "minecraft:wooded_badlands" ->
                    state.is(BlockTags.BADLANDS_TERRACOTTA) || state.is(Blocks.RED_SAND);
            case "minecraft:mangrove_swamp" -> state.is(Blocks.MUD) || state.is(Blocks.MUDDY_MANGROVE_ROOTS);
            case "minecraft:frozen_peaks", "minecraft:jagged_peaks", "minecraft:stony_peaks" ->
                    state.is(BlockTags.BASE_STONE_OVERWORLD) || state.is(Blocks.SNOW_BLOCK) || state.is(BlockTags.ICE);
            default -> false;
        };
    }
}
