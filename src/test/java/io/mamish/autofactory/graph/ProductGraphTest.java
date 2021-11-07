package io.mamish.autofactory.graph;

import io.mamish.autofactory.Constants;
import io.mamish.autofactory.model.FactoryRequest;
import io.mamish.autofactory.model.ProductAmount;
import io.mamish.autofactory.model.ProductRecipe;
import io.mamish.autofactory.recipe.RecipeSet;
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

    private static final FactoryRequest DESIRED_FACTORY = new FactoryRequest(List.of(
            new ProductAmount(PRODUCT_A, 10),
            new ProductAmount(PRODUCT_B, 5)
    ));

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
    public void generatesCorrectProductRequired() {
        ProductGraph graph = new ProductGraph(RECIPE_SET, DESIRED_FACTORY);

        assertEquals(10, graph.getProductRequired(Constants.FACTORY_OUTPUT_PRODUCT_NAME, PRODUCT_A));
        assertEquals(5, graph.getProductRequired(Constants.FACTORY_OUTPUT_PRODUCT_NAME, PRODUCT_B));
        assertEquals(10, graph.getProductRequired(PRODUCT_A, PRODUCT_B));
        assertEquals(20, graph.getProductRequired(PRODUCT_A, PRODUCT_C));
        assertEquals(45, graph.getProductRequired(PRODUCT_B, PRODUCT_D));
    }

    @Test
    public void unconnectedNodesReturnZeroWeight() {
        ProductGraph graph = new ProductGraph(RECIPE_SET, DESIRED_FACTORY);

        assertEquals(0, graph.getProductRequired(PRODUCT_C, PRODUCT_D));
    }

    @Test
    public void generatesCorrectProductTotal() {
        ProductGraph graph = new ProductGraph(RECIPE_SET, DESIRED_FACTORY);

        assertEquals(15, graph.getTotalProductRequired(PRODUCT_B));
    }

}
