package tacos.data;

import tacos.Ingredient;

public interface IngredientRepository {
    Iterable<Ingredient> finaAll();
    Ingredient findOne(String id);
    Ingredient save(Ingredient ingredient);
}
