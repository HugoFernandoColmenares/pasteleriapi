package com.pastryapi.service;

import com.pastryapi.dto.IngredientDto;
import com.pastryapi.dto.PageResponse;

import java.util.UUID;

public interface IngredientService {
    IngredientDto createIngredient(IngredientDto ingredientDto);

    IngredientDto getIngredientById(UUID id);

    IngredientDto updateIngredient(UUID id, IngredientDto ingredientDto);

    void deleteIngredient(UUID id);

    PageResponse<IngredientDto> search(String name, Boolean status, Boolean lowStock, int page, int size);
}
