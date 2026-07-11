package net.syrupstudios.animaniax.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;

public final class MilkConversionRecipe extends CustomRecipe {
    public static final RecipeSerializer<MilkConversionRecipe> SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(MilkConversionRecipe::new);

    public MilkConversionRecipe(CraftingBookCategory category) { super(category); }

    @Override public boolean isSpecial() { return false; }

    @Override public boolean matches(CraftingInput input, Level level) {
        int milkBuckets = 0;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (isAnimalMilk(stack)) milkBuckets++;
            else return false;
        }
        return milkBuckets == 1;
    }

    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(Items.MILK_BUCKET);
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.MILK_BUCKET);
    }

    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(AnimalMilkContent.SHEEP_MILK_BUCKET,
                AnimalMilkContent.GOAT_MILK_BUCKET,
                AnimalMilkContent.FRIESIAN_MILK_BUCKET,
                AnimalMilkContent.HOLSTEIN_MILK_BUCKET,
                AnimalMilkContent.JERSEY_MILK_BUCKET));
        return ingredients;
    }

    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 1; }

    @Override public RecipeSerializer<?> getSerializer() { return SERIALIZER; }

    private static boolean isAnimalMilk(ItemStack stack) {
        return stack.is(AnimalMilkContent.SHEEP_MILK_BUCKET)
                || stack.is(AnimalMilkContent.GOAT_MILK_BUCKET)
                || stack.is(AnimalMilkContent.FRIESIAN_MILK_BUCKET)
                || stack.is(AnimalMilkContent.HOLSTEIN_MILK_BUCKET)
                || stack.is(AnimalMilkContent.JERSEY_MILK_BUCKET);
    }
}
