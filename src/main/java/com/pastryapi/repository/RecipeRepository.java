package com.pastryapi.repository;

import com.pastryapi.model.Recipe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository {
    Recipe save(Recipe recipe);

    Optional<Recipe> findById(UUID id);

    Recipe update(Recipe recipe);

    void deleteById(UUID id);

    long count();

    List<Recipe> findPage(int offset, int limit);

    boolean existsByNameIgnoreCase(String name);

    boolean isIngredientUsed(UUID ingredientId);

    void decrementIngredientQuantity(UUID ingredientId, double amount);
}
