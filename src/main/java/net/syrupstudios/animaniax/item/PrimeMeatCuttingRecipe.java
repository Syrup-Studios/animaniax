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
import net.syrupstudios.animaniax.block.StrawContent;

public final class PrimeMeatCuttingRecipe extends CustomRecipe {
    public enum Kind { BEEF, PORK, STRAW }

    public static final RecipeSerializer<PrimeMeatCuttingRecipe> BEEF_SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(category -> new PrimeMeatCuttingRecipe(category, Kind.BEEF));
    public static final RecipeSerializer<PrimeMeatCuttingRecipe> PORK_SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(category -> new PrimeMeatCuttingRecipe(category, Kind.PORK));
    public static final RecipeSerializer<PrimeMeatCuttingRecipe> STRAW_SERIALIZER =
            new SimpleCraftingRecipeSerializer<>(category -> new PrimeMeatCuttingRecipe(category, Kind.STRAW));

    private final Kind kind;

    private PrimeMeatCuttingRecipe(CraftingBookCategory category, Kind kind) {
        super(category);
        this.kind = kind;
    }

    @Override public boolean isSpecial() { return false; }

    @Override public boolean matches(CraftingInput input, Level level) {
        int knives = 0;
        int ingredients = 0;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (stack.is(CheeseWedgeContent.KNIFE) && stack.getDamageValue() < stack.getMaxDamage()) knives++;
            else if (stack.is(ingredientItem())) ingredients++;
            else return false;
        }
        return knives == 1 && ingredients == 1;
    }

    @Override public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return result();
    }

    @Override public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result();
    }

    @Override public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(CheeseWedgeContent.KNIFE));
        ingredients.add(Ingredient.of(ingredientItem()));
        return ingredients;
    }

    @Override public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(CheeseWedgeContent.KNIFE)) {
                ItemStack knife = stack.copy();
                int damage = knife.getDamageValue() + 1;
                if (damage < knife.getMaxDamage()) {
                    knife.setDamageValue(damage);
                    remaining.set(i, knife);
                }
            }
        }
        return remaining;
    }

    @Override public boolean canCraftInDimensions(int width, int height) { return width * height >= 2; }

    @Override public RecipeSerializer<?> getSerializer() {
        return switch (kind) {
            case BEEF -> BEEF_SERIALIZER;
            case PORK -> PORK_SERIALIZER;
            case STRAW -> STRAW_SERIALIZER;
        };
    }

    private net.minecraft.world.item.Item ingredientItem() {
        return switch (kind) {
            case BEEF -> PrimeMeatContent.RAW_PRIME_BEEF;
            case PORK -> PrimeMeatContent.RAW_PRIME_PORK;
            case STRAW -> Items.WHEAT;
        };
    }

    private ItemStack result() {
        return switch (kind) {
            case BEEF -> new ItemStack(PrimeMeatContent.RAW_PRIME_STEAK, 4);
            case PORK -> new ItemStack(PrimeMeatContent.RAW_PRIME_BACON, 4);
            case STRAW -> new ItemStack(StrawContent.ITEM);
        };
    }
}
