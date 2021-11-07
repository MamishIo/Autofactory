package io.mamish.autofactory.recipe;

import java.io.IOException;

public class RecipeLoader {

    private static final String RECIPE_RESOURCE_FORMAT = "recipes/%s.lua";

    private final RecipeSet loadedRecipeSet;

    public RecipeLoader(String version) {
        try (var recipeFile = getClass().getClassLoader().getResourceAsStream(getRecipeResourceName(version))) {
            if (recipeFile == null) {
                throw new IllegalArgumentException("Resource file not found: " + getRecipeResourceName(version));
            }
            LuaRecipeFileParser parser = new LuaRecipeFileParser(recipeFile);
            loadedRecipeSet = parser.getRecipeSet();
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid version", e);
        }
    }

    public RecipeSet getLoadedRecipeSet() {
        return loadedRecipeSet;
    }

    private static String getRecipeResourceName(String version) {
        return RECIPE_RESOURCE_FORMAT.formatted(version);
    }
}
