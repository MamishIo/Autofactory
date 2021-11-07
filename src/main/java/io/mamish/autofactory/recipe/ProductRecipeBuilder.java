package io.mamish.autofactory.recipe;

import io.mamish.autofactory.model.ProductAmount;
import io.mamish.autofactory.model.ProductRecipe;

import java.util.List;
import java.util.Objects;

public class ProductRecipeBuilder {

    private String name;
    private List<ProductAmount> ingredients;

    public void setName(String name) {
        this.name = name;
    }

    public void setIngredients(List<ProductAmount> ingredients) {
        this.ingredients = ingredients;
    }

    public ProductRecipe build(){
        Objects.requireNonNull(name, "no name specified");
        Objects.requireNonNull(ingredients, "no ingredients specified");
        return new ProductRecipe(name, ingredients);
    }
}
