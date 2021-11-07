package io.mamish.autofactory.recipe;

import io.mamish.autofactory.model.ProductRecipe;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RecipeSet {

    private final Map<String, ProductRecipe> namesToRecipes = new HashMap<>();

    public void putRecipe(ProductRecipe recipe) {
        namesToRecipes.put(recipe.productName(), recipe);
    }

    public ProductRecipe getRecipe(String name) throws NoSuchElementException {
        return Optional.ofNullable(namesToRecipes.get(name)).orElseThrow(NoSuchElementException::new);
    }
}
