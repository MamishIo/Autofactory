package io.mamish.autofactory.model;

import java.util.List;

public record ProductRecipe(
        String productName,
        List<ProductAmount> ingredients
) {
}
