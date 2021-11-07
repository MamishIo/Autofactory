package io.mamish.autofactory.graph;

import io.mamish.autofactory.Constants;
import io.mamish.autofactory.RecipeSet;
import io.mamish.autofactory.model.ProductAmount;
import io.mamish.autofactory.model.ProductRecipe;
import org.jgrapht.graph.DirectedAcyclicGraph;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ProductGraph {

    private final DirectedAcyclicGraph<ProductNode, ProductEdge> graph = new DirectedAcyclicGraph<>(ProductEdge.class);
    private final RecipeSet recipeSet;
    private final ProductNode outputNode;

    public ProductGraph(RecipeSet recipeSet, List<ProductAmount> desiredOutput) {
        this.recipeSet = recipeSet;
        ProductRecipe outputRecipe = new ProductRecipe(Constants.FACTORY_OUTPUT_PRODUCT_NAME, desiredOutput);
        this.outputNode = addProductNodesRecursive(outputRecipe);
        propagateCumulativeCost();
    }

    public double getCumulativeWeight(String fromProduct, String toProduct) {
        ProductEdge edge = graph.getEdge(new ProductNode(fromProduct), new ProductNode(toProduct));
        return Optional.ofNullable(edge).map(ProductEdge::getCumulativeCost).orElse(0d);
    }

    private ProductNode addProductNodesRecursive(ProductRecipe nodeRecipe) {
        ProductNode thisNode = new ProductNode(nodeRecipe.productName());
        graph.addVertex(thisNode);
        nodeRecipe.ingredients().forEach(ingredient -> {
            ProductRecipe ingredientRecipe = recipeSet.getRecipe(ingredient.productName());
            ProductNode ingredientNode = addProductNodesRecursive(ingredientRecipe);
            ProductEdge cost = new ProductEdge(ingredient.productAmount());
            graph.addEdge(thisNode, ingredientNode, cost);
        });
        return thisNode;
    }

    private void propagateCumulativeCost() {
        for (ProductNode node : graph) {
            double totalIncoming = getTotalIncomingWeight(node);
            graph.outgoingEdgesOf(node).forEach(e -> e.setCumulativeCost(totalIncoming * e.getUnitCost()));
        }
    }

    private double getTotalIncomingWeight(ProductNode node) {
        Set<ProductEdge> inputEdges = graph.incomingEdgesOf(node);
        if (inputEdges.isEmpty()) {
            // This is the root/output node, assume input weight of 1
            return 1;
        } else {
            return graph.incomingEdgesOf(node).stream()
                    .mapToDouble(ProductEdge::getCumulativeCost)
                    .sum();
        }
    }
}
