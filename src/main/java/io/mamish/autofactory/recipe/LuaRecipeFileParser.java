package io.mamish.autofactory.recipe;

import io.mamish.autofactory.model.ProductAmount;
import org.luaj.vm2.ast.Exp;
import org.luaj.vm2.ast.SyntaxElement;
import org.luaj.vm2.ast.TableConstructor;
import org.luaj.vm2.ast.TableField;
import org.luaj.vm2.parser.LuaParser;
import org.luaj.vm2.parser.ParseException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LuaRecipeFileParser {

    private final RecipeSet recipeSet = new RecipeSet();

    public LuaRecipeFileParser(InputStream fileStream) {
        LuaParser parser = new LuaParser(fileStream);
        try {
            Exp.MethodCall methodCall = parseAs(parser.PrimaryExp(), Exp.MethodCall.class,
                    "First token not a method call");
            Exp.NameExp dataName = parseAs(methodCall.lhs, Exp.NameExp.class,
                    "LHS of method call isn't a name");
            validate(dataName.name.name.equals("data"), "method target isn't <data>");
            validate(methodCall.name.equals("extend"), "method name isn't <extend>");
            validate(!methodCall.args.exps.isEmpty(), "empty method call args");
            TableConstructor recipeTable = parseAs((SyntaxElement) methodCall.args.exps.get(0), TableConstructor.class,
                    "extend argument isn't a TableConstructor");
            for (var f: recipeTable.fields) {
                parseRecipe((TableField) f);
            }
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse recipe file", e);
        }
    }

    public RecipeSet getRecipeSet() {
        return recipeSet;
    }

    private void parseRecipe(TableField field) throws ParseException{
        validate(field.name == null, "field is named");
        validate(field.index == null, "field is indexed");
        TableConstructor itemConstructor = parseAs(field.rhs, TableConstructor.class, "field isn't a table");
        ProductRecipeBuilder recipeBuilder = new ProductRecipeBuilder();
        for (var f: itemConstructor.fields) {
            parseRecipeField((TableField) f, recipeBuilder);
        }
        recipeSet.putRecipe(recipeBuilder.build());
    }

private void parseRecipeField(TableField field, ProductRecipeBuilder recipe) throws ParseException {
    validate(field.name != null, "field is anonymous");
    validate(field.index == null, "field is indexed");

    switch (field.name) {
        case "type" -> {
            Exp.Constant typeConst = parseAs(field.rhs, Exp.Constant.class, "type not a constant string");
            validate(typeConst.value.tojstring().equals("recipe"), "object type isn't <recipe>");
        }
        case "name" -> {
            Exp.Constant nameConst = parseAs(field.rhs, Exp.Constant.class, "name not a constant string");
            String name = nameConst.value.tojstring();
            recipe.setName(name);
        }
        case "ingredients" -> {
            TableConstructor ingredientsTable = parseAs(field.rhs, TableConstructor.class, "ingredients isn't a table");
            List<TableField> ingredientsFields = (List<TableField>) ingredientsTable.fields;
            List<ProductAmount> ingredients = new ArrayList<>(ingredientsFields.size());
            for (var f: ingredientsFields) {
                ingredients.add(parseRecipeIngredient(f));
            }
            recipe.setIngredients(ingredients);
        }
    }
}

    private ProductAmount parseRecipeIngredient(TableField field) throws ParseException {
        validate(field.name == null, "field is named");
        validate(field.index == null, "field is indexed");
        TableConstructor componentsTable = parseAs(field.rhs, TableConstructor.class, "ingredient not a table");
        List<TableField> components = (List<TableField>) componentsTable.fields;
        TableField f0 = components.get(0);
        if (f0.name == null) {
            return parseRecipeIngredientAnonymous(components);
        } else {
            validate(f0.name.equals("type"), "first ingredient component not null or <type>");
            String type = parseAs(f0.rhs, Exp.Constant.class, "type field isn't a constant").value.tojstring();
            return switch (type) {
                case "item" -> parseRecipeIngredientItem(components);
                case "fluid" -> parseRecipeIngredientFluid(components);
                default -> throw new ParseException("ingredient type is <" + type + ">, expected (<fluid>|<item>)");
            };
        }
    }

    private ProductAmount parseRecipeIngredientAnonymous(List<TableField> components) throws ParseException {
        return parseRecipeIngredientComponents(components, 2, 2, false, 0, 1);
    }

    private ProductAmount parseRecipeIngredientItem(List<TableField> components) throws ParseException {
        return parseRecipeIngredientComponents(components, 3, 3, true, 1, 2);
    }

    private ProductAmount parseRecipeIngredientFluid(List<TableField> components) throws ParseException {
        // 3 or 4 components: `fluidbox_index` may or may not be present
        return parseRecipeIngredientComponents(components, 3, 4, true, 1, 2);
    }

    private ProductAmount parseRecipeIngredientComponents(List<TableField> components,
                                                          int expectMinComponents, int expectMaxComponents, boolean expectNamed,
                                                          int nameIndex, int amountIndex) throws ParseException{

        validate(components.size() >= expectMinComponents && components.size() <= expectMaxComponents,
                "ingredient has %d elements, expected %d..%d".formatted(components.size(), expectMinComponents, expectMaxComponents));

        TableField nameComponent = components.get(nameIndex);
        TableField amountComponent = components.get(amountIndex);

        String expectNameKey = expectNamed ? "name": null;
        String expectAmountKey = expectNamed ? "amount": null;
        validate(Objects.equals(nameComponent.name, expectNameKey),
                "name component has key <%s>, expected <%s>".formatted(nameComponent.name, expectNameKey));
        validate(Objects.equals(amountComponent.name, expectAmountKey),
                "amount component has key <%s>, expected <%s>".formatted(amountComponent.name, expectAmountKey));

        String name = parseAs(nameComponent.rhs, Exp.Constant.class, "ingredient name not a constant").value.tojstring();
        double amount = parseAs(amountComponent.rhs, Exp.Constant.class, "ingredient amount not a constant").value.checkdouble();

        return new ProductAmount(name, amount);
    }

    private static <T> T parseAs(SyntaxElement element, Class<T> targetClass, String errorMessage) throws ParseException {
        Objects.requireNonNull(element);
        try {
            return targetClass.cast(element);
        } catch (ClassCastException e) {
            throw new ParseException(errorMessage + ": " + e.getMessage());
        }
    }

    private static void validate(boolean condition, String errorMessage) throws ParseException {
        if (!condition) {
            throw new ParseException(errorMessage);
        }
    }
}
