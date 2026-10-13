package com.pastryapi.repository;

import com.pastryapi.model.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);

    Optional<Ingredient> findById(UUID id);

    Ingredient update(Ingredient ingredient);

    void deleteById(UUID id);

    long count(String name, Boolean status, Boolean lowStock);

    List<Ingredient> search(String name, Boolean status, Boolean lowStock, int offset, int limit);

    boolean existsByNameIgnoreCase(String name);
}
