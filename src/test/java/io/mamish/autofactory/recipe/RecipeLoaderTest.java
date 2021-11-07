package io.mamish.autofactory.recipe;

import io.mamish.autofactory.model.ProductAmount;
import io.mamish.autofactory.model.ProductRecipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RecipeLoaderTest {

    @Test
    public void throwsErrorOnUnknownVersion() {
        assertThrows(IllegalArgumentException.class, () -> new RecipeLoader("1.0.fake"));
    }

    @Test
    public void loadsRecipesWithAllIngredientTypes() {
        RecipeSet set = new RecipeLoader("1.0.test").getLoadedRecipeSet();

        ProductRecipe plate = set.getRecipe("iron-plate");
        assertIterableEquals(
                List.of(new ProductAmount("iron-ore", 1)),
                plate.ingredients()
        );
        ProductRecipe sulfuricAcid = set.getRecipe("sulfuric-acid");
        assertIterableEquals(
                List.of(
                        new ProductAmount("sulfur", 5),
                        new ProductAmount("iron-plate", 1),
                        new ProductAmount("water", 100)
                ),
                sulfuricAcid.ingredients()
        );
    }

}
