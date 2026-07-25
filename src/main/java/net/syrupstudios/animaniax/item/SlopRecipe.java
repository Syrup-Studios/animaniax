package net.syrupstudios.animaniax.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class SlopRecipe extends CustomRecipe {
    public static final RecipeSerializer<SlopRecipe> SERIALIZER = new SimpleCraftingRecipeSerializer<>(SlopRecipe::new);
    private static final TagKey<Item> FOODS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "pig_slop_foods"));
    private static final TagKey<Item> MILK = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("animaniax", "milk_buckets"));

    public SlopRecipe(CraftingBookCategory category) { super(category); }

    @Override public boolean isSpecial() { return false; }

    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(FOODS));
        ingredients.add(Ingredient.of(FOODS));
        ingredients.add(Ingredient.of(MILK));
        return ingredients;
    }

    @Override public boolean matches(CraftingInput input, Level level) {
        int occupied = 0, foods = 0, milk = 0;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            occupied++;
            if (stack.is(FOODS)) foods++;
            if (stack.is(MILK)) milk++;
            if (!stack.is(FOODS) && !stack.is(MILK)) return false;
        }
        return occupied == 3 && foods >= 2 && milk >= 1;
    }

    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(SlopContent.SLOP_BUCKET);
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) { return new ItemStack(SlopContent.SLOP_BUCKET); }
    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) { return NonNullList.withSize(input.size(), ItemStack.EMPTY); }
    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 3; }
    @Override public RecipeSerializer<?> getSerializer() { return SERIALIZER; }
}
