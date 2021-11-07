package io.mamish.autofactory.graph;

import io.mamish.autofactory.Constants;
import io.mamish.autofactory.RecipeSet;
import io.mamish.autofactory.model.ProductAmount;
import io.mamish.autofactory.model.ProductRecipe;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProductGraphTest {

    private static final RecipeSet RECIPE_SET = new RecipeSet();

    private static final String PRODUCT_A = "a";
    private static final String PRODUCT_B = "b";
    private static final String PRODUCT_C = "c";
    private static final String PRODUCT_D = "d";

    @BeforeAll
    public static void setup() {
        RECIPE_SET.putRecipe(new ProductRecipe(PRODUCT_A, List.of(
                new ProductAmount(PRODUCT_B, 1),
                new ProductAmount(PRODUCT_C, 2)
        )));
        RECIPE_SET.putRecipe(new ProductRecipe(PRODUCT_B, List.of(
                new ProductAmount(PRODUCT_D, 3)
        )));
        RECIPE_SET.putRecipe(new ProductRecipe(PRODUCT_C, List.of()));
        RECIPE_SET.putRecipe(new ProductRecipe(PRODUCT_D, List.of()));
    }

    @Test
    public void generatesCorrectCumulativeWeights() {
        ProductGraph graph = new ProductGraph(RECIPE_SET, List.of(
                new ProductAmount(PRODUCT_A, 10),
                new ProductAmount(PRODUCT_B, 5)
        ));

        assertEquals(10, graph.getCumulativeWeight(Constants.FACTORY_OUTPUT_PRODUCT_NAME, PRODUCT_A));
        assertEquals(5, graph.getCumulativeWeight(Constants.FACTORY_OUTPUT_PRODUCT_NAME, PRODUCT_B));
        assertEquals(10, graph.getCumulativeWeight(PRODUCT_A, PRODUCT_B));
        assertEquals(20, graph.getCumulativeWeight(PRODUCT_A, PRODUCT_C));
        assertEquals(45, graph.getCumulativeWeight(PRODUCT_B, PRODUCT_D));
    }

}
