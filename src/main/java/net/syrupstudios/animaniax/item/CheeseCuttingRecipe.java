package net.syrupstudios.animaniax.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.syrupstudios.animaniax.block.CheeseMoldContent;

public final class CheeseCuttingRecipe extends CustomRecipe {
    public enum Kind { FRIESIAN, HOLSTEIN, JERSEY, GOAT, SHEEP }

    public static final RecipeSerializer<CheeseCuttingRecipe> FRIESIAN_SERIALIZER =
            serializer(Kind.FRIESIAN);
    public static final RecipeSerializer<CheeseCuttingRecipe> HOLSTEIN_SERIALIZER =
            serializer(Kind.HOLSTEIN);
    public static final RecipeSerializer<CheeseCuttingRecipe> JERSEY_SERIALIZER =
            serializer(Kind.JERSEY);
    public static final RecipeSerializer<CheeseCuttingRecipe> GOAT_SERIALIZER =
            serializer(Kind.GOAT);
    public static final RecipeSerializer<CheeseCuttingRecipe> SHEEP_SERIALIZER =
            serializer(Kind.SHEEP);

    private static final String GROUP = "cheese_cutting";
    private final Kind kind;

    private CheeseCuttingRecipe(CraftingBookCategory category, Kind kind) {
        super(category);
        this.kind = kind;
    }

    private static RecipeSerializer<CheeseCuttingRecipe> serializer(Kind kind) {
        return new SimpleCraftingRecipeSerializer<>(category -> new CheeseCuttingRecipe(category, kind));
    }

    @Override public boolean isSpecial() { return false; }

    @Override public String getGroup() { return GROUP; }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int knives = 0;
        int wheels = 0;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) continue;
            if (stack.is(CheeseWedgeContent.KNIFE) && stack.getDamageValue() < stack.getMaxDamage()) knives++;
            else if (stack.is(wheelItem())) wheels++;
            else return false;
        }
        return knives == 1 && wheels == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(wedgeItem(), 4);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(wedgeItem(), 4);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(CheeseWedgeContent.KNIFE));
        ingredients.add(Ingredient.of(wheelItem()));
        return ingredients;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return switch (kind) {
            case FRIESIAN -> FRIESIAN_SERIALIZER;
            case HOLSTEIN -> HOLSTEIN_SERIALIZER;
            case JERSEY -> JERSEY_SERIALIZER;
            case GOAT -> GOAT_SERIALIZER;
            case SHEEP -> SHEEP_SERIALIZER;
        };
    }

    private Item wheelItem() {
        return switch (kind) {
            case FRIESIAN -> CheeseMoldContent.FRIESIAN_CHEESE;
            case HOLSTEIN -> CheeseMoldContent.HOLSTEIN_CHEESE;
            case JERSEY -> CheeseMoldContent.JERSEY_CHEESE;
            case GOAT -> CheeseMoldContent.GOAT_CHEESE;
            case SHEEP -> CheeseMoldContent.SHEEP_CHEESE;
        };
    }

    private Item wedgeItem() {
        return switch (kind) {
            case FRIESIAN -> CheeseWedgeContent.FRIESIAN_WEDGE;
            case HOLSTEIN -> CheeseWedgeContent.HOLSTEIN_WEDGE;
            case JERSEY -> CheeseWedgeContent.JERSEY_WEDGE;
            case GOAT -> CheeseWedgeContent.GOAT_WEDGE;
            case SHEEP -> CheeseWedgeContent.SHEEP_WEDGE;
        };
    }
}
