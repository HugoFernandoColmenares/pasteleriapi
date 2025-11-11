package com.pastryapi.service;

import com.pastryapi.dto.IngredientDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredientService {
    IngredientDto createIngredient(IngredientDto ingredientDto);
    List<IngredientDto> getAllIngredients();
    Optional<IngredientDto> getIngredientById(UUID id);
    IngredientDto updateIngredient(UUID id, IngredientDto ingredientDto);
    void deleteIngredient(UUID id);
}
