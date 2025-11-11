package com.pastryapi.repository;

import com.pastryapi.model.Ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientRepository {
    Ingredient save(Ingredient ingredient);
    List<Ingredient> findAll();
    Optional<Ingredient> findById(UUID id);
    Ingredient update(Ingredient ingredient);
    void deleteById(UUID id);
}
